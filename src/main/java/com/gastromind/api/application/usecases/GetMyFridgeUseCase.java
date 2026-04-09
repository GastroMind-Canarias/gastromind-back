package com.gastromind.api.application.usecases;

import com.gastromind.api.domain.models.Fridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetMyFridgeUseCase {

    private final ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase;

    public GetMyFridgeUseCase(ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase) {
        this.resolveAuthenticatedHouseholdContextUseCase = resolveAuthenticatedHouseholdContextUseCase;
    }

    @Transactional(readOnly = true)
    public Fridge execute(String principal) {
        return resolveAuthenticatedHouseholdContextUseCase.execute(principal).fridge();
    }
}
