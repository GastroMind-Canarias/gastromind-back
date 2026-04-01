package com.gastromind.api.application.usecases;

import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.domain.ports.in.IAllergenService;
import com.gastromind.api.domain.ports.in.IHouseHoldService;
import com.gastromind.api.domain.ports.in.IUserService;
import com.gastromind.api.infrastructure.security.auth.dtos.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterUserUseCase {

    private final IHouseHoldService householdService;
    private final IUserService userService;
    private final IAllergenService allergenService;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserUseCase(
            IHouseHoldService householdService,
            IUserService userService,
            IAllergenService allergenService,
            PasswordEncoder passwordEncoder) {
        this.householdService = householdService;
        this.userService = userService;
        this.allergenService = allergenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void exec(RegisterRequest request) {
        HouseHold houseHold = new HouseHold();
        houseHold.setName(request.householdName());
        var nuevoHogar = householdService.create(houseHold);

        if (request.applianceTypes() != null && !request.applianceTypes().isEmpty()) {
            request.applianceTypes().forEach(appliance ->
                    householdService.addAppliance(nuevoHogar.getId(), appliance)
            );
        }

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User();
        user.setName(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordHash);
        user.setHouseHold_id(nuevoHogar);
        user.setRole(Role.ROLE_OWNER);

        if (request.allergenIds() != null && !request.allergenIds().isEmpty()) {
            request.allergenIds().forEach(id -> {
                Allergen allergen = allergenService.findById(id);
                user.addAllergen(allergen);
            });
        }
        userService.create(user);
    }
}