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
/**
 * Caso de uso que solicita una receta sugerida a partir del contexto del hogar.
 * Reúne stock disponible, alérgenos y electrodomésticos antes de invocar el puerto de IA.
 */
public class SuggestRecipeFromHouseholdUseCase {

    private final IHouseHoldService houseHoldService;
    private final FridgeRepository fridgeRepository;
    private final IFridgeItemService fridgeItemService;
    private final RecipeAiPort recipeAiPort;
    private final RecipeSuggestionCachePort suggestionCache;
    /**
     * Constructor con las dependencias para construir el contexto de sugerencia.
     *
     * @param houseHoldService servicio de hogares
     * @param fridgeRepository repositorio de neveras
     * @param fridgeItemService servicio de items de nevera
     * @param recipeAiPort puerto de generación de recetas
     * @param suggestionCache caché de sugerencias generadas
     */

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
     * Genera una sugerencia de receta para el hogar indicado y la guarda en caché.
     *
     * @param householdId identificador del hogar
     * @param userId identificador del usuario que solicita la sugerencia
     * @param servings raciones deseadas; si es nulo o inválido, se ajusta al tamaño del hogar
     * @return receta propuesta junto con su identificador de sugerencia
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




