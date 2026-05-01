package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.models.Ticket;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.TicketItem;
import com.gastromind.api.domain.models.Unit;
import com.gastromind.api.domain.models.enums.TicketLineVerificationStatus;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.ports.out.ProductRepository;
import com.gastromind.api.domain.ports.out.TicketRepository;
import com.gastromind.api.domain.ports.out.UnitRepository;
import com.gastromind.api.domain.ports.out.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private TicketRepository repository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UnitRepository unitRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UsualPurchaseTicketSyncService usualPurchaseTicketSyncService;

    @InjectMocks
    private TicketServiceImpl service;

    private Ticket existing;
    private Unit defaultUd;
    private Unit otherUnit;
    private Product fullProduct;

    @BeforeEach
    void setUp() {
        lenient().when(userRepository.findById(anyString())).thenReturn(Optional.empty());
        existing = new Ticket("t-1");
        existing.setUser_id(new User("owner-1"));
        defaultUd = new Unit("u-def", "Unidades");
        otherUnit = new Unit("u-kg", "Kg");
        fullProduct = new Product("p-1");
        fullProduct.setName("Leche");
    }

    @Test
    void findAll_delegates() {
        when(repository.findAll()).thenReturn(List.of(existing));
        assertEquals(List.of(existing), service.findAll());
    }

    @Test
    void findAllByUserId_delegates() {
        when(repository.findAllByUserId("owner-1")).thenReturn(List.of(existing));
        assertEquals(List.of(existing), service.findAllByUserId("owner-1"));
    }

    @Test
    void findAllVisibleForUserHousehold_delegates() {
        User member = new User("member-1");
        member.setHouseHold_id(new HouseHold("hh-1"));
        when(userRepository.findById("member-1")).thenReturn(Optional.of(member));
        when(repository.findVisibleForHousehold("hh-1")).thenReturn(List.of(existing));
        assertEquals(List.of(existing), service.findAllVisibleForUserHousehold("member-1"));
    }

    @Test
    void findByIdForHouseholdMember_whenTicketHasHousehold() {
        existing.setHouseHold_id(new HouseHold("hh-1"));
        User viewer = new User("other");
        viewer.setHouseHold_id(new HouseHold("hh-1"));
        when(repository.findById("t-1")).thenReturn(Optional.of(existing));
        when(userRepository.findById("other")).thenReturn(Optional.of(viewer));
        assertSame(existing, service.findByIdForHouseholdMember("t-1", "other"));
    }

    @Test
    void findByIdForUser_returnsWhenOwner() {
        when(repository.findById("t-1")).thenReturn(Optional.of(existing));
        assertSame(existing, service.findByIdForUser("t-1", "owner-1"));
    }

    @Test
    void findByIdForUser_throwsWhenNotOwner() {
        when(repository.findById("t-1")).thenReturn(Optional.of(existing));
        assertThrows(ForbiddenException.class, () -> service.findByIdForUser("t-1", "other"));
    }

    @Test
    void findById_throwsWhenMissing() {
        when(repository.findById("x")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findById("x"));
    }

    @Test
    void findById_returnsWhenPresent() {
        when(repository.findById("t-1")).thenReturn(Optional.of(existing));
        assertEquals(existing, service.findById("t-1"));
    }

    @Test
    void create_withNoItems_savesDirectly() {
        Ticket t = new Ticket();
        t.setItems(null);
        when(repository.save(t)).thenReturn(existing);
        assertEquals(existing, service.create(t));
        verify(repository).save(t);
        verify(usualPurchaseTicketSyncService).syncAfterTicketCreated(existing);
    }

    @Test
    void create_resolvesProductsUnitsAndDefaultVerification() {
        when(unitRepository.findFirstByNameIgnoreCase("Unidades")).thenReturn(Optional.of(defaultUd));
        when(productRepository.findById("p-1")).thenReturn(Optional.of(fullProduct));
        when(unitRepository.findById("u-kg")).thenReturn(Optional.of(otherUnit));

        TicketItem line = new TicketItem();
        line.setProduct(new Product("p-1"));
        line.setQuantity(BigDecimal.ONE);
        line.setUnit(new Unit("u-kg"));

        Ticket t = new Ticket();
        t.setItems(List.of(line));

        when(repository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        Ticket saved = service.create(t);

        assertEquals(TicketLineVerificationStatus.OK, line.getVerificationStatus());
        assertEquals(fullProduct, line.getProduct());
        assertEquals(otherUnit, line.getUnit());
        verify(repository).save(saved);
        verify(usualPurchaseTicketSyncService).syncAfterTicketCreated(saved);
    }


    @Test
    void create_usesDefaultUnitWhenUnitMissing() {
        when(unitRepository.findFirstByNameIgnoreCase("Unidades")).thenReturn(Optional.of(defaultUd));
        when(productRepository.findById("p-1")).thenReturn(Optional.of(fullProduct));

        TicketItem line = new TicketItem();
        line.setProduct(new Product("p-1"));
        line.setQuantity(BigDecimal.ONE);
        line.setUnit(null);

        Ticket t = new Ticket();
        t.setItems(List.of(line));

        when(repository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        Ticket saved = service.create(t);

        assertEquals(defaultUd, line.getUnit());
        verify(usualPurchaseTicketSyncService).syncAfterTicketCreated(saved);
    }

    @Test
    void create_throwsWhenProductIdMissing() {
        when(unitRepository.findFirstByNameIgnoreCase("Unidades")).thenReturn(Optional.of(defaultUd));

        TicketItem line = new TicketItem();
        line.setProduct(new Product(null));

        Ticket t = new Ticket();
        t.setItems(List.of(line));

        assertThrows(IllegalArgumentException.class, () -> service.create(t));
    }

    @Test
    void create_acceptsLineProductNameWithoutCatalogProduct() {
        when(unitRepository.findFirstByNameIgnoreCase("Unidades")).thenReturn(Optional.of(defaultUd));

        TicketItem line = new TicketItem();
        line.setLineProductName("Yogur sin catalogo");
        line.setQuantity(BigDecimal.ONE);
        line.setUnit(null);

        Ticket t = new Ticket();
        t.setItems(List.of(line));

        when(repository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        Ticket saved = service.create(t);

        assertEquals("Yogur sin catalogo", line.getLineProductName());
        assertEquals(defaultUd, line.getUnit());
        verify(usualPurchaseTicketSyncService).syncAfterTicketCreated(saved);
    }

    @Test
    void create_throwsWhenDefaultUnitMissing() {
        when(unitRepository.findFirstByNameIgnoreCase("Unidades")).thenReturn(Optional.empty());

        TicketItem line = new TicketItem();
        line.setProduct(new Product("p-1"));

        Ticket t = new Ticket();
        t.setItems(List.of(line));

        assertThrows(NotFoundException.class, () -> service.create(t));
    }

    @Test
    void update_delegatesWithResolve() {
        when(repository.findById("t-1")).thenReturn(Optional.of(existing));
        when(unitRepository.findFirstByNameIgnoreCase("Unidades")).thenReturn(Optional.of(defaultUd));
        when(productRepository.findById("p-1")).thenReturn(Optional.of(fullProduct));

        TicketItem line = new TicketItem();
        line.setProduct(new Product("p-1"));
        Ticket patch = new Ticket();
        patch.setItems(List.of(line));

        when(repository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        Ticket out = service.update("t-1", patch);

        assertEquals("t-1", out.getId());
        verify(repository).save(patch);
    }

    @Test
    void delete_verifiesExistsThenDeletes() {
        when(repository.findById("t-1")).thenReturn(Optional.of(existing));
        service.delete("t-1");
        verify(repository).deleteById(eq("t-1"));
    }

    @Test
    void deleteForUser_verifiesOwnerThenDeletes() {
        when(repository.findById("t-1")).thenReturn(Optional.of(existing));
        service.deleteForUser("t-1", "owner-1");
        verify(repository).deleteById(eq("t-1"));
    }

    @Test
    void updateForUser_keepsOwnerUser() {
        when(repository.findById("t-1")).thenReturn(Optional.of(existing));
        when(unitRepository.findFirstByNameIgnoreCase("Unidades")).thenReturn(Optional.of(defaultUd));
        when(productRepository.findById("p-1")).thenReturn(Optional.of(fullProduct));

        TicketItem line = new TicketItem();
        line.setProduct(new Product("p-1"));
        Ticket patch = new Ticket();
        patch.setItems(List.of(line));

        when(repository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        Ticket out = service.updateForUser("t-1", patch, "owner-1");

        assertEquals("t-1", out.getId());
        assertEquals("owner-1", out.getUser_id().getId());
        verify(repository).save(patch);
    }
}
