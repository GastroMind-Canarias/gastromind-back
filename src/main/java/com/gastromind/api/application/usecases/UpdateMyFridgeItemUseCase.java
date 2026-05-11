package com.gastromind.api.application.usecases;

import com.gastromind.api.application.services.FridgeItemServiceImpl;
import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.domain.ports.out.FridgeItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Actualiza un item de la nevera del hogar del usuario. Comprueba que la fila exista y sea de
 * su nevera; el cuerpo del PUT solo trae cantidad, caducidad y estado, asi que reutilizamos
 * el producto (y la etiqueta libre si la habia) que ya estan en base. Si no hicieramos esto,
 * el {@code save} del repositorio podria dejar el producto a null y romper el enlace al catalogo.
 */
@Service
public class UpdateMyFridgeItemUseCase {

    private final ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase;
    private final FridgeItemRepository fridgeItemRepository;
    private final FridgeItemServiceImpl fridgeItemService;

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
     * Aplica los cambios de inventario para un item concreto. {@code itemToUpdate} suele venir
     * del mapper REST sin producto cuando el cliente no envia {@code productId}; aqui se rellena
     * desde la fila actual antes de persistir.
     */
    @Transactional
    public FridgeItem execute(String principal, String itemId, FridgeItem itemToUpdate) {
        String fridgeId = resolveAuthenticatedHouseholdContextUseCase.execute(principal).fridge().getId();
        FridgeItem existing = assertItemBelongsToFridge(itemId, fridgeId);
        itemToUpdate.setFridgeId(fridgeId);
        itemToUpdate.setProduct(existing.getProduct());
        itemToUpdate.setProductLabel(existing.getProductLabel());
        return fridgeItemService.update(itemId, itemToUpdate);
    }

    private FridgeItem assertItemBelongsToFridge(String itemId, String fridgeId) {
        FridgeItem existing = fridgeItemRepository.findById(itemId)
                .orElseThrow(() -> new com.gastromind.api.domain.exceptions.NotFoundException("Item de nevera no encontrado"));
        if (!fridgeId.equals(existing.getFridgeId())) {
            throw new ForbiddenException("El item no pertenece a la nevera del usuario autenticado");
        }
        return existing;
    }
}




