package com.gastromind.api.application.usecases;

import com.gastromind.api.application.services.FridgeItemServiceImpl;
import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.domain.ports.out.FridgeItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ConsumeMyFridgeItemUseCase {

    private final ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase;
    private final FridgeItemRepository fridgeItemRepository;
    private final FridgeItemServiceImpl fridgeItemService;

    public ConsumeMyFridgeItemUseCase(
            ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase,
            FridgeItemRepository fridgeItemRepository,
            FridgeItemServiceImpl fridgeItemService
    ) {
        this.resolveAuthenticatedHouseholdContextUseCase = resolveAuthenticatedHouseholdContextUseCase;
        this.fridgeItemRepository = fridgeItemRepository;
        this.fridgeItemService = fridgeItemService;
    }

    @Transactional
    public FridgeItem execute(String principal, String itemId, BigDecimal quantityToConsume) {
        String fridgeId = resolveAuthenticatedHouseholdContextUseCase.execute(principal).fridge().getId();
        assertItemBelongsToFridge(itemId, fridgeId);
        return fridgeItemService.consumePartially(itemId, quantityToConsume);
    }

    private void assertItemBelongsToFridge(String itemId, String fridgeId) {
        FridgeItem existing = fridgeItemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item de nevera no encontrado"));
        if (!fridgeId.equals(existing.getFridgeId())) {
            throw new ForbiddenException("El item no pertenece a la nevera del usuario autenticado");
        }
    }
}
