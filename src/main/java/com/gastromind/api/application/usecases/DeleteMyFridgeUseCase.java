package com.gastromind.api.application.usecases;

import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.domain.ports.out.FridgeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteMyFridgeUseCase {

    private final ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase;
    private final FridgeRepository fridgeRepository;

    public DeleteMyFridgeUseCase(
            ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase,
            FridgeRepository fridgeRepository
    ) {
        this.resolveAuthenticatedHouseholdContextUseCase = resolveAuthenticatedHouseholdContextUseCase;
        this.fridgeRepository = fridgeRepository;
    }

    @Transactional
    public void execute(String principal) {
        ResolveAuthenticatedHouseholdContextUseCase.AuthenticatedHouseholdContext context =
                resolveAuthenticatedHouseholdContextUseCase.execute(principal);

        if (context.user().getRole() != Role.ROLE_OWNER) {
            throw new ForbiddenException("Solo el OWNER del hogar puede gestionar su nevera");
        }

        fridgeRepository.deleteById(context.fridge().getId());
    }
}
