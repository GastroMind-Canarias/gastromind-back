package com.gastromind.api.application.usecases;

import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.ports.out.FridgeRepository;
import com.gastromind.api.domain.ports.out.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResolveAuthenticatedHouseholdContextUseCase {

    private final UserRepository userRepository;
    private final FridgeRepository fridgeRepository;

    public ResolveAuthenticatedHouseholdContextUseCase(UserRepository userRepository, FridgeRepository fridgeRepository) {
        this.userRepository = userRepository;
        this.fridgeRepository = fridgeRepository;
    }

    @Transactional(readOnly = true)
    public AuthenticatedHouseholdContext execute(String principal) {
        AuthenticatedHouseholdContext context = resolveWithoutFridge(principal);
        Fridge fridge = fridgeRepository.findFirstByHouseholdId(context.householdId())
                .orElseThrow(() -> new NotFoundException("Nevera no encontrada"));

        return new AuthenticatedHouseholdContext(context.user(), context.householdId(), fridge);
    }

    @Transactional(readOnly = true)
    public AuthenticatedHouseholdContext executeWithoutFridge(String principal) {
        return resolveWithoutFridge(principal);
    }

    private AuthenticatedHouseholdContext resolveWithoutFridge(String principal) {
        if (principal == null || principal.isBlank()) {
            throw new ForbiddenException("Usuario no autenticado");
        }

        User user = userRepository.findByName(principal)
                .or(() -> userRepository.findByEmail(principal))
                .orElseThrow(() -> new ForbiddenException("No se pudo resolver el usuario autenticado"));

        if (user.getHouseHold_id() == null || user.getHouseHold_id().getId() == null || user.getHouseHold_id().getId().isBlank()) {
            throw new ForbiddenException("El usuario no pertenece a ningún hogar");
        }

        String householdId = user.getHouseHold_id().getId();
        return new AuthenticatedHouseholdContext(user, householdId, null);
    }

    public record AuthenticatedHouseholdContext(User user, String householdId, Fridge fridge) {
    }
}
