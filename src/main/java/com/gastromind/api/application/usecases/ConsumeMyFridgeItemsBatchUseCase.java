package com.gastromind.api.application.usecases;

import com.gastromind.api.application.services.FridgeItemServiceImpl;
import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.domain.models.FridgeItemConsumeLine;
import com.gastromind.api.domain.ports.out.FridgeItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Consume parcial de varios items de la nevera del hogar en una sola peticion.
 * Primero comprueba que cada item sea de tu nevera; luego aplica todos los descuentos en una transaccion.
 */
@Service
public class ConsumeMyFridgeItemsBatchUseCase {

    private final ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase;
    private final FridgeItemRepository fridgeItemRepository;
    private final FridgeItemServiceImpl fridgeItemService;

    public ConsumeMyFridgeItemsBatchUseCase(
            ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase,
            FridgeItemRepository fridgeItemRepository,
            FridgeItemServiceImpl fridgeItemService
    ) {
        this.resolveAuthenticatedHouseholdContextUseCase = resolveAuthenticatedHouseholdContextUseCase;
        this.fridgeItemRepository = fridgeItemRepository;
        this.fridgeItemService = fridgeItemService;
    }

    @Transactional
    public List<FridgeItem> execute(String principal, List<FridgeItemConsumeLine> lines) {
        String fridgeId = resolveAuthenticatedHouseholdContextUseCase.execute(principal).fridge().getId();
        for (FridgeItemConsumeLine line : lines) {
            assertItemBelongsToFridge(line.itemId(), fridgeId);
        }
        return fridgeItemService.consumePartiallyBatch(lines);
    }

    private void assertItemBelongsToFridge(String itemId, String fridgeId) {
        FridgeItem existing = fridgeItemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item de nevera no encontrado"));
        if (!fridgeId.equals(existing.getFridgeId())) {
            throw new ForbiddenException("El item no pertenece a la nevera del usuario autenticado");
        }
    }
}
