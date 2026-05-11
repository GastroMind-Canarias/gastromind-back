package com.gastromind.api.application.usecases;

import com.gastromind.api.application.services.FridgeItemServiceImpl;
import com.gastromind.api.application.services.TicketProductResolutionService;
import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ItemStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
/**
 * Caso de uso para crear un nuevo item en la nevera del usuario autenticado.
 */
public class CreateMyFridgeItemUseCase {

    private final ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase;
    private final FridgeItemServiceImpl fridgeItemService;
    private final TicketProductResolutionService ticketProductResolutionService;

    public CreateMyFridgeItemUseCase(
            ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase,
            FridgeItemServiceImpl fridgeItemService,
            TicketProductResolutionService ticketProductResolutionService
    ) {
        this.resolveAuthenticatedHouseholdContextUseCase = resolveAuthenticatedHouseholdContextUseCase;
        this.fridgeItemService = fridgeItemService;
        this.ticketProductResolutionService = ticketProductResolutionService;
    }
    /**
     * Anade un producto al inventario de la nevera del usuario autenticado.
     *
     * @param principal identificador del usuario autenticado
     * @param productId identificador del producto a anadir
     * @param productName nombre del producto para resolver o crear si no se informa productId
     * @param quantity cantidad inicial del item
     * @param expirationDate fecha de caducidad opcional
     * @param status estado inicial del item
     * @return item de nevera creado
     */

    @Transactional
    public FridgeItem execute(
            String principal,
            String productId,
            String productName,
            BigDecimal quantity,
            LocalDate expirationDate,
            ItemStatus status
    ) {
        if (!hasText(productId) && !hasText(productName)) {
            throw new IllegalArgumentException("Debes indicar productId o productName");
        }
        String fridgeId = resolveAuthenticatedHouseholdContextUseCase.execute(principal).fridge().getId();
        String resolvedProductId = resolveProductId(productId, productName);
        return fridgeItemService.addProductToFridge(fridgeId, resolvedProductId, quantity, expirationDate, status);
    }

    private String resolveProductId(String productId, String productName) {
        if (hasText(productId)) {
            return productId.trim();
        }
        return ticketProductResolutionService.resolveOrCreateProductFromManualEntry(productName).getId();
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}




