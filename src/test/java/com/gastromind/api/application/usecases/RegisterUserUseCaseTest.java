package com.gastromind.api.application.usecases;

import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Appliance;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.domain.ports.in.IAllergenService;
import com.gastromind.api.domain.ports.in.IHouseHoldService;
import com.gastromind.api.domain.ports.in.IUserService;
import com.gastromind.api.infrastructure.security.auth.dtos.HouseholdRegistrationMode;
import com.gastromind.api.infrastructure.security.auth.dtos.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegisterUserUseCaseTest {

    @Test
    void exec_shouldJoinExistingHousehold_whenJoinModeAndTokenPresent() {
        IHouseHoldService householdService = mock(IHouseHoldService.class);
        IUserService userService = mock(IUserService.class);
        IAllergenService allergenService = mock(IAllergenService.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        RegisterUserUseCase useCase = new RegisterUserUseCase(householdService, userService, allergenService, passwordEncoder);

        RegisterRequest request = new RegisterRequest(
                "juan1234", "Secret123!", "juan@example.com", null,
                HouseholdRegistrationMode.JOIN_EXISTING, null, "  INV-1  ",
                List.of(), List.of());
        when(passwordEncoder.encode("Secret123!")).thenReturn("hashed-pass");
        when(userService.create(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId("user-1");
            return u;
        });

        useCase.exec(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).create(userCaptor.capture());
        User saved = userCaptor.getValue();
        assertEquals("juan1234", saved.getName());
        assertEquals("juan@example.com", saved.getEmail());
        assertEquals("hashed-pass", saved.getPassword());
        assertEquals(Role.ROLE_MEMBER, saved.getRole());
        verify(householdService).addMemberByToken("INV-1", "user-1");
        verify(householdService, never()).create(any(HouseHold.class));
    }

    @Test
    void exec_shouldCreateHouseholdAndOwner_whenCreateMode() {
        IHouseHoldService householdService = mock(IHouseHoldService.class);
        IUserService userService = mock(IUserService.class);
        IAllergenService allergenService = mock(IAllergenService.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        RegisterUserUseCase useCase = new RegisterUserUseCase(householdService, userService, allergenService, passwordEncoder);

        Allergen gluten = new Allergen();
        gluten.setId("a-1");
        HouseHold created = new HouseHold();
        created.setId("house-1");
        when(passwordEncoder.encode("Secret123!")).thenReturn("hashed-pass");
        when(allergenService.findById("a-1")).thenReturn(gluten);
        when(householdService.create(any(HouseHold.class))).thenReturn(created);

        RegisterRequest request = new RegisterRequest(
                "ana1234", "Secret123!", "ana@example.com", null,
                HouseholdRegistrationMode.CREATE_NEW, "  Casa Ana  ", null,
                List.of("a-1"), List.of(Appliance.AIR_FRYER, Appliance.HORNO));

        useCase.exec(request);

        verify(householdService).addAppliance("house-1", Appliance.AIR_FRYER);
        verify(householdService).addAppliance("house-1", Appliance.HORNO);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).create(userCaptor.capture());
        User createdUser = userCaptor.getValue();
        assertEquals(Role.ROLE_OWNER, createdUser.getRole());
        assertEquals("house-1", createdUser.getHouseHold_id().getId());
        assertEquals(1, createdUser.getAllergens().size());
    }

    @Test
    void exec_shouldFail_whenJoinModeWithoutToken() {
        RegisterUserUseCase useCase = new RegisterUserUseCase(
                mock(IHouseHoldService.class),
                mock(IUserService.class),
                mock(IAllergenService.class),
                mock(PasswordEncoder.class));

        RegisterRequest request = new RegisterRequest(
                "ana1234", "Secret123!", "ana@example.com", null,
                HouseholdRegistrationMode.JOIN_EXISTING, null, " ",
                List.of(), List.of());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> useCase.exec(request));
        assertEquals("Debe enviar inviteToken para unirse a un hogar existente", ex.getMessage());
    }

    @Test
    void exec_shouldFail_whenCreateModeWithToken() {
        RegisterUserUseCase useCase = new RegisterUserUseCase(
                mock(IHouseHoldService.class),
                mock(IUserService.class),
                mock(IAllergenService.class),
                mock(PasswordEncoder.class));

        RegisterRequest request = new RegisterRequest(
                "ana1234", "Secret123!", "ana@example.com", null,
                HouseholdRegistrationMode.CREATE_NEW, "Casa", "INV-1",
                List.of(), List.of());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> useCase.exec(request));
        assertEquals("No envíe inviteToken si el modo es crear hogar nuevo (CREATE_NEW)", ex.getMessage());
    }

    @Test
    void exec_shouldFail_whenModeNullAndAmbiguousData() {
        RegisterUserUseCase useCase = new RegisterUserUseCase(
                mock(IHouseHoldService.class),
                mock(IUserService.class),
                mock(IAllergenService.class),
                mock(PasswordEncoder.class));

        RegisterRequest request = new RegisterRequest(
                "ana1234", "Secret123!", "ana@example.com", null,
                null, "Casa", "INV-1",
                List.of(), List.of());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> useCase.exec(request));
        assertEquals("Indique solo una opción: inviteToken para unirse o householdName para crear un hogar nuevo", ex.getMessage());
    }
}
