package com.gastromind.api.application.usecases;

import com.gastromind.api.application.services.FridgeItemServiceImpl;
import com.gastromind.api.domain.models.ConsumeRecipeOutcome;
import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.domain.models.FridgeItemConsumeLine;
import com.gastromind.api.domain.models.RecipeIngredientUsage;
import com.gastromind.api.domain.ports.out.FridgeItemRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ItemStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Aplica el "cociné esta receta" sobre la nevera del hogar autenticado.
 * Recorre los ingredientes que la receta consumio, busca los items del producto
 * en tu nevera y los descuenta empezando por los que ya estan caducados y luego
 * por los que caducan antes. La operacion es todo o nada: si a algun ingrediente
 * le falta stock, la peticion falla sin tocar el inventario.
 *
 * Ingredientes sin {@code productId} (sal, agua, pimienta...) no se descuentan
 * pero se devuelven en {@code ignored} para que el cliente pueda mostrarlos.
 */
@Service
public class ConsumeMyFridgeItemsFromRecipeUseCase {

    private final ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase;
    private final FridgeItemRepository fridgeItemRepository;
    private final FridgeItemServiceImpl fridgeItemService;

    /**
     * Crea el use case con las dependencias necesarias para resolver hogar,
     * leer inventario y delegar el descuento transaccional.
     *
     * @param resolveAuthenticatedHouseholdContextUseCase resuelve el hogar y nevera del principal
     * @param fridgeItemRepository acceso de lectura sobre items de nevera
     * @param fridgeItemService servicio que aplica los descuentos en batch
     */
    public ConsumeMyFridgeItemsFromRecipeUseCase(
            ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase,
            FridgeItemRepository fridgeItemRepository,
            FridgeItemServiceImpl fridgeItemService
    ) {
        this.resolveAuthenticatedHouseholdContextUseCase = resolveAuthenticatedHouseholdContextUseCase;
        this.fridgeItemRepository = fridgeItemRepository;
        this.fridgeItemService = fridgeItemService;
    }

    /**
     * Ejecuta el descuento de inventario derivado de una receta cocinada.
     *
     * @param principal usuario autenticado
     * @param ingredientsUsed ingredientes a descontar; las cantidades han de ser positivas
     * @return resultado con los items afectados y los ingredientes descartados
     * @throws IllegalArgumentException si algun ingrediente no tiene stock suficiente
     */
    @Transactional
    public ConsumeRecipeOutcome execute(String principal, List<RecipeIngredientUsage> ingredientsUsed) {
        String fridgeId = resolveAuthenticatedHouseholdContextUseCase.execute(principal).fridge().getId();
        List<FridgeItem> fridgeItems = fridgeItemRepository.findByFridgeId(fridgeId);

        List<FridgeItemConsumeLine> lines = new ArrayList<>();
        List<ConsumeRecipeOutcome.IgnoredIngredient> ignored = new ArrayList<>();
        List<String> shortages = new ArrayList<>();

        for (RecipeIngredientUsage ingredient : ingredientsUsed) {
            if (ingredient == null) {
                continue;
            }
            if (ingredient.getProductId() == null || ingredient.getProductId().isBlank()) {
                ignored.add(new ConsumeRecipeOutcome.IgnoredIngredient(
                        null, ingredient.getProductName(), "Sin productId"));
                continue;
            }

            List<FridgeItem> matching = fridgeItems.stream()
                    .filter(i -> i.getProduct() != null
                            && ingredient.getProductId().equals(i.getProduct().getId()))
                    .sorted(consumeOrder())
                    .collect(Collectors.toList());

            BigDecimal remaining = ingredient.getQuantityUsed();
            for (FridgeItem item : matching) {
                if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }
                BigDecimal take = item.getQuantity().min(remaining);
                if (take.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                lines.add(new FridgeItemConsumeLine(item.getId(), take));
                remaining = remaining.subtract(take);
            }

            if (remaining.compareTo(BigDecimal.ZERO) > 0) {
                String label = ingredient.getProductName() != null && !ingredient.getProductName().isBlank()
                        ? ingredient.getProductName()
                        : ingredient.getProductId();
                shortages.add(label + " (falta " + remaining.toPlainString() + ")");
            }
        }

        if (!shortages.isEmpty()) {
            throw new IllegalArgumentException("Stock insuficiente para: " + String.join(", ", shortages));
        }

        List<FridgeItem> updated = lines.isEmpty()
                ? List.of()
                : fridgeItemService.consumePartiallyBatch(lines);

        return new ConsumeRecipeOutcome(updated, ignored);
    }

    private Comparator<FridgeItem> consumeOrder() {
        Comparator<FridgeItem> expiredFirst = Comparator.comparing(
                i -> i.getStatus() == ItemStatus.EXPIRED ? 0 : 1);
        Comparator<FridgeItem> byExpiration = Comparator.comparing(
                FridgeItem::getExpirationDate, Comparator.nullsLast(LocalDate::compareTo));
        return expiredFirst.thenComparing(byExpiration);
    }
}
