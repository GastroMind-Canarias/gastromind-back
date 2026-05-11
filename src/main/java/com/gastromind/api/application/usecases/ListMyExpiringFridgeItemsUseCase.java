package com.gastromind.api.application.usecases;

import com.gastromind.api.application.services.FridgeItemServiceImpl;
import com.gastromind.api.domain.models.FridgeItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
/**
 * Caso de uso para listar items proximos a caducar en la nevera del usuario.
 */
public class ListMyExpiringFridgeItemsUseCase {

    private final ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase;
    private final FridgeItemServiceImpl fridgeItemService;

    public ListMyExpiringFridgeItemsUseCase(
            ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase,
            FridgeItemServiceImpl fridgeItemService
    ) {
        this.resolveAuthenticatedHouseholdContextUseCase = resolveAuthenticatedHouseholdContextUseCase;
        this.fridgeItemService = fridgeItemService;
    }
    /**
     * Devuelve los items cuya caducidad estA dentro del umbral indicado.
     *
     * @param principal identificador del usuario autenticado
     * @param days nAmero de dias de anticipaciAn para considerar la caducidad
     * @return lista de items proximos a caducar
     */

    @Transactional(readOnly = true)
    public List<FridgeItem> execute(String principal, int days) {
        String fridgeId = resolveAuthenticatedHouseholdContextUseCase.execute(principal).fridge().getId();
        return fridgeItemService.getExpiringItems(fridgeId, days);
    }
}




