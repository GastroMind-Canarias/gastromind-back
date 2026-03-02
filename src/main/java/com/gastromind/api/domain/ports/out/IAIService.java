package com.gastromind.api.domain.ports.out;

import java.util.List;

public interface IAIService {
    /**
     * Sugiere una receta basada en una lista de nombres de ingredientes.
     */
    String suggestRecipe(java.util.List<java.util.Map<String, Object>> ingredients, java.util.List<String> appliances,
            java.util.List<String> allergens);

    /**
     * Analiza el texto de un ticket y extrae información estructurada (simulado por
     * ahora).
     */
    String analyzeTicket(String ticketText);
}
