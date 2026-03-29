package com.gastromind.api.application.usecases;

import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.ports.in.IAllergenService;
import com.gastromind.api.domain.ports.in.IFridgeService;
import com.gastromind.api.domain.ports.in.IHouseHoldService;
import com.gastromind.api.domain.ports.in.IUserService;
import com.gastromind.api.infrastructure.security.auth.dtos.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RegisterUserUseCase {

    private final IHouseHoldService householdService;
    private final IUserService userService;
    private final IAllergenService allergenService;
    private final IFridgeService fridgeService;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserUseCase(
            IHouseHoldService householdService,
            IUserService userService,
            IAllergenService allergenService,
            IFridgeService fridgeService,
            PasswordEncoder passwordEncoder) {
        this.householdService = householdService;
        this.userService = userService;
        this.allergenService = allergenService;
        this.passwordEncoder = passwordEncoder;
        this.fridgeService = fridgeService;
    }

    @Transactional
    public void registrarUsuarioCompleto(RegisterRequest request) {
        HouseHold houseHold = new HouseHold();
        houseHold.setName(request.householdName());
        houseHold.setMembers(1);
        var nuevoHogar = householdService.create(houseHold);

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User();
        user.setName(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordHash);
        user.setHouseHold_id(nuevoHogar);
        user.setRole(request.role());

        if (request.allergenIds() != null && !request.allergenIds().isEmpty()) {
            request.allergenIds().forEach(id -> {
                Allergen allergen = allergenService.findById(id);
                user.addAllergen(allergen);
            });
        }
        userService.create(user);

        Fridge fridge = new Fridge();
        fridge.setHouseHold_id(nuevoHogar);
        fridgeService.create(fridge);
    }
}