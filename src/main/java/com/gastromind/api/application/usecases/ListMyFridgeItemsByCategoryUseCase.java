package com.gastromind.api.application.usecases;

import com.gastromind.api.application.services.FridgeItemServiceImpl;
import com.gastromind.api.domain.models.FridgeItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
/**
 * Caso de uso para listar items de la nevera filtrados por categoría.
 */
public class ListMyFridgeItemsByCategoryUseCase {

    private final ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase;
    private final FridgeItemServiceImpl fridgeItemService;
    /**
     * Constructor con servicios de contexto autenticado e inventario.
     *
     * @param resolveAuthenticatedHouseholdContextUseCase resolvedor de contexto autenticado
     * @param fridgeItemService servicio de consulta de inventario por categoría
     */

    public ListMyFridgeItemsByCategoryUseCase(
            ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase,
            FridgeItemServiceImpl fridgeItemService
    ) {
        this.resolveAuthenticatedHouseholdContextUseCase = resolveAuthenticatedHouseholdContextUseCase;
        this.fridgeItemService = fridgeItemService;
    }
    /**
     * Devuelve los items de la nevera del usuario para una categoría concreta.
     *
     * @param principal identificador del usuario autenticado
     * @param categoryId identificador de la categoría de producto
     * @return lista de items pertenecientes a la categoría
     */

    @Transactional(readOnly = true)
    public List<FridgeItem> execute(String principal, String categoryId) {
        String fridgeId = resolveAuthenticatedHouseholdContextUseCase.execute(principal).fridge().getId();
        return fridgeItemService.getInventoryByCategory(fridgeId, categoryId);
    }
}




