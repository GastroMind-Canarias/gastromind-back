package com.gastromind.api.application.usecases;

import com.gastromind.api.application.services.HouseHoldServiceImpl;
import com.gastromind.api.application.services.UserServiceImpl;
import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Appliance;
import com.gastromind.api.domain.models.enums.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
/**
 * Caso de uso para actualizar preferencias del usuario autenticado y su hogar.
 * Permite reemplazar alérgenos del usuario y electrodomésticos del hogar.
 */
public class UpdateMyPreferencesUseCase {

    private final UserServiceImpl userServiceImpl;
    private final HouseHoldServiceImpl houseHoldServiceImpl;
    /**
     * Constructor con servicios de usuario y hogar.
     *
     * @param userServiceImpl servicio de gestión de usuarios
     * @param houseHoldServiceImpl servicio de gestión de hogares
     */

    public UpdateMyPreferencesUseCase(UserServiceImpl userServiceImpl, HouseHoldServiceImpl houseHoldServiceImpl) {
        this.userServiceImpl = userServiceImpl;
        this.houseHoldServiceImpl = houseHoldServiceImpl;
    }
    /**
     * Define las preferencias del usuario autenticado.
     *
     * @param principal identificador del usuario autenticado
     * @param allergenIds identificadores de alérgenos seleccionados
     * @param appliances lista de electrodomésticos del hogar
     * @return usuario actualizado tras aplicar cambios
     * @throws ForbiddenException si el usuario no está autenticado, no pertenece a un hogar o no es OWNER
     */

    @Transactional
    public User execute(String principal, List<String> allergenIds, List<Appliance> appliances) {
        if (principal == null || principal.isBlank()) {
            throw new ForbiddenException("Usuario no autenticado");
        }

        User user = userServiceImpl.findByUsername(principal);
        if (user.getHouseHold_id() == null || user.getHouseHold_id().getId() == null || user.getHouseHold_id().getId().isBlank()) {
            throw new ForbiddenException("El usuario no pertenece a ningun hogar");
        }
        if (user.getRole() != Role.ROLE_OWNER) {
            throw new ForbiddenException("Solo el OWNER del hogar puede gestionar los electrodomesticos");
        }

        userServiceImpl.replaceAllergens(user.getId(), allergenIds != null ? allergenIds : List.of());
        houseHoldServiceImpl.replaceAppliances(user.getHouseHold_id().getId(), appliances != null ? appliances : List.of());

        return userServiceImpl.findById(user.getId());
    }
}




