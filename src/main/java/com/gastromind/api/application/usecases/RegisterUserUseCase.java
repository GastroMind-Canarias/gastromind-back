package com.gastromind.api.application.usecases;

import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.domain.ports.in.IAllergenService;
import com.gastromind.api.domain.ports.in.IHouseHoldService;
import com.gastromind.api.domain.ports.in.IUserService;
import com.gastromind.api.infrastructure.security.auth.dtos.HouseholdRegistrationMode;
import com.gastromind.api.infrastructure.security.auth.dtos.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/**
 * Caso de uso responsable de registrar un usuario y vincularlo a un hogar.
 * Permite unirse a un hogar existente mediante invitaciAn o crear uno nuevo.
 */
public class RegisterUserUseCase {

    private final IHouseHoldService householdService;
    private final IUserService userService;
    private final IAllergenService allergenService;
    private final PasswordEncoder passwordEncoder;
    /**
     * Constructor con las dependencias necesarias para el flujo de alta de usuario.
     *
     * @param householdService servicio de gestiAn de hogares
     * @param userService servicio de gestiAn de usuarios
     * @param allergenService servicio de consulta de alergenos
     * @param passwordEncoder componente para cifrar la contraseAa
     */

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
    /**
     * Registra al usuario segAn el modo de alta indicado en la solicitud.
     *
     * @param request datos de registro, incluyendo datos personales y de hogar
     * @throws IllegalArgumentException si faltan datos obligatorios o el modo de alta es inconsistente
     */

    @Transactional
    public void exec(RegisterRequest request) {
        boolean joinExisting = resolveJoinExisting(request);
        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User();
        user.setName(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordHash);

        if (request.allergenIds() != null && !request.allergenIds().isEmpty()) {
            request.allergenIds().forEach(id -> {
                Allergen allergen = allergenService.findById(id);
                user.addAllergen(allergen);
            });
        }

        if (joinExisting) {
            String token = request.inviteToken() != null ? request.inviteToken().trim() : "";
            if (token.isEmpty()) {
                throw new IllegalArgumentException("El codigo de invitacion es obligatorio para unirse a un hogar existente");
            }
            user.setHouseHold_id(null);
            user.setRole(Role.ROLE_MEMBER);
            User saved = userService.create(user);
            householdService.addMemberByToken(token, saved.getId());
            return;
        }

        String householdName = request.householdName() != null ? request.householdName().trim() : "";
        if (householdName.isEmpty()) {
            throw new IllegalArgumentException("El nombre del hogar es obligatorio al crear un hogar nuevo");
        }

        HouseHold houseHold = new HouseHold();
        houseHold.setName(householdName);
        HouseHold nuevoHogar = householdService.create(houseHold);

        if (request.applianceTypes() != null && !request.applianceTypes().isEmpty()) {
            request.applianceTypes().forEach(appliance ->
                    householdService.addAppliance(nuevoHogar.getId(), appliance)
            );
        }

        user.setHouseHold_id(nuevoHogar);
        user.setRole(Role.ROLE_OWNER);
        userService.create(user);
    }

    private boolean resolveJoinExisting(RegisterRequest request) {
        HouseholdRegistrationMode mode = request.householdMode();
        boolean hasToken = request.inviteToken() != null && !request.inviteToken().isBlank();
        boolean hasHouseholdName = request.householdName() != null && !request.householdName().isBlank();

        if (mode == HouseholdRegistrationMode.JOIN_EXISTING) {
            if (!hasToken) {
                throw new IllegalArgumentException("Debe enviar inviteToken para unirse a un hogar existente");
            }
            return true;
        }
        if (mode == HouseholdRegistrationMode.CREATE_NEW) {
            if (hasToken) {
                throw new IllegalArgumentException("No envie inviteToken si el modo es crear hogar nuevo (CREATE_NEW)");
            }
            return false;
        }

        if (hasToken && hasHouseholdName) {
            throw new IllegalArgumentException("Indique solo una opcion: inviteToken para unirse o householdName para crear un hogar nuevo");
        }
        if (hasToken) {
            return true;
        }
        if (hasHouseholdName) {
            return false;
        }
        throw new IllegalArgumentException("Debe enviar inviteToken para unirse a un hogar o householdName para crear uno nuevo");
    }
}




