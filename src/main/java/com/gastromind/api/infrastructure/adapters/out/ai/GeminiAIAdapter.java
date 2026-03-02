package com.gastromind.api.infrastructure.adapters.out.ai;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.gastromind.api.domain.ports.out.IAIService;

@Component
public class GeminiAIAdapter implements IAIService {

    private final RestTemplate restTemplate;

    @Value("${GEMINI_API_KEY:}")
    private String apiKey;

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    public GeminiAIAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String suggestRecipe(java.util.List<java.util.Map<String, Object>> ingredients,
            java.util.List<String> appliances, java.util.List<String> allergens) {
        if (ingredients == null || ingredients.isEmpty()) {
            return "{\"error\": \"No tienes ingredientes suficientes para una receta sugerida.\"}";
        }

        StringBuilder ingredientsPrompt = new StringBuilder();
        for (java.util.Map<String, Object> item : ingredients) {
            ingredientsPrompt.append(String.format("- %s (cantidad: %s)\n",
                    item.get("name"), item.get("quantity")));
        }

        String prompt = String.format(
                "Eres un Chef Profesional y Nutricionista experto. Genera UNA receta creativa y realista " +
                        "usando ÚNICAMENTE los ingredientes disponibles en la nevera del usuario.\n\n" +
                        "### REGLAS OBLIGATORIAS:\n" +
                        "1. NO incluyas ingredientes que estén en la lista de ALÉRGENOS.\n" +
                        "2. El electrodoméstico de la receta DEBE coincidir con los disponibles en el hogar (si hay alguno).\n"
                        +
                        "3. Responde EXCLUSIVAMENTE con un objeto JSON válido. SIN texto adicional, SIN bloques markdown (no uses ```), SIN comentarios.\n"
                        +
                        "4. Todos los campos del JSON son OBLIGATORIOS. No dejes ninguno vacío.\n\n" +
                        "### ESQUEMA JSON EXACTO QUE DEBES DEVOLVER:\n" +
                        "{\n" +
                        "  \"title\": \"Nombre creativo del plato (string)\",\n" +
                        "  \"description\": \"Descripción atractiva de 1-2 frases resaltando el sabor y los ingredientes principales (string)\",\n"
                        +
                        "  \"instructions\": \"Pasos de preparación numerados, separados por saltos de línea. Mínimo 4 pasos detallados (string)\",\n"
                        +
                        "  \"servings\": 4,\n" +
                        "  \"prep_time\": 30,\n" +
                        "  \"difficulty\": \"EASY\",\n" +
                        "  \"appliance_needed\": \"VITROCERAMICA\",\n" +
                        "  \"calories\": 450,\n" +
                        "  \"image_url\": \"\"\n" +
                        "}\n\n" +
                        "### VALORES ACEPTADOS PARA CADA CAMPO ENUM:\n" +
                        "- difficulty: EASY, MEDIUM, HARD\n" +
                        "- appliance_needed: HORNO, MICROONDAS, AIR_FRYER, VITROCERAMICA, ROBOT_COCINA, BATIDORA, SARTEN\n\n"
                        +
                        "### DATOS DE LA NEVERA:\n" +
                        "Ingredientes disponibles:\n%s\n" +
                        "Electrodomésticos disponibles: %s\n" +
                        "Alérgenos a evitar: %s",
                ingredientsPrompt.toString(),
                (appliances == null || appliances.isEmpty())
                        ? "Sin restricciones (usa cualquier electrodoméstico básico)"
                        : String.join(", ", appliances),
                (allergens == null || allergens.isEmpty()) ? "Ninguno" : String.join(", ", allergens));

        String rawResponse = callGemini(prompt);
        return cleanJsonResponse(rawResponse);
    }

    /**
     * Limpia la respuesta de la IA eliminando posibles bloques de código markdown.
     */
    private String cleanJsonResponse(String raw) {
        if (raw == null || raw.isBlank())
            return "{\"error\": \"Respuesta vacía de la IA\"}";
        String cleaned = raw.trim();
        // Eliminar bloques de código markdown como ```json ... ``` o ``` ... ```
        if (cleaned.startsWith("```")) {
            int firstNewLine = cleaned.indexOf('\n');
            if (firstNewLine != -1) {
                cleaned = cleaned.substring(firstNewLine + 1);
            }
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.lastIndexOf("```")).trim();
            }
        }
        return cleaned.trim();
    }

    @Override
    public String analyzeTicket(String ticketText) {
        String prompt = "Analiza el siguiente texto de un ticket de supermercado y extrae los productos, cantidades y precios. "
                +
                "Responde solo con un JSON estructurado con los campos 'name', 'quantity' y 'price'.\n\n" + ticketText;
        return callGemini(prompt);
    }

    private String callGemini(String prompt) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "Error: API Key de Gemini no configurada.";
        }

        String url = GEMINI_URL + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Estructura del payload para Gemini API
        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> body = Map.of("contents", List.of(content));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            Map<?, ?> response = restTemplate.postForObject(url, entity, Map.class);
            if (response != null && response.containsKey("candidates")) {
                List<?> candidates = (List<?>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);
                    Map<?, ?> resContent = (Map<?, ?>) candidate.get("content");
                    List<?> parts = (List<?>) resContent.get("parts");
                    Map<?, ?> resPart = (Map<?, ?>) parts.get(0);
                    String rawResponse = (String) resPart.get("text");
                    return rawResponse != null ? rawResponse.trim() : "";
                }
            }
            return "{\"error\": \"No se pudo obtener una respuesta de la IA\"}";
        } catch (Exception e) {
            return "{\"error\": \"Error al llamar a Gemini: " + e.getMessage() + "\"}";
        }
    }
}
