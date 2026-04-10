package com.gastromind.api.application.usecases;

import com.gastromind.api.application.services.UsualPurchaseQuantityMath;
import com.gastromind.api.application.services.TicketQuantityUnitResolver;
import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.ports.in.IFridgeItemService;
import com.gastromind.api.domain.ports.out.FridgeRepository;
import com.gastromind.api.domain.ports.out.TicketPurchaseHistoryLine;
import com.gastromind.api.domain.ports.out.TicketPurchaseHistoryRepository;
import com.gastromind.api.domain.ports.out.UsualPurchaseRepository;
import com.gastromind.api.infrastructure.config.UsualPurchaseProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Sugerencias de compra habituales a partir del historial de tickets de todo el hogar
 * (propietario y miembros) y stock actual en nevera.
 */
@Service
public class ListUsualPurchaseSuggestionsUseCase {

    private final ResolveAuthenticatedHouseholdContextUseCase resolveHouseholdContext;
    private final TicketPurchaseHistoryRepository ticketHistory;
    private final FridgeRepository fridgeRepository;
    private final IFridgeItemService fridgeItemService;
    private final UsualPurchaseRepository usualPurchaseRepository;
    private final UsualPurchaseProperties properties;

    public ListUsualPurchaseSuggestionsUseCase(
            ResolveAuthenticatedHouseholdContextUseCase resolveHouseholdContext,
            TicketPurchaseHistoryRepository ticketHistory,
            FridgeRepository fridgeRepository,
            IFridgeItemService fridgeItemService,
            UsualPurchaseRepository usualPurchaseRepository,
            UsualPurchaseProperties properties) {
        this.resolveHouseholdContext = resolveHouseholdContext;
        this.ticketHistory = ticketHistory;
        this.fridgeRepository = fridgeRepository;
        this.fridgeItemService = fridgeItemService;
        this.usualPurchaseRepository = usualPurchaseRepository;
        this.properties = properties;
    }

    public record UsualPurchaseSuggestion(
            String productId,
            String productName,
            BigDecimal targetQuantity,
            String quantityUnit,
            BigDecimal currentFridgeQuantity,
            double score,
            int distinctTicketCount,
            LocalDateTime lastPurchasedAt,
            boolean lowStock
    ) {}

    @Transactional(readOnly = true)
    public List<UsualPurchaseSuggestion> execute(String principal, boolean lowStockOnly, Integer historyDaysOverride) {
        ResolveAuthenticatedHouseholdContextUseCase.AuthenticatedHouseholdContext ctx =
                resolveHouseholdContext.execute(principal);
        String householdId = ctx.householdId();
        User currentUser = ctx.user();

        int historyDays = historyDaysOverride != null && historyDaysOverride > 0
                ? historyDaysOverride
                : properties.getHistoryDays();
        LocalDateTime since = LocalDateTime.now().minusDays(historyDays);

        if (householdId == null || householdId.isBlank()) {
            return List.of();
        }

        List<TicketPurchaseHistoryLine> lines = ticketHistory.findLinesForHouseholdSince(householdId, since);
        if (lines.isEmpty()) {
            return List.of();
        }

        Map<String, Map<String, BigDecimal>> qtyByTicketAndProduct = new HashMap<>();
        Map<String, LocalDateTime> purchaseDateByTicket = new HashMap<>();
        Map<String, String> productNames = new HashMap<>();
        Map<String, List<String>> canonicalCodesByProduct = new HashMap<>();

        for (TicketPurchaseHistoryLine line : lines) {
            String tid = line.ticketId();
            String pid = line.productId();
            productNames.putIfAbsent(pid, line.productName());
            purchaseDateByTicket.merge(tid, line.purchaseDate(), (a, b) -> a.isAfter(b) ? a : b);

            String canon = TicketQuantityUnitResolver.canonicalCodeFromDbUnitName(line.unitNameFromDb());
            canonicalCodesByProduct.computeIfAbsent(pid, k -> new ArrayList<>()).add(canon);

            BigDecimal amt = UsualPurchaseQuantityMath.toCanonicalAmount(line.quantityRaw(), canon);
            qtyByTicketAndProduct
                    .computeIfAbsent(tid, k -> new HashMap<>())
                    .merge(pid, amt, BigDecimal::add);
        }

        Map<String, List<BigDecimal>> totalsPerTicketByProduct = new HashMap<>();
        Map<String, LocalDateTime> lastPurchaseByProduct = new HashMap<>();

        for (Map.Entry<String, Map<String, BigDecimal>> e : qtyByTicketAndProduct.entrySet()) {
            String ticketId = e.getKey();
            LocalDateTime pd = purchaseDateByTicket.get(ticketId);
            for (Map.Entry<String, BigDecimal> pe : e.getValue().entrySet()) {
                String pid = pe.getKey();
                BigDecimal q = pe.getValue();
                totalsPerTicketByProduct.computeIfAbsent(pid, k -> new ArrayList<>()).add(q);
                lastPurchaseByProduct.merge(pid, pd, (a, b) -> a.isAfter(b) ? a : b);
            }
        }

        Map<String, BigDecimal> manualTargets = usualPurchaseRepository.findAllByUserId(currentUser.getId()).stream()
                .filter(up -> up.getProduct_id() != null && up.getProduct_id().getId() != null)
                .collect(Collectors.toMap(
                        up -> up.getProduct_id().getId(),
                        up -> BigDecimal.valueOf(up.getTarget_quantity()),
                        (a, b) -> b));

        Map<String, BigDecimal> fridgeByProduct = aggregateFridgeStockByProduct(householdId);

        int minTickets = Math.max(1, properties.getMinDistinctTickets());
        double halfLife = properties.getRecencyHalfLifeDays() > 0 ? properties.getRecencyHalfLifeDays() : 30.0;
        double lowFrac = properties.getLowStockFraction();

        List<UsualPurchaseSuggestion> out = new ArrayList<>();

        for (Map.Entry<String, List<BigDecimal>> e : totalsPerTicketByProduct.entrySet()) {
            String productId = e.getKey();
            List<BigDecimal> perTicketTotals = e.getValue();
            int distinct = perTicketTotals.size();
            if (distinct < minTickets) {
                continue;
            }

            BigDecimal median = UsualPurchaseQuantityMath.median(perTicketTotals);
            BigDecimal target = manualTargets.getOrDefault(productId, median);

            String presentationUnit = dominantUnit(canonicalCodesByProduct.get(productId));
            BigDecimal fridgeQty = fridgeByProduct.getOrDefault(productId, BigDecimal.ZERO);

            boolean low = fridgeQty.compareTo(target.multiply(BigDecimal.valueOf(lowFrac))) < 0;

            LocalDateTime lastAt = lastPurchaseByProduct.get(productId);
            long daysSince = lastAt != null
                    ? ChronoUnit.DAYS.between(lastAt.toLocalDate(), LocalDate.now())
                    : 9999L;
            double decay = Math.exp(-daysSince / halfLife);
            double score = distinct * decay;

            out.add(new UsualPurchaseSuggestion(
                    productId,
                    productNames.getOrDefault(productId, ""),
                    target.setScale(4, java.math.RoundingMode.HALF_UP),
                    presentationUnit,
                    fridgeQty.setScale(4, java.math.RoundingMode.HALF_UP),
                    score,
                    distinct,
                    lastAt,
                    low));
        }

        out.sort(Comparator.comparingDouble(UsualPurchaseSuggestion::score).reversed());

        if (lowStockOnly) {
            return out.stream().filter(UsualPurchaseSuggestion::lowStock).toList();
        }
        return out;
    }

    private static String dominantUnit(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return "ud";
        }
        Set<String> set = codes.stream().collect(Collectors.toSet());
        boolean mass = set.stream().anyMatch(c -> "g".equals(c) || "kg".equals(c));
        boolean vol = set.stream().anyMatch(c -> "ml".equals(c) || "l".equals(c));
        if (mass) {
            return "kg";
        }
        if (vol) {
            return "l";
        }
        return "ud";
    }

    private Map<String, BigDecimal> aggregateFridgeStockByProduct(String householdId) {
        Map<String, BigDecimal> byProduct = new LinkedHashMap<>();
        fridgeRepository.findByHouseholdId(householdId).forEach(fridge -> {
            List<FridgeItem> items = fridgeItemService.findByFridgeId(fridge.getId());
            for (FridgeItem item : items) {
                if (item.getStatus() != null) {
                    String sn = item.getStatus().name();
                    if ("EXPIRED".equals(sn) || "CONSUMED".equals(sn)) {
                        continue;
                    }
                }
                Product p = item.getProduct();
                if (p == null || p.getId() == null) {
                    continue;
                }
                BigDecimal q = item.getQuantity() != null ? item.getQuantity() : BigDecimal.ZERO;
                byProduct.merge(p.getId(), q, BigDecimal::add);
            }
        });
        return byProduct;
    }
}
