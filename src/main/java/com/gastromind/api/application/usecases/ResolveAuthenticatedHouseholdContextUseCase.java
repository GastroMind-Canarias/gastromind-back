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
/**
 * Caso de uso que resuelve el contexto de hogar del usuario autenticado.
 * Valida la identidad del principal y comprueba su pertenencia a un hogar.
 */
public class ResolveAuthenticatedHouseholdContextUseCase {

    private final UserRepository userRepository;
    private final FridgeRepository fridgeRepository;
    /**
     * Constructor con los repositorios necesarios para resolver contexto autenticado.
     *
     * @param userRepository repositorio de usuarios
     * @param fridgeRepository repositorio de neveras
     */

    public ResolveAuthenticatedHouseholdContextUseCase(UserRepository userRepository, FridgeRepository fridgeRepository) {
        this.userRepository = userRepository;
        this.fridgeRepository = fridgeRepository;
    }
    /**
     * Resuelve el contexto completo del usuario, incluyendo la nevera principal del hogar.
     *
     * @param principal nombre de usuario o correo autenticado
     * @return contexto autenticado con usuario, hogar y nevera
     * @throws ForbiddenException si el principal no es válido o el usuario no pertenece a un hogar
     * @throws NotFoundException si el hogar no tiene nevera asociada
     */

    @Transactional(readOnly = true)
    public AuthenticatedHouseholdContext execute(String principal) {
        AuthenticatedHouseholdContext context = resolveWithoutFridge(principal);
        Fridge fridge = fridgeRepository.findFirstByHouseholdId(context.householdId())
                .orElseThrow(() -> new NotFoundException("Nevera no encontrada"));

        return new AuthenticatedHouseholdContext(context.user(), context.householdId(), fridge);
    }
    /**
     * Resuelve el contexto del usuario sin exigir nevera asociada.
     *
     * @param principal nombre de usuario o correo autenticado
     * @return contexto autenticado con usuario y hogar, sin nevera
     * @throws ForbiddenException si el principal no es válido o el usuario no pertenece a un hogar
     */

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
            throw new ForbiddenException("El usuario no pertenece a ningun hogar");
        }

        String householdId = user.getHouseHold_id().getId();
        return new AuthenticatedHouseholdContext(user, householdId, null);
    }

    public record AuthenticatedHouseholdContext(User user, String householdId, Fridge fridge) {
    }
}




