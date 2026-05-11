package com.gastromind.api.application.usecases;

import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.domain.ports.out.FridgeItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
/**
 * Caso de uso para listar todos los items de la nevera del usuario autenticado.
 */
public class ListMyFridgeItemsUseCase {

    private final ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase;
    private final FridgeItemRepository fridgeItemRepository;

    public ListMyFridgeItemsUseCase(
            ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase,
            FridgeItemRepository fridgeItemRepository
    ) {
        this.resolveAuthenticatedHouseholdContextUseCase = resolveAuthenticatedHouseholdContextUseCase;
        this.fridgeItemRepository = fridgeItemRepository;
    }
    /**
     * Devuelve el inventario completo de la nevera asociada al usuario autenticado.
     *
     * @param principal identificador del usuario autenticado
     * @return lista de items de nevera
     */

    @Transactional(readOnly = true)
    public List<FridgeItem> execute(String principal) {
        String fridgeId = resolveAuthenticatedHouseholdContextUseCase.execute(principal).fridge().getId();
        return fridgeItemRepository.findByFridgeId(fridgeId);
    }
}




