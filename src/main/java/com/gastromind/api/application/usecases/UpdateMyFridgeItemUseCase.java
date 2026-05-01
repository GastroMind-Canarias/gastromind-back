package com.gastromind.api.application.usecases;

import com.gastromind.api.application.services.FridgeItemServiceImpl;
import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.domain.ports.out.FridgeItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/**
 * Caso de uso para actualizar un item de la nevera del usuario autenticado.
 * Verifica que el item pertenezca al hogar antes de aplicar cambios.
 */
public class UpdateMyFridgeItemUseCase {

    private final ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase;
    private final FridgeItemRepository fridgeItemRepository;
    private final FridgeItemServiceImpl fridgeItemService;
    /**
     * Constructor con dependencias de validaciAn y actualizaciAn de items.
     *
     * @param resolveAuthenticatedHouseholdContextUseCase resolvedor de contexto autenticado
     * @param fridgeItemRepository repositorio de items de nevera
     * @param fridgeItemService servicio de actualizaciAn de items
     */

    public UpdateMyFridgeItemUseCase(
            ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase,
            FridgeItemRepository fridgeItemRepository,
            FridgeItemServiceImpl fridgeItemService
    ) {
        this.resolveAuthenticatedHouseholdContextUseCase = resolveAuthenticatedHouseholdContextUseCase;
        this.fridgeItemRepository = fridgeItemRepository;
        this.fridgeItemService = fridgeItemService;
    }
    /**
     * Define un item de nevera existente para el usuario autenticado.
     *
     * @param principal identificador del usuario autenticado
     * @param itemId identificador del item a actualizar
     * @param itemToUpdate datos actualizados del item
     * @return item actualizado
     * @throws com.gastromind.api.domain.exceptions.NotFoundException si el item no existe
     * @throws ForbiddenException si el item no pertenece a la nevera del usuario
     */

    @Transactional
    public FridgeItem execute(String principal, String itemId, FridgeItem itemToUpdate) {
        String fridgeId = resolveAuthenticatedHouseholdContextUseCase.execute(principal).fridge().getId();
        assertItemBelongsToFridge(itemId, fridgeId);
        itemToUpdate.setFridgeId(fridgeId);
        return fridgeItemService.update(itemId, itemToUpdate);
    }

    private void assertItemBelongsToFridge(String itemId, String fridgeId) {
        FridgeItem existing = fridgeItemRepository.findById(itemId)
                .orElseThrow(() -> new com.gastromind.api.domain.exceptions.NotFoundException("Item de nevera no encontrado"));
        if (!fridgeId.equals(existing.getFridgeId())) {
            throw new ForbiddenException("El item no pertenece a la nevera del usuario autenticado");
        }
    }
}




