package com.gastromind.api.application.usecases;

import com.gastromind.api.application.services.TicketQuantityUnitResolver;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.models.Store;
import com.gastromind.api.domain.models.Ticket;
import com.gastromind.api.domain.models.TicketItem;
import com.gastromind.api.domain.models.Unit;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.TicketLineVerificationStatus;
import com.gastromind.api.domain.models.ticket.ExtractedTicketLine;
import com.gastromind.api.domain.models.ticket.ExtractedTicketReceipt;
import com.gastromind.api.domain.ports.in.IFridgeItemService;
import com.gastromind.api.domain.ports.in.ITicketService;
import com.gastromind.api.domain.ports.out.FridgeRepository;
import com.gastromind.api.domain.ports.out.ProductRepository;
import com.gastromind.api.domain.ports.out.StoreRepository;
import com.gastromind.api.domain.ports.out.TicketExtractionPort;
import com.gastromind.api.domain.ports.out.UserRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ItemStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ImportTicketFromImageUseCaseTest {

    @Test
    void execute_shouldBuildTicketAndPushBothCatalogAndLabeledItemsToFridge() {
        TicketExtractionPort extraction = mock(TicketExtractionPort.class);
        ProductRepository productRepository = mock(ProductRepository.class);
        TicketQuantityUnitResolver unitResolver = mock(TicketQuantityUnitResolver.class);
        ITicketService ticketService = mock(ITicketService.class);
        UserRepository userRepository = mock(UserRepository.class);
        StoreRepository storeRepository = mock(StoreRepository.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        IFridgeItemService fridgeItemService = mock(IFridgeItemService.class);
        ImportTicketFromImageUseCase useCase = new ImportTicketFromImageUseCase(
                extraction, productRepository, unitResolver, ticketService, userRepository, storeRepository, fridgeRepository, fridgeItemService);

        when(userRepository.findById("user-1")).thenReturn(Optional.of(new User("user-1")));
        Store store = new Store();
        store.setId("store-1");
        when(storeRepository.findById("store-1")).thenReturn(Optional.of(store));

        ExtractedTicketLine known = new ExtractedTicketLine("Tomate", new BigDecimal("0"), "kg", null, new BigDecimal("4.00"), false, null);
        ExtractedTicketLine unknown = new ExtractedTicketLine("Pan", new BigDecimal("500"), "g", null, new BigDecimal("1.50"), true, "  OCR dudoso ");
        when(extraction.extractFromImage(any(), eq("image/png"))).thenReturn(new ExtractedTicketReceipt(
                "Store X", LocalDate.of(2026, 4, 1), BigDecimal.ZERO, List.of(known, unknown)));

        Product catalogProduct = new Product();
        catalogProduct.setId("prod-1");
        when(productRepository.findFirstByNameIgnoreCase(anyString())).thenAnswer(inv -> {
            String name = inv.getArgument(0, String.class);
            if ("tomate".equalsIgnoreCase(name)) {
                return Optional.of(catalogProduct);
            }
            return Optional.empty();
        });
        when(unitResolver.resolveFromAiCode("kg")).thenReturn(new Unit("u-kg", "kg"));
        when(unitResolver.resolveFromAiCode("g")).thenReturn(new Unit("u-g", "g"));

        HouseHold house = new HouseHold();
        house.setId("house-1");
        Fridge fridge = new Fridge();
        fridge.setId("fridge-1");
        when(fridgeRepository.findFirstByHouseholdId("house-1")).thenReturn(Optional.of(fridge));
        when(ticketService.create(any(Ticket.class))).thenAnswer(inv -> {
            Ticket t = inv.getArgument(0);
            t.setHouseHold_id(house);
            return t;
        });

        Ticket saved = useCase.execute(new byte[]{1, 2}, "image/png", "user-1", "store-1");

        assertEquals(2, saved.getItems().size());
        TicketItem i0 = saved.getItems().get(0);
        assertEquals(new BigDecimal("1"), i0.getQuantity());
        assertEquals(new BigDecimal("4.0000"), i0.getPriceUnit());
        assertEquals(TicketLineVerificationStatus.OK, i0.getVerificationStatus());
        assertNull(i0.getLineNote());

        TicketItem i1 = saved.getItems().get(1);
        assertEquals(new BigDecimal("500"), i1.getQuantity());
        assertEquals(TicketLineVerificationStatus.PENDING_REVIEW, i1.getVerificationStatus());
        assertEquals("OCR dudoso", i1.getLineNote());
        assertEquals(5.5f, saved.getTotal_amount(), 0.0001f);

        verify(fridgeItemService).addProductToFridge("fridge-1", "prod-1", new BigDecimal("1"), null, ItemStatus.GOOD);
        verify(fridgeItemService).addLabeledItemToFridge("fridge-1", "Pan", new BigDecimal("500"), null, ItemStatus.GOOD);
    }

    @Test
    void execute_shouldUseStoreNameFallbackAndSkipFridgePushWhenNoHouseholdContext() {
        TicketExtractionPort extraction = mock(TicketExtractionPort.class);
        ProductRepository productRepository = mock(ProductRepository.class);
        TicketQuantityUnitResolver unitResolver = mock(TicketQuantityUnitResolver.class);
        ITicketService ticketService = mock(ITicketService.class);
        UserRepository userRepository = mock(UserRepository.class);
        StoreRepository storeRepository = mock(StoreRepository.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        IFridgeItemService fridgeItemService = mock(IFridgeItemService.class);
        ImportTicketFromImageUseCase useCase = new ImportTicketFromImageUseCase(
                extraction, productRepository, unitResolver, ticketService, userRepository, storeRepository, fridgeRepository, fridgeItemService);

        when(userRepository.findById("user-1")).thenReturn(Optional.of(new User("user-1")));
        when(extraction.extractFromImage(any(), eq("image/png"))).thenReturn(new ExtractedTicketReceipt(
                "  Mi Tienda  ", null, new BigDecimal("9.90"),
                List.of(new ExtractedTicketLine("Leche", new BigDecimal("1"), "ud", new BigDecimal("1.20"), new BigDecimal("1.20"), false, null))));
        Store store = new Store();
        store.setId("store-by-name");
        when(storeRepository.findFirstByNameIgnoreCase("Mi Tienda")).thenReturn(Optional.of(store));
        when(productRepository.findFirstByNameIgnoreCase("leche")).thenReturn(Optional.empty());
        when(unitResolver.resolveFromAiCode("ud")).thenReturn(new Unit("u-ud", "ud"));
        when(ticketService.create(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        Ticket out = useCase.execute(new byte[]{3}, "image/png", "user-1", null);

        assertEquals("store-by-name", out.getStore_id().getId());
        verify(fridgeRepository, never()).findFirstByHouseholdId(any());
        verify(fridgeItemService, never()).addProductToFridge(any(), any(), any(), any(), any());
    }

    @Test
    void execute_shouldFail_whenUserOrStoreOrProductNameInvalid() {
        TicketExtractionPort extraction = mock(TicketExtractionPort.class);
        ProductRepository productRepository = mock(ProductRepository.class);
        TicketQuantityUnitResolver unitResolver = mock(TicketQuantityUnitResolver.class);
        ITicketService ticketService = mock(ITicketService.class);
        UserRepository userRepository = mock(UserRepository.class);
        StoreRepository storeRepository = mock(StoreRepository.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        IFridgeItemService fridgeItemService = mock(IFridgeItemService.class);
        ImportTicketFromImageUseCase useCase = new ImportTicketFromImageUseCase(
                extraction, productRepository, unitResolver, ticketService, userRepository, storeRepository, fridgeRepository, fridgeItemService);

        when(userRepository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> useCase.execute(new byte[]{1}, "image/png", "missing", "store-1"));

        when(userRepository.findById("user-1")).thenReturn(Optional.of(new User("user-1")));
        when(storeRepository.findById("store-404")).thenReturn(Optional.empty());
        when(extraction.extractFromImage(any(), eq("image/png"))).thenReturn(new ExtractedTicketReceipt(
                "Store", LocalDate.now(), BigDecimal.ONE,
                List.of(new ExtractedTicketLine("Leche", BigDecimal.ONE, "ud", BigDecimal.ONE, BigDecimal.ONE, false, null))));
        assertThrows(NotFoundException.class, () -> useCase.execute(new byte[]{1}, "image/png", "user-1", "store-404"));

        when(storeRepository.findById("store-1")).thenReturn(Optional.of(new Store("store-1", "s")));
        when(extraction.extractFromImage(any(), eq("image/png"))).thenReturn(new ExtractedTicketReceipt(
                "Store", LocalDate.now(), BigDecimal.ONE,
                List.of(new ExtractedTicketLine("   ", BigDecimal.ONE, "ud", BigDecimal.ONE, BigDecimal.ONE, false, null))));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(new byte[]{1}, "image/png", "user-1", "store-1"));
        assertEquals("Nombre de producto vacío en una línea del ticket", ex.getMessage());
    }

    @Test
    void execute_shouldFallbackTotalToApproximateItems_whenNoLineOrReceiptTotal() {
        TicketExtractionPort extraction = mock(TicketExtractionPort.class);
        ProductRepository productRepository = mock(ProductRepository.class);
        TicketQuantityUnitResolver unitResolver = mock(TicketQuantityUnitResolver.class);
        ITicketService ticketService = mock(ITicketService.class);
        UserRepository userRepository = mock(UserRepository.class);
        StoreRepository storeRepository = mock(StoreRepository.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        IFridgeItemService fridgeItemService = mock(IFridgeItemService.class);
        ImportTicketFromImageUseCase useCase = new ImportTicketFromImageUseCase(
                extraction, productRepository, unitResolver, ticketService, userRepository, storeRepository, fridgeRepository, fridgeItemService);

        when(userRepository.findById("user-1")).thenReturn(Optional.of(new User("user-1")));
        when(storeRepository.findById("store-1")).thenReturn(Optional.of(new Store("store-1", "s")));
        when(extraction.extractFromImage(any(), eq("image/png"))).thenReturn(new ExtractedTicketReceipt(
                "Store", LocalDate.now(), BigDecimal.ZERO,
                List.of(new ExtractedTicketLine("Arroz", new BigDecimal("750"), "g", new BigDecimal("3.20"), BigDecimal.ZERO, false, null))));
        when(productRepository.findFirstByNameIgnoreCase("arroz")).thenReturn(Optional.empty());
        when(unitResolver.resolveFromAiCode("g")).thenReturn(new Unit("g", "Gramos"));
        when(ticketService.create(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(new byte[]{9}, "image/png", "user-1", "store-1");

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketService).create(captor.capture());
        assertEquals(2.4f, captor.getValue().getTotal_amount(), 0.0001f);
    }
}
