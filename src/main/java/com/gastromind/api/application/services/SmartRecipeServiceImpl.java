package com.gastromind.api.application.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.models.enums.Appliance;
import com.gastromind.api.domain.ports.in.IFridgeItemService;
import com.gastromind.api.domain.ports.in.IFridgeService;
import com.gastromind.api.domain.ports.in.IHouseHoldService;
import com.gastromind.api.domain.ports.in.ISmartRecipeService;
import com.gastromind.api.domain.ports.out.IAIService;

@Service
public class SmartRecipeServiceImpl implements ISmartRecipeService {

    private final IFridgeItemService fridgeItemService;
    private final IFridgeService fridgeService;
    private final IHouseHoldService householdService;
    private final IAIService aiService;
    private final ObjectMapper objectMapper;

    public SmartRecipeServiceImpl(IFridgeItemService fridgeItemService, IFridgeService fridgeService,
            IHouseHoldService householdService, IAIService aiService, ObjectMapper objectMapper) {
        this.fridgeItemService = fridgeItemService;
        this.fridgeService = fridgeService;
        this.householdService = householdService;
        this.aiService = aiService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Recipe suggestRecipeForFridge(String fridgeId) {
        Fridge fridge = fridgeService.findById(fridgeId);
        List<FridgeItem> inventory = fridgeItemService.findByFridgeId(fridgeId);

        if (inventory == null || inventory.isEmpty()) {
            throw new NotFoundException("Tu nevera está vacía, no puedo sugerir recetas.");
        }

        List<java.util.Map<String, Object>> ingredients = inventory.stream()
                .filter(item -> item.getQuantity() != null && item.getQuantity().doubleValue() > 0)
                .map(item -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", item.getProduct().getId());
                    map.put("name", item.getProduct().getName());
                    map.put("quantity", item.getQuantity().toString());
                    return map;
                })
                .collect(Collectors.toList());

        String householdId = fridge.getHouseHold_id() != null ? fridge.getHouseHold_id().getId() : null;
        List<String> appliances = List.of();
        List<String> allergens = List.of();

        if (householdId != null) {
            appliances = householdService.listAppliances(householdId).stream()
                    .map(a -> a.getAppliance().name())
                    .collect(Collectors.toList());

            allergens = householdService.listMembers(householdId).stream()
                    .filter(user -> user.getAllergens() != null)
                    .flatMap(user -> user.getAllergens().stream())
                    .map(allergen -> allergen.getName())
                    .distinct()
                    .collect(Collectors.toList());
        }

        String jsonResponse = aiService.suggestRecipe(ingredients, appliances, allergens);
        return parseRecipeFromJson(jsonResponse);
    }

    private Recipe parseRecipeFromJson(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);

            // Si la IA devolvió un error, lo propagamos
            if (root.has("error")) {
                throw new RuntimeException("La IA devolvió un error: " + root.get("error").asText());
            }

            Recipe recipe = new Recipe();
            recipe.setTitle(root.path("title").asText(""));
            recipe.setInstructions(root.path("instructions").asText(""));
            recipe.setServings(root.path("servings").asInt(2));
            recipe.setPrep_time(root.path("prep_time").asInt(30));
            recipe.setDifficulty(root.path("difficulty").asText("EASY"));
            recipe.setDescription(root.path("description").asText(""));
            recipe.setCalories(root.path("calories").asInt(0));
            recipe.setImage_url(root.path("image_url").asText(""));
            recipe.setCreated_at(LocalDate.now());

            // Mapear el electrodoméstico (appliance_needed)
            String applianceStr = root.path("appliance_needed").asText("VITROCERAMICA");
            try {
                recipe.setAppliance_needed(Appliance.valueOf(applianceStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                recipe.setAppliance_needed(Appliance.VITROCERAMICA);
            }

            return recipe;

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear la receta generada por la IA: " + e.getMessage(), e);
        }
    }
}
