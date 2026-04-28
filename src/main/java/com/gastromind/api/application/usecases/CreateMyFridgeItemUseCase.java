package com.gastromind.api.application.usecases;

import com.gastromind.api.application.services.FridgeItemServiceImpl;
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
    /**
     * Constructor con dependencias de contexto y gestión de items de nevera.
     *
     * @param resolveAuthenticatedHouseholdContextUseCase resolvedor de contexto autenticado
     * @param fridgeItemService servicio de creación de items de nevera
     */

    public CreateMyFridgeItemUseCase(
            ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase,
            FridgeItemServiceImpl fridgeItemService
    ) {
        this.resolveAuthenticatedHouseholdContextUseCase = resolveAuthenticatedHouseholdContextUseCase;
        this.fridgeItemService = fridgeItemService;
    }
    /**
     * Añade un producto al inventario de la nevera del usuario autenticado.
     *
     * @param principal identificador del usuario autenticado
     * @param productId identificador del producto a añadir
     * @param quantity cantidad inicial del item
     * @param expirationDate fecha de caducidad opcional
     * @param status estado inicial del item
     * @return item de nevera creado
     */

    @Transactional
    public FridgeItem execute(
            String principal,
            String productId,
            BigDecimal quantity,
            LocalDate expirationDate,
            ItemStatus status
    ) {
        String fridgeId = resolveAuthenticatedHouseholdContextUseCase.execute(principal).fridge().getId();
        return fridgeItemService.addProductToFridge(fridgeId, productId, quantity, expirationDate, status);
    }
}




