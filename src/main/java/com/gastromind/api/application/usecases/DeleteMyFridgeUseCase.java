package com.gastromind.api.application.usecases;

import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.domain.ports.out.FridgeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/**
 * Caso de uso para eliminar la nevera del hogar autenticado.
 * Solo usuarios con rol OWNER pueden ejecutar esta operación.
 */
public class DeleteMyFridgeUseCase {

    private final ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase;
    private final FridgeRepository fridgeRepository;
    /**
     * Constructor con dependencias para resolver contexto y eliminar nevera.
     *
     * @param resolveAuthenticatedHouseholdContextUseCase resolvedor de contexto autenticado
     * @param fridgeRepository repositorio de neveras
     */

    public DeleteMyFridgeUseCase(
            ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase,
            FridgeRepository fridgeRepository
    ) {
        this.resolveAuthenticatedHouseholdContextUseCase = resolveAuthenticatedHouseholdContextUseCase;
        this.fridgeRepository = fridgeRepository;
    }
    /**
     * Elimina la nevera asociada al hogar del usuario autenticado.
     *
     * @param principal identificador del usuario autenticado
     * @throws ForbiddenException si el usuario no tiene permisos de OWNER
     */

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




