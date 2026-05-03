package com.gastromind.api.application.usecases;

import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.domain.ports.out.FridgeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/**
 * Caso de uso para actualizar los datos de la nevera del hogar autenticado.
 * Solo permite la operaciAn a usuarios con rol OWNER.
 */
public class UpdateMyFridgeUseCase {

    private final ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase;
    private final FridgeRepository fridgeRepository;
    /**
     * Constructor con dependencias para resolver contexto y persistir nevera.
     *
     * @param resolveAuthenticatedHouseholdContextUseCase resolvedor de contexto autenticado
     * @param fridgeRepository repositorio de neveras
     */

    public UpdateMyFridgeUseCase(
            ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase,
            FridgeRepository fridgeRepository
    ) {
        this.resolveAuthenticatedHouseholdContextUseCase = resolveAuthenticatedHouseholdContextUseCase;
        this.fridgeRepository = fridgeRepository;
    }
    /**
     * Define la nevera asociada al hogar del usuario autenticado.
     *
     * @param principal identificador del usuario autenticado
     * @param fridge datos de nevera a persistir
     * @return nevera actualizada
     * @throws ForbiddenException si el usuario no tiene permisos de OWNER
     */

    @Transactional
    public Fridge execute(String principal, Fridge fridge) {
        ResolveAuthenticatedHouseholdContextUseCase.AuthenticatedHouseholdContext context =
                resolveAuthenticatedHouseholdContextUseCase.execute(principal);

        if (context.user().getRole() != Role.ROLE_OWNER) {
            throw new ForbiddenException("Solo el OWNER del hogar puede gestionar su nevera");
        }

        fridge.setId(context.fridge().getId());
        fridge.setHouseHold_id(new HouseHold(context.householdId()));
        return fridgeRepository.save(fridge);
    }
}




