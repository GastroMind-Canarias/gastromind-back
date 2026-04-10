package com.gastromind.api.application.usecases;

import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.ports.in.IFridgeItemService;
import com.gastromind.api.domain.ports.out.FridgeRepository;
import com.gastromind.api.domain.ports.out.TicketPurchaseHistoryLine;
import com.gastromind.api.domain.ports.out.TicketPurchaseHistoryRepository;
import com.gastromind.api.domain.ports.out.UsualPurchaseRepository;
import com.gastromind.api.infrastructure.config.UsualPurchaseProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListUsualPurchaseSuggestionsUseCaseTest {

    @Mock
    private ResolveAuthenticatedHouseholdContextUseCase resolveContext;
    @Mock
    private TicketPurchaseHistoryRepository ticketHistory;
    @Mock
    private FridgeRepository fridgeRepository;
    @Mock
    private IFridgeItemService fridgeItemService;
    @Mock
    private UsualPurchaseRepository usualPurchaseRepository;

    private ListUsualPurchaseSuggestionsUseCase useCase;
    private UsualPurchaseProperties props;

    @BeforeEach
    void setUp() {
        props = new UsualPurchaseProperties();
        props.setMinDistinctTickets(2);
        props.setLowStockFraction(0.3);
        props.setRecencyHalfLifeDays(30);
        props.setHistoryDays(90);
        useCase = new ListUsualPurchaseSuggestionsUseCase(
                resolveContext,
                ticketHistory,
                fridgeRepository,
                fridgeItemService,
                usualPurchaseRepository,
                props);
    }

    @Test
    void aggregatesHouseholdTicketsAndFlagsLowStock() {
        User owner = new User("owner-1");
        owner.setHouseHold_id(new HouseHold("hh-1"));
        Fridge fridge = new Fridge("fridge-1");
        when(resolveContext.execute(eq("alice")))
                .thenReturn(new ResolveAuthenticatedHouseholdContextUseCase.AuthenticatedHouseholdContext(
                        owner, "hh-1", fridge));

        LocalDateTime t0 = LocalDateTime.now().minusDays(2);
        List<TicketPurchaseHistoryLine> lines = List.of(
                new TicketPurchaseHistoryLine("p1", "Leche", "ticket-a", t0, BigDecimal.ONE, "Unidades"),
                new TicketPurchaseHistoryLine("p1", "Leche", "ticket-b", t0.minusDays(1), BigDecimal.ONE, "Unidades"));
        when(ticketHistory.findLinesForHouseholdSince(eq("hh-1"), any(LocalDateTime.class)))
                .thenReturn(lines);

        when(usualPurchaseRepository.findAllByUserId("owner-1")).thenReturn(List.of());
        when(fridgeRepository.findByHouseholdId("hh-1")).thenReturn(List.of(fridge));
        when(fridgeItemService.findByFridgeId("fridge-1")).thenReturn(List.of());

        List<ListUsualPurchaseSuggestionsUseCase.UsualPurchaseSuggestion> out =
                useCase.execute("alice", false, null);

        assertEquals(1, out.size());
        assertEquals("p1", out.get(0).productId());
        assertTrue(out.get(0).lowStock());
    }
}
