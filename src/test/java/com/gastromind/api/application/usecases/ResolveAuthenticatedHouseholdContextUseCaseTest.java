package com.gastromind.api.application.usecases;

import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.ports.out.FridgeRepository;
import com.gastromind.api.domain.ports.out.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ResolveAuthenticatedHouseholdContextUseCaseTest {

    @Test
    void execute_shouldResolveUserHouseholdAndFridgeFromPrincipal() {
        UserRepository userRepository = mock(UserRepository.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        ResolveAuthenticatedHouseholdContextUseCase useCase =
                new ResolveAuthenticatedHouseholdContextUseCase(userRepository, fridgeRepository);

        HouseHold household = new HouseHold();
        household.setId("house-1");

        User user = new User();
        user.setId("user-1");
        user.setName("owner1");
        user.setHouseHold_id(household);

        Fridge fridge = new Fridge();
        fridge.setId("fridge-1");
        fridge.setHouseHold_id(household);

        when(userRepository.findByName("owner1")).thenReturn(Optional.of(user));
        when(fridgeRepository.findFirstByHouseholdId("house-1")).thenReturn(Optional.of(fridge));

        ResolveAuthenticatedHouseholdContextUseCase.AuthenticatedHouseholdContext context =
                useCase.execute("owner1");

        assertNotNull(context);
        assertEquals("user-1", context.user().getId());
        assertEquals("house-1", context.householdId());
        assertEquals("fridge-1", context.fridge().getId());

        verify(userRepository).findByName("owner1");
        verify(fridgeRepository).findFirstByHouseholdId("house-1");
    }

    @Test
    void execute_shouldThrowForbiddenWhenPrincipalIsBlank() {
        UserRepository userRepository = mock(UserRepository.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        ResolveAuthenticatedHouseholdContextUseCase useCase =
                new ResolveAuthenticatedHouseholdContextUseCase(userRepository, fridgeRepository);

        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> useCase.execute(" "));

        assertEquals("Usuario no autenticado", ex.getMessage());
    }

    @Test
    void execute_shouldThrowForbiddenWhenUserHasNoHousehold() {
        UserRepository userRepository = mock(UserRepository.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        ResolveAuthenticatedHouseholdContextUseCase useCase =
                new ResolveAuthenticatedHouseholdContextUseCase(userRepository, fridgeRepository);

        User user = new User();
        user.setId("user-1");
        user.setName("owner1");
        user.setHouseHold_id(null);

        when(userRepository.findByName("owner1")).thenReturn(Optional.of(user));

        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> useCase.execute("owner1"));

        assertEquals("El usuario no pertenece a ningun hogar", ex.getMessage());
    }

    @Test
    void execute_shouldThrowNotFoundWhenHouseholdHasNoFridge() {
        UserRepository userRepository = mock(UserRepository.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        ResolveAuthenticatedHouseholdContextUseCase useCase =
                new ResolveAuthenticatedHouseholdContextUseCase(userRepository, fridgeRepository);

        HouseHold household = new HouseHold();
        household.setId("house-1");

        User user = new User();
        user.setId("user-1");
        user.setName("owner1");
        user.setHouseHold_id(household);

        when(userRepository.findByName("owner1")).thenReturn(Optional.of(user));
        when(fridgeRepository.findFirstByHouseholdId("house-1")).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> useCase.execute("owner1"));

        assertEquals("Nevera no encontrada", ex.getMessage());
    }
}
