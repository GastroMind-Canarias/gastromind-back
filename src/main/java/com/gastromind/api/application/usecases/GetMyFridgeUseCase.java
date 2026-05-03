package com.gastromind.api.application.usecases;

import com.gastromind.api.domain.models.Fridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/**
 * Caso de uso para obtener la nevera asociada al usuario autenticado.
 */
public class GetMyFridgeUseCase {

    private final ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase;
    /**
     * Constructor con el resolvedor de contexto autenticado.
     *
     * @param resolveAuthenticatedHouseholdContextUseCase caso de uso para resolver usuario, hogar y nevera
     */

    public GetMyFridgeUseCase(ResolveAuthenticatedHouseholdContextUseCase resolveAuthenticatedHouseholdContextUseCase) {
        this.resolveAuthenticatedHouseholdContextUseCase = resolveAuthenticatedHouseholdContextUseCase;
    }
    /**
     * Devuelve la nevera del hogar del usuario autenticado.
     *
     * @param principal identificador del usuario autenticado
     * @return nevera del hogar
     */

    @Transactional(readOnly = true)
    public Fridge execute(String principal) {
        return resolveAuthenticatedHouseholdContextUseCase.execute(principal).fridge();
    }
}




