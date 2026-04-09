package com.gastromind.api.application.usecases;

import com.gastromind.api.application.services.FridgeItemServiceImpl;
import com.gastromind.api.domain.models.FridgeItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class CreateMyFridgeItemUseCase {

    private final ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase;
    private final FridgeItemServiceImpl fridgeItemService;

    public CreateMyFridgeItemUseCase(
            ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase,
            FridgeItemServiceImpl fridgeItemService
    ) {
        this.resolveAuthenticatedHouseholdContextUseCase = resolveAuthenticatedHouseholdContextUseCase;
        this.fridgeItemService = fridgeItemService;
    }

    @Transactional
    public FridgeItem execute(String principal, String productId, BigDecimal quantity, LocalDate expirationDate) {
        String fridgeId = resolveAuthenticatedHouseholdContextUseCase.execute(principal).fridge().getId();
        return fridgeItemService.addProductToFridge(fridgeId, productId, quantity, expirationDate);
    }
}
