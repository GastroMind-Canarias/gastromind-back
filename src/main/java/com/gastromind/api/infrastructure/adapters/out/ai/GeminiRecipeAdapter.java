package com.gastromind.api.infrastructure.adapters.out.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gastromind.api.domain.exceptions.AiRecipeException;
import com.gastromind.api.domain.models.HouseholdRecipeContext;
import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.models.RecipeIngredientUsage;
import com.gastromind.api.domain.models.RecipeStockLine;
import com.gastromind.api.domain.models.enums.Appliance;
import com.gastromind.api.domain.ports.out.RecipeAiPort;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
/**
 * Representa gemini recipe dentro del dominio de la aplicacion.
 */
public class GeminiRecipeAdapter implements RecipeAiPort {

    private final GeminiProperties properties;
    private final GeminiGenerateContentClient geminiClient;
    private final ObjectMapper objectMapper;
    /**
     * Constructor de gemini recipe.
     * @param properties valor a utilizar.
     * @param geminiClient valor a utilizar.
     * @param objectMapper valor a utilizar.
     */

    public GeminiRecipeAdapter(
            GeminiProperties properties,
            GeminiGenerateContentClient geminiClient,
            ObjectMapper objectMapper) {
        this.properties = properties;
        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
    }
    /**
     * Realiza generate one recipe.
     * @param context valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Recipe generateOneRecipe(HouseholdRecipeContext context) {
        if (!properties.isConfigured()) {
            throw new AiRecipeException("Generacion de recetas por IA no configurada (falta app.ai.gemini.api-key)");
        }

        String prompt = buildPrompt(context);
        String requestBody = buildRequestBody(prompt);

        try {
            String raw = geminiClient.postGenerateContent(requestBody);
            return parseRecipeResponse(raw, context);
        } catch (RestClientException e) {
            throw new AiRecipeException("Error al llamar a Gemini: " + e.getMessage(), e);
        } catch (AiRecipeException e) {
            throw e;
        } catch (Exception e) {
            throw new AiRecipeException("No se pudo interpretar la respuesta de IA", e);
        }
    }

    private String buildRequestBody(String prompt) {
        try {
            var root = objectMapper.createObjectNode();
            var contents = root.putArray("contents");
            var content = contents.addObject();
            var parts = content.putArray("parts");
            parts.addObject().put("text", prompt);
            var gen = root.putObject("generationConfig");
            gen.put("temperature", 0.6);
            gen.put("responseMimeType", "application/json");
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new AiRecipeException("Error construyendo peticion a Gemini", e);
        }
    }

    private String buildPrompt(HouseholdRecipeContext ctx) {
        String stockBlock;
        try {
            stockBlock = buildStockBlock(ctx.availableStock());
        } catch (Exception e) {
            throw new AiRecipeException("Error construyendo inventario para el prompt", e);
        }
        String allergens = ctx.allergenNamesToAvoid().isEmpty()
                ? "ninguno indicado"
                : String.join(", ", ctx.allergenNamesToAvoid());
        String appliances = ctx.availableAppliances().isEmpty()
                ? "ninguno especifico (elige el electrodomastico mas razonable)"
                : ctx.availableAppliances().stream().map(Enum::name).collect(Collectors.joining(", "));

        return """
                Eres un chef asistente. Debes responder SOLO con un JSON valido (sin markdown ni texto fuera del JSON) con exactamente estas claves y tipos:
                {
                  "title": string,
                  "instructions": string (pasos numerados o claros),
                  "servings": number (entero, raciones),
                  "prep_time": number (entero, minutos totales aproximados),
                  "appliance_needed": string (uno de: HORNO, MICROONDAS, AIR_FRYER, VITROCERAMICA, ROBOT_COCINA, BATIDORA, OLLA_EXPRESS),
                  "difficulty": string (exactamente uno de: EASY, MEDIUM, HARD),
                  "ingredients_used": array de { "product_id": string (uuid del inventario), "quantity_used": number }
                }
                Inventario del hogar (cantidades numericas en las mismas unidades que la nevera; NO puedes usar mas de quantity_available por product_id):
                %s
                Reglas:
                - Una sola receta.
                - Solo puedes incluir en ingredients_used product_id que aparezcan en el inventario anterior.
                - Para cada producto usado, quantity_used debe ser > 0 y etc quantity_available del inventario.
                - Si el inventario esta vacio, ingredients_used puede ser [] y sugiere una receta muy sencilla con ingredientes habituales (sin inventario estructurado).
                - Raciones objetivo (comensales): %d
                - Evita completamente alergenos o ingredientes que contengan: %s
                - Prioriza utensilios disponibles en el hogar: %s; el campo appliance_needed debe ser uno de la lista permitida y coherente con la receta.
                """.formatted(stockBlock, ctx.servings(), allergens, appliances);
    }

    private String buildStockBlock(List<RecipeStockLine> stock) throws java.io.IOException {
        if (stock == null || stock.isEmpty()) {
            return "(vacio si no hay lineas en nevera con cantidad disponible)";
        }
        ArrayNode arr = objectMapper.createArrayNode();
        for (RecipeStockLine line : stock) {
            ObjectNode o = objectMapper.createObjectNode();
            o.put("product_id", line.productId());
            o.put("name", line.productName());
            o.put("quantity_available", line.quantityAvailable());
            arr.add(o);
        }
        return objectMapper.writeValueAsString(arr);
    }

    private Recipe parseRecipeResponse(String rawJson, HouseholdRecipeContext context) throws Exception {
        JsonNode root = objectMapper.readTree(rawJson);
        JsonNode candidates = root.path("candidates");
        if (!candidates.isArray() || candidates.isEmpty()) {
            throw new AiRecipeException("Respuesta de Gemini sin candidatos");
        }
        String text = candidates.get(0).path("content").path("parts").get(0).path("text").asText();
        if (text.isBlank()) {
            throw new AiRecipeException("Respuesta de Gemini vacia");
        }

        JsonNode recipeJson = objectMapper.readTree(text);
        String title = recipeJson.path("title").asText();
        String instructions = recipeJson.path("instructions").asText();
        int servings = recipeJson.path("servings").asInt(2);
        int prepTime = recipeJson.path("prep_time").asInt(30);
        String applianceStr = recipeJson.path("appliance_needed").asText("VITROCERAMICA");
        String difficulty = recipeJson.path("difficulty").asText("MEDIUM");

        Appliance appliance;
        try {
            appliance = Appliance.valueOf(applianceStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            appliance = Appliance.VITROCERAMICA;
        }

        Recipe recipe = new Recipe();
        recipe.setTitle(title);
        recipe.setInstructions(instructions);
        recipe.setServings(Math.max(1, servings));
        recipe.setPrep_time(Math.max(1, prepTime));
        recipe.setAppliance_needed(appliance);
        recipe.setDifficulty(difficulty);
        recipe.setCreated_at(LocalDate.now());
        recipe.setIngredientsUsed(parseAndValidateIngredients(recipeJson.path("ingredients_used"), context));
        return recipe;
    }

    private List<RecipeIngredientUsage> parseAndValidateIngredients(JsonNode ingredientsNode,
            HouseholdRecipeContext context) {
        Map<String, RecipeStockLine> stockById = new LinkedHashMap<>();
        for (RecipeStockLine line : context.availableStock()) {
            stockById.put(line.productId(), line);
        }
        List<RecipeIngredientUsage> out = new ArrayList<>();
        if (!ingredientsNode.isArray()) {
            return out;
        }
        for (JsonNode n : ingredientsNode) {
            String pid = n.path("product_id").asText(null);
            if (pid == null || pid.isBlank()) {
                continue;
            }
            RecipeStockLine line = stockById.get(pid);
            if (line == null) {
                continue;
            }
            BigDecimal used = BigDecimal.ZERO;
            if (n.path("quantity_used").isNumber()) {
                used = n.get("quantity_used").decimalValue();
            }
            if (used.compareTo(BigDecimal.ZERO) < 0) {
                used = BigDecimal.ZERO;
            }
            BigDecimal cap = line.quantityAvailable() != null ? line.quantityAvailable() : BigDecimal.ZERO;
            if (used.compareTo(cap) > 0) {
                used = cap;
            }
            if (used.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            out.add(new RecipeIngredientUsage(pid, line.productName(), used, cap));
        }
        return out;
    }
}




