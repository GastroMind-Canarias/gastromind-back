package com.gastromind.api.infrastructure.adapters.out.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gastromind.api.domain.exceptions.AiRecipeException;
import com.gastromind.api.domain.models.HouseholdRecipeContext;
import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.models.RecipeStockLine;
import com.gastromind.api.domain.models.enums.Appliance;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GeminiRecipeAdapterTest {

    @Test
    void generateOneRecipe_shouldFailWhenAiNotConfigured() {
        GeminiProperties props = new GeminiProperties();
        props.setApiKey(" ");
        GeminiRecipeAdapter adapter = new GeminiRecipeAdapter(props, mock(GeminiGenerateContentClient.class), new ObjectMapper());

        AiRecipeException ex = assertThrows(AiRecipeException.class, () -> adapter.generateOneRecipe(context()));
        assertEquals("Generacion de recetas por IA no configurada (falta app.ai.gemini.api-key)", ex.getMessage());
    }

    @Test
    void generateOneRecipe_shouldParseAndClampIngredients() {
        GeminiProperties props = new GeminiProperties();
        props.setApiKey("k");
        GeminiGenerateContentClient client = mock(GeminiGenerateContentClient.class);
        GeminiRecipeAdapter adapter = new GeminiRecipeAdapter(props, client, new ObjectMapper());

        String raw = """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          {
                            "text": "{\\"title\\":\\"Receta\\",\\"instructions\\":\\"Paso 1\\",\\"servings\\":0,\\"prep_time\\":0,\\"appliance_needed\\":\\"invalid\\",\\"difficulty\\":\\"MEDIUM\\",\\"ingredients_used\\":[{\\"product_id\\":\\"p1\\",\\"quantity_used\\":999},{\\"product_id\\":\\"p2\\",\\"quantity_used\\":-2},{\\"product_id\\":\\"x\\",\\"quantity_used\\":1}]}"
                          }
                        ]
                      }
                    }
                  ]
                }
                """;
        when(client.postGenerateContent(anyString())).thenReturn(raw);

        Recipe out = adapter.generateOneRecipe(context());

        assertEquals("Receta", out.getTitle());
        assertEquals(1, out.getServings());
        assertEquals(1, out.getPrep_time());
        assertEquals(Appliance.VITROCERAMICA, out.getAppliance_needed());
        assertEquals(1, out.getIngredientsUsed().size());
        assertEquals("p1", out.getIngredientsUsed().getFirst().getProductId());
        assertEquals(new BigDecimal("2.5"), out.getIngredientsUsed().getFirst().getQuantityUsed());
    }

    @Test
    void generateOneRecipe_shouldWrapRestErrors() {
        GeminiProperties props = new GeminiProperties();
        props.setApiKey("k");
        GeminiGenerateContentClient client = mock(GeminiGenerateContentClient.class);
        GeminiRecipeAdapter adapter = new GeminiRecipeAdapter(props, client, new ObjectMapper());
        when(client.postGenerateContent(anyString())).thenThrow(new RestClientException("boom"));

        AiRecipeException ex = assertThrows(AiRecipeException.class, () -> adapter.generateOneRecipe(context()));
        assertEquals("Error al llamar a Gemini: boom", ex.getMessage());
    }

    private static HouseholdRecipeContext context() {
        return new HouseholdRecipeContext(
                "h1",
                List.of(
                        new RecipeStockLine("p1", "Tomate", new BigDecimal("2.5")),
                        new RecipeStockLine("p2", "Leche", new BigDecimal("1.0"))
                ),
                List.of("Lactosa"),
                List.of(Appliance.HORNO),
                2
        );
    }
}
