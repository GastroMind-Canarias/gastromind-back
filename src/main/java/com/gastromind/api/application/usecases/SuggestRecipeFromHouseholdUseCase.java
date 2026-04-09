package com.gastromind.api.application.usecases;

import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.HouseholdRecipeContext;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.models.RecipeStockLine;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Appliance;
import com.gastromind.api.domain.ports.in.IFridgeItemService;
import com.gastromind.api.domain.ports.in.IHouseHoldService;
import com.gastromind.api.domain.ports.out.FridgeRepository;
import com.gastromind.api.domain.ports.out.RecipeAiPort;
import com.gastromind.api.domain.ports.out.RecipeSuggestionCachePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SuggestRecipeFromHouseholdUseCase {

    private final IHouseHoldService houseHoldService;
    private final FridgeRepository fridgeRepository;
    private final IFridgeItemService fridgeItemService;
    private final RecipeAiPort recipeAiPort;
    private final RecipeSuggestionCachePort suggestionCache;

    public SuggestRecipeFromHouseholdUseCase(
            IHouseHoldService houseHoldService,
            FridgeRepository fridgeRepository,
            IFridgeItemService fridgeItemService,
            RecipeAiPort recipeAiPort,
            RecipeSuggestionCachePort suggestionCache) {
        this.houseHoldService = houseHoldService;
        this.fridgeRepository = fridgeRepository;
        this.fridgeItemService = fridgeItemService;
        this.recipeAiPort = recipeAiPort;
        this.suggestionCache = suggestionCache;
    }

    /**
     * @param householdId hogar del usuario autenticado
     * @param userId      id del usuario autenticado
     * @param servings    si viene null o &lt;= 0, se usa el número de miembros del hogar (mínimo 1)
     */
    @Transactional(readOnly = true)
    public SuggestRecipeResult execute(String householdId, String userId, Integer servings) {
        List<User> members = houseHoldService.listMembers(householdId);
        int memberCount = Math.max(1, members.size());
        int effectiveServings = (servings != null && servings > 0) ? servings : memberCount;

        List<RecipeStockLine> availableStock = collectAvailableStock(householdId);
        List<String> allergenNames = collectAllergenNames(members);
        List<Appliance> appliances = houseHoldService.listAppliances(householdId).stream()
                .map(ha -> ha.getAppliance())
                .collect(Collectors.toList());

        HouseholdRecipeContext context = new HouseholdRecipeContext(
                householdId,
                availableStock,
                allergenNames,
                appliances,
                effectiveServings
        );

        Recipe recipe = recipeAiPort.generateOneRecipe(context);
        String suggestionId = suggestionCache.save(householdId, userId, recipe);
        return new SuggestRecipeResult(recipe, suggestionId);
    }

    /**
     * Suma cantidades por {@code product_id} en la nevera (excluye consumido / caducado).
     */
    private List<RecipeStockLine> collectAvailableStock(String householdId) {
        Map<String, StockAgg> byProduct = new LinkedHashMap<>();
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
                if (p == null || p.getId() == null || p.getName() == null || p.getName().isBlank()) {
                    continue;
                }
                BigDecimal q = item.getQuantity() != null ? item.getQuantity() : BigDecimal.ZERO;
                String pid = p.getId();
                String pname = p.getName().trim();
                byProduct.merge(pid, new StockAgg(pname, q), (a, b) -> a.add(b));
            }
        });
        return byProduct.entrySet().stream()
                .map(e -> new RecipeStockLine(e.getKey(), e.getValue().name, e.getValue().qty))
                .toList();
    }

    private static final class StockAgg {
        final String name;
        BigDecimal qty;

        StockAgg(String name, BigDecimal qty) {
            this.name = name;
            this.qty = qty;
        }

        StockAgg add(StockAgg o) {
            this.qty = this.qty.add(o.qty);
            return this;
        }
    }

    private List<String> collectAllergenNames(List<User> members) {
        Set<String> unique = new LinkedHashSet<>();
        for (User member : members) {
            if (member.getAllergens() == null) {
                continue;
            }
            for (Allergen a : member.getAllergens()) {
                if (a != null && a.getName() != null && !a.getName().isBlank()) {
                    unique.add(a.getName().trim());
                }
            }
        }
        return new ArrayList<>(unique);
    }

    public record SuggestRecipeResult(Recipe recipe, String suggestionId) {}
}
