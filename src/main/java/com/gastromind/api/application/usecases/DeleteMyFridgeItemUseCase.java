package com.gastromind.api.application.usecases;

import com.gastromind.api.application.services.FridgeItemServiceImpl;
import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.domain.ports.out.FridgeItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteMyFridgeItemUseCase {

    private final ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase;
    private final FridgeItemRepository fridgeItemRepository;
    private final FridgeItemServiceImpl fridgeItemService;

    public DeleteMyFridgeItemUseCase(
            ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase,
            FridgeItemRepository fridgeItemRepository,
            FridgeItemServiceImpl fridgeItemService
    ) {
        this.resolveAuthenticatedHouseholdContextUseCase = resolveAuthenticatedHouseholdContextUseCase;
        this.fridgeItemRepository = fridgeItemRepository;
        this.fridgeItemService = fridgeItemService;
    }

    @Transactional
    public void execute(String principal, String itemId) {
        String fridgeId = resolveAuthenticatedHouseholdContextUseCase.execute(principal).fridge().getId();
        assertItemBelongsToFridge(itemId, fridgeId);
        fridgeItemService.delete(itemId);
    }

    private void assertItemBelongsToFridge(String itemId, String fridgeId) {
        FridgeItem existing = fridgeItemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item de nevera no encontrado"));
        if (!fridgeId.equals(existing.getFridgeId())) {
            throw new ForbiddenException("El item no pertenece a la nevera del usuario autenticado");
        }
    }
}
