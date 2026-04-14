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
public class UpdateMyPreferencesUseCase {

    private final UserServiceImpl userServiceImpl;
    private final HouseHoldServiceImpl houseHoldServiceImpl;

    public UpdateMyPreferencesUseCase(UserServiceImpl userServiceImpl, HouseHoldServiceImpl houseHoldServiceImpl) {
        this.userServiceImpl = userServiceImpl;
        this.houseHoldServiceImpl = houseHoldServiceImpl;
    }

    @Transactional
    public User execute(String principal, List<String> allergenIds, List<Appliance> appliances) {
        if (principal == null || principal.isBlank()) {
            throw new ForbiddenException("Usuario no autenticado");
        }

        User user = userServiceImpl.findByUsername(principal);
        if (user.getHouseHold_id() == null || user.getHouseHold_id().getId() == null || user.getHouseHold_id().getId().isBlank()) {
            throw new ForbiddenException("El usuario no pertenece a ningún hogar");
        }
        if (user.getRole() != Role.ROLE_OWNER) {
            throw new ForbiddenException("Solo el OWNER del hogar puede gestionar los electrodomésticos");
        }

        userServiceImpl.replaceAllergens(user.getId(), allergenIds != null ? allergenIds : List.of());
        houseHoldServiceImpl.replaceAppliances(user.getHouseHold_id().getId(), appliances != null ? appliances : List.of());

        return userServiceImpl.findById(user.getId());
    }
}
