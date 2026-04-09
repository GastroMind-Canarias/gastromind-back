package com.gastromind.api.application.usecases;

import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.exceptions.FridgeAlreadyExistsException;
import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.domain.ports.out.FridgeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateMyFridgeUseCase {

    private final ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase;
    private final FridgeRepository fridgeRepository;

    public CreateMyFridgeUseCase(
            ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase,
            FridgeRepository fridgeRepository
    ) {
        this.resolveAuthenticatedHouseholdContextUseCase = resolveAuthenticatedHouseholdContextUseCase;
        this.fridgeRepository = fridgeRepository;
    }

    @Transactional
    public Fridge execute(String principal, Fridge fridge) {
        ResolveAuthenticatedHouseholdContextUseCase.AuthenticatedHouseholdContext context =
                resolveAuthenticatedHouseholdContextUseCase.executeWithoutFridge(principal);

        if (context.user().getRole() != Role.ROLE_OWNER) {
            throw new ForbiddenException("Solo el OWNER del hogar puede gestionar su nevera");
        }

        if (fridgeRepository.findFirstByHouseholdId(context.householdId()).isPresent()) {
            throw new FridgeAlreadyExistsException("El hogar ya tiene una nevera creada");
        }

        fridge.setHouseHold_id(new HouseHold(context.householdId()));
        return fridgeRepository.save(fridge);
    }
}
