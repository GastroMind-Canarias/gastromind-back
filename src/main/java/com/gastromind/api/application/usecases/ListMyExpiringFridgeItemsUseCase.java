package com.gastromind.api.application.usecases;

import com.gastromind.api.application.services.FridgeItemServiceImpl;
import com.gastromind.api.domain.models.FridgeItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
/**
 * Caso de uso para listar items próximos a caducar en la nevera del usuario.
 */
public class ListMyExpiringFridgeItemsUseCase {

    private final ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase;
    private final FridgeItemServiceImpl fridgeItemService;
    /**
     * Constructor con dependencias de contexto autenticado e inventario.
     *
     * @param resolveAuthenticatedHouseholdContextUseCase resolvedor de contexto autenticado
     * @param fridgeItemService servicio de consulta de caducidades
     */

    public ListMyExpiringFridgeItemsUseCase(
            ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase,
            FridgeItemServiceImpl fridgeItemService
    ) {
        this.resolveAuthenticatedHouseholdContextUseCase = resolveAuthenticatedHouseholdContextUseCase;
        this.fridgeItemService = fridgeItemService;
    }
    /**
     * Devuelve los items cuya caducidad está dentro del umbral indicado.
     *
     * @param principal identificador del usuario autenticado
     * @param days número de días de anticipación para considerar la caducidad
     * @return lista de items próximos a caducar
     */

    @Transactional(readOnly = true)
    public List<FridgeItem> execute(String principal, int days) {
        String fridgeId = resolveAuthenticatedHouseholdContextUseCase.execute(principal).fridge().getId();
        return fridgeItemService.getExpiringItems(fridgeId, days);
    }
}




