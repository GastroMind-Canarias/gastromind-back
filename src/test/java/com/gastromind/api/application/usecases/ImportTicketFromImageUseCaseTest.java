package com.gastromind.api.application.usecases;

import com.gastromind.api.application.services.TicketQuantityUnitResolver;
import com.gastromind.api.application.services.TicketProductResolutionService;
import com.gastromind.api.application.services.StoreResolutionResult;
import com.gastromind.api.application.services.StoreResolutionService;
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
        TicketProductResolutionService productResolutionService = mock(TicketProductResolutionService.class);
        TicketQuantityUnitResolver unitResolver = mock(TicketQuantityUnitResolver.class);
        ITicketService ticketService = mock(ITicketService.class);
        UserRepository userRepository = mock(UserRepository.class);
        StoreResolutionService storeResolutionService = mock(StoreResolutionService.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        IFridgeItemService fridgeItemService = mock(IFridgeItemService.class);
        ImportTicketFromImageUseCase useCase = new ImportTicketFromImageUseCase(
                extraction, productResolutionService, unitResolver, ticketService, userRepository, storeResolutionService,
                fridgeRepository, fridgeItemService);

        when(userRepository.findById("user-1")).thenReturn(Optional.of(new User("user-1")));
        Store store = new Store();
        store.setId("store-1");
        when(storeResolutionService.resolve("store-1", "Store X")).thenReturn(new StoreResolutionResult(store, null, "Store X"));

        ExtractedTicketLine known = new ExtractedTicketLine("Tomate", new BigDecimal("0"), "kg", null, new BigDecimal("4.00"), false, null);
        ExtractedTicketLine unknown = new ExtractedTicketLine("Pan", new BigDecimal("500"), "g", null, new BigDecimal("1.50"), true, "  OCR dudoso ");
        when(extraction.extractFromImage(any(), eq("image/png"))).thenReturn(new ExtractedTicketReceipt(
                "Store X", LocalDate.of(2026, 4, 1), BigDecimal.ZERO, List.of(known, unknown)));

        Product catalogProduct = new Product();
        catalogProduct.setId("prod-1");
        Product provisional = new Product();
        provisional.setId("prod-provisional");
        provisional.setNeedsReview(true);
        when(productResolutionService.resolveOrCreateProduct("Tomate")).thenReturn(catalogProduct);
        when(productResolutionService.resolveOrCreateProduct("Pan")).thenReturn(provisional);
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

        Ticket saved = useCase.execute(new byte[]{1, 2}, "image/png", "user-1", "store-1").ticket();

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
        verify(fridgeItemService).addProductToFridge("fridge-1", "prod-provisional", new BigDecimal("500"), null, ItemStatus.GOOD);
    }

    @Test
    void execute_shouldUseStoreNameFallbackAndSkipFridgePushWhenNoHouseholdContext() {
        TicketExtractionPort extraction = mock(TicketExtractionPort.class);
        TicketProductResolutionService productResolutionService = mock(TicketProductResolutionService.class);
        TicketQuantityUnitResolver unitResolver = mock(TicketQuantityUnitResolver.class);
        ITicketService ticketService = mock(ITicketService.class);
        UserRepository userRepository = mock(UserRepository.class);
        StoreResolutionService storeResolutionService = mock(StoreResolutionService.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        IFridgeItemService fridgeItemService = mock(IFridgeItemService.class);
        ImportTicketFromImageUseCase useCase = new ImportTicketFromImageUseCase(
                extraction, productResolutionService, unitResolver, ticketService, userRepository, storeResolutionService,
                fridgeRepository, fridgeItemService);

        when(userRepository.findById("user-1")).thenReturn(Optional.of(new User("user-1")));
        when(extraction.extractFromImage(any(), eq("image/png"))).thenReturn(new ExtractedTicketReceipt(
                "  Mi Tienda  ", null, new BigDecimal("9.90"),
                List.of(new ExtractedTicketLine("Leche", new BigDecimal("1"), "ud", new BigDecimal("1.20"), new BigDecimal("1.20"), false, null))));
        Store store = new Store();
        store.setId("store-by-name");
        when(storeResolutionService.resolve(null, "  Mi Tienda  ")).thenReturn(new StoreResolutionResult(store, null, "  Mi Tienda  "));
        Product provisional = new Product();
        provisional.setId("prod-leche");
        when(productResolutionService.resolveOrCreateProduct("Leche")).thenReturn(provisional);
        when(unitResolver.resolveFromAiCode("ud")).thenReturn(new Unit("u-ud", "ud"));
        when(ticketService.create(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        Ticket out = useCase.execute(new byte[]{3}, "image/png", "user-1", null).ticket();

        assertEquals("store-by-name", out.getStore_id().getId());
        verify(fridgeRepository, never()).findFirstByHouseholdId(any());
        verify(fridgeItemService, never()).addProductToFridge(any(), any(), any(), any(), any());
    }

    @Test
    void execute_shouldFail_whenUserOrStoreOrProductNameInvalid() {
        TicketExtractionPort extraction = mock(TicketExtractionPort.class);
        TicketProductResolutionService productResolutionService = mock(TicketProductResolutionService.class);
        TicketQuantityUnitResolver unitResolver = mock(TicketQuantityUnitResolver.class);
        ITicketService ticketService = mock(ITicketService.class);
        UserRepository userRepository = mock(UserRepository.class);
        StoreResolutionService storeResolutionService = mock(StoreResolutionService.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        IFridgeItemService fridgeItemService = mock(IFridgeItemService.class);
        ImportTicketFromImageUseCase useCase = new ImportTicketFromImageUseCase(
                extraction, productResolutionService, unitResolver, ticketService, userRepository, storeResolutionService,
                fridgeRepository, fridgeItemService);

        when(userRepository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> useCase.execute(new byte[]{1}, "image/png", "missing", "store-1"));

        when(userRepository.findById("user-1")).thenReturn(Optional.of(new User("user-1")));
        when(storeResolutionService.resolve("store-404", "Store")).thenThrow(new NotFoundException("Tienda no encontrada"));
        when(extraction.extractFromImage(any(), eq("image/png"))).thenReturn(new ExtractedTicketReceipt(
                "Store", LocalDate.now(), BigDecimal.ONE,
                List.of(new ExtractedTicketLine("Leche", BigDecimal.ONE, "ud", BigDecimal.ONE, BigDecimal.ONE, false, null))));
        assertThrows(NotFoundException.class, () -> useCase.execute(new byte[]{1}, "image/png", "user-1", "store-404"));

        when(storeResolutionService.resolve("store-1", "Store")).thenReturn(new StoreResolutionResult(new Store("store-1", "s"), null, "Store"));
        when(extraction.extractFromImage(any(), eq("image/png"))).thenReturn(new ExtractedTicketReceipt(
                "Store", LocalDate.now(), BigDecimal.ONE,
                List.of(new ExtractedTicketLine("Leche", BigDecimal.ONE, "ud", BigDecimal.ONE, BigDecimal.ONE, false, null))));
        when(productResolutionService.resolveOrCreateProduct("Leche"))
                .thenThrow(new IllegalArgumentException("Nombre de producto vacio en una linea del ticket"));
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(new byte[]{1}, "image/png", "user-1", "store-1"));
    }

    @Test
    void execute_shouldFallbackTotalToApproximateItems_whenNoLineOrReceiptTotal() {
        TicketExtractionPort extraction = mock(TicketExtractionPort.class);
        TicketProductResolutionService productResolutionService = mock(TicketProductResolutionService.class);
        TicketQuantityUnitResolver unitResolver = mock(TicketQuantityUnitResolver.class);
        ITicketService ticketService = mock(ITicketService.class);
        UserRepository userRepository = mock(UserRepository.class);
        StoreResolutionService storeResolutionService = mock(StoreResolutionService.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        IFridgeItemService fridgeItemService = mock(IFridgeItemService.class);
        ImportTicketFromImageUseCase useCase = new ImportTicketFromImageUseCase(
                extraction, productResolutionService, unitResolver, ticketService, userRepository, storeResolutionService,
                fridgeRepository, fridgeItemService);

        when(userRepository.findById("user-1")).thenReturn(Optional.of(new User("user-1")));
        when(storeResolutionService.resolve("store-1", "Store")).thenReturn(new StoreResolutionResult(new Store("store-1", "s"), null, "Store"));
        when(extraction.extractFromImage(any(), eq("image/png"))).thenReturn(new ExtractedTicketReceipt(
                "Store", LocalDate.now(), BigDecimal.ZERO,
                List.of(new ExtractedTicketLine("Arroz", new BigDecimal("750"), "g", new BigDecimal("3.20"), BigDecimal.ZERO, false, null))));
        Product provisional = new Product();
        provisional.setId("prod-arroz");
        when(productResolutionService.resolveOrCreateProduct("Arroz")).thenReturn(provisional);
        when(unitResolver.resolveFromAiCode("g")).thenReturn(new Unit("g", "Gramos"));
        when(ticketService.create(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(new byte[]{9}, "image/png", "user-1", "store-1");

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketService).create(captor.capture());
        assertEquals(2.4f, captor.getValue().getTotal_amount(), 0.0001f);
    }

    @Test
    void execute_shouldReuseExistingAliasProductAcrossRepeatedImports() {
        TicketExtractionPort extraction = mock(TicketExtractionPort.class);
        TicketProductResolutionService productResolutionService = mock(TicketProductResolutionService.class);
        TicketQuantityUnitResolver unitResolver = mock(TicketQuantityUnitResolver.class);
        ITicketService ticketService = mock(ITicketService.class);
        UserRepository userRepository = mock(UserRepository.class);
        StoreResolutionService storeResolutionService = mock(StoreResolutionService.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        IFridgeItemService fridgeItemService = mock(IFridgeItemService.class);
        ImportTicketFromImageUseCase useCase = new ImportTicketFromImageUseCase(
                extraction, productResolutionService, unitResolver, ticketService, userRepository, storeResolutionService,
                fridgeRepository, fridgeItemService);

        when(userRepository.findById("user-1")).thenReturn(Optional.of(new User("user-1")));
        when(storeResolutionService.resolve("store-1", "Store")).thenReturn(new StoreResolutionResult(new Store("store-1", "s"), null, "Store"));
        when(extraction.extractFromImage(any(), eq("image/png"))).thenReturn(new ExtractedTicketReceipt(
                "Store", LocalDate.now(), new BigDecimal("3.20"),
                List.of(new ExtractedTicketLine("Tomate Pera", BigDecimal.ONE, "ud", new BigDecimal("1.60"), new BigDecimal("1.60"), false, null))));
        when(unitResolver.resolveFromAiCode("ud")).thenReturn(new Unit("u-ud", "ud"));
        Product provisional = new Product("prod-shared");
        when(productResolutionService.resolveOrCreateProduct("Tomate Pera")).thenReturn(provisional);
        when(ticketService.create(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        Ticket first = useCase.execute(new byte[]{1}, "image/png", "user-1", "store-1").ticket();
        Ticket second = useCase.execute(new byte[]{2}, "image/png", "user-1", "store-1").ticket();

        assertEquals("prod-shared", first.getItems().get(0).getProduct().getId());
        assertEquals("prod-shared", second.getItems().get(0).getProduct().getId());
    }
}
