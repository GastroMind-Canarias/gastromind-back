package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gastromind.api.domain.models.FrequentPurchaseSuggestion;
import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.models.UsualPurchase;
import com.gastromind.api.domain.ports.in.IFrequentPurchaseService;
import com.gastromind.api.domain.ports.in.ISmartRecipeService;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.analysis.FrequentPurchaseSuggestionResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe.RecipeResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.usualpurchase.UsualPurchaseResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.RecipeRestMapper;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.UsualPurchaseRestMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "Inteligencia Artificial", description = "Endpoints potenciados por IA para sugerencias y análisis automáticos")
public class AIController {

    private final ISmartRecipeService smartRecipeService;
    private final RecipeRestMapper recipeMapper;
    private final IFrequentPurchaseService frequentPurchaseService;
    private final UsualPurchaseRestMapper usualPurchaseMapper;

    public AIController(ISmartRecipeService smartRecipeService,
            RecipeRestMapper recipeMapper,
            IFrequentPurchaseService frequentPurchaseService,
            UsualPurchaseRestMapper usualPurchaseMapper) {
        this.smartRecipeService = smartRecipeService;
        this.recipeMapper = recipeMapper;
        this.frequentPurchaseService = frequentPurchaseService;
        this.usualPurchaseMapper = usualPurchaseMapper;
    }

    // ──────────────────────────────────────────────────────────────
    // GenerarRecetasSugeridas
    // ──────────────────────────────────────────────────────────────

    @Operation(summary = "Sugerir receta por nevera", description = "GenerarRecetasSugeridas: genera una receta completa con la IA (Gemini) basada en los ingredientes de la nevera, los electrodomésticos disponibles y los alérgenos de los usuarios del hogar.")
    @ApiStandardDoc
    @GetMapping("/suggest-recipe/{fridgeId}")
    public ResponseEntity<RecipeResponse> suggestRecipe(
            @Parameter(description = "ID de la nevera", example = "fridge-abc-123") @PathVariable String fridgeId) {
        Recipe recipe = smartRecipeService.suggestRecipeForFridge(fridgeId);
        return ResponseEntity.ok(recipeMapper.toResponse(recipe));
    }

    // ──────────────────────────────────────────────────────────────
    // IdentificarComprasHabituales (AnalyzeFrequentPurchases)
    // ──────────────────────────────────────────────────────────────

    /**
     * Análisis puro: devuelve sugerencias sin persistir en la base de datos.
     * Ideal para mostrar al usuario qué productos podría añadir a sus habituales.
     */
    @Operation(summary = "Analizar compras habituales (solo análisis)", description = "IdentificarComprasHabituales: analiza el histórico de tickets del usuario para identificar los productos comprados con mayor frecuencia. "
            + "Devuelve sugerencias ordenadas por frecuencia descendente SIN persistir en la base de datos. "
            + "Parámetro 'minFrequency': número mínimo de tickets en los que debe haber aparecido el producto para ser considerado habitual (por defecto 2).")
    @ApiStandardDoc
    @GetMapping("/analyze-purchases/{userId}")
    public ResponseEntity<List<FrequentPurchaseSuggestionResponse>> analyzeFrequentPurchases(
            @Parameter(description = "ID del usuario a analizar", example = "usr-456-abc") @PathVariable String userId,
            @Parameter(description = "Frecuencia mínima de aparición en tickets (defecto: 2)", example = "2") @RequestParam(defaultValue = "2") int minFrequency) {

        List<FrequentPurchaseSuggestion> suggestions = frequentPurchaseService.analyzeFrequentPurchases(userId,
                minFrequency);

        List<FrequentPurchaseSuggestionResponse> response = suggestions.stream()
                .map(s -> new FrequentPurchaseSuggestionResponse(
                        s.getProduct().getId(),
                        s.getProduct().getName(),
                        s.getFrequency(),
                        s.getAvgQuantity(),
                        s.isAlreadyRegistered()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Análisis + persistencia: registra o actualiza las compras habituales en
     * usual_purchase.
     */
    @Operation(summary = "Identificar y registrar compras habituales", description = "IdentificarComprasHabituales (con persistencia): analiza el histórico de tickets del usuario, identifica los productos más frecuentes y los registra/actualiza en la tabla 'usual_purchase'. "
            + "Si el producto ya estaba registrado, actualiza la cantidad objetivo con la media recalculada. "
            + "Parámetro 'minFrequency': umbral mínimo de frecuencia (por defecto 2).")
    @ApiPostDoc
    @PostMapping("/analyze-purchases/{userId}/persist")
    public ResponseEntity<List<UsualPurchaseResponse>> analyzeAndPersistFrequentPurchases(
            @Parameter(description = "ID del usuario a analizar", example = "usr-456-abc") @PathVariable String userId,
            @Parameter(description = "Frecuencia mínima de aparición en tickets (defecto: 2)", example = "2") @RequestParam(defaultValue = "2") int minFrequency) {

        List<UsualPurchase> saved = frequentPurchaseService.analyzeAndPersist(userId, minFrequency);
        return ResponseEntity.ok(usualPurchaseMapper.toResponseList(saved));
    }
}
