package com.gastromind.api.application.usecases;

import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.exceptions.FridgeAlreadyExistsException;
import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.domain.ports.out.FridgeRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyFridgeUseCasesTest {

    @Test
    void getMyFridge_shouldReturnFridgeForAuthenticatedUser() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        GetMyFridgeUseCase useCase = new GetMyFridgeUseCase(resolveUseCase);

        ResolveAuthenticatedHouseholdContextUseCase.AuthenticatedHouseholdContext context =
                buildContext(Role.ROLE_MEMBER, "house-1", "fridge-1");
        when(resolveUseCase.execute("member1")).thenReturn(context);

        Fridge fridge = useCase.execute("member1");

        assertNotNull(fridge);
        assertEquals("fridge-1", fridge.getId());
        verify(resolveUseCase).execute("member1");
    }

    @Test
    void createMyFridge_shouldCreateWhenUserIsOwner() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        CreateMyFridgeUseCase useCase = new CreateMyFridgeUseCase(resolveUseCase, fridgeRepository);

        ResolveAuthenticatedHouseholdContextUseCase.AuthenticatedHouseholdContext context =
                buildContext(Role.ROLE_OWNER, "house-1", null);
        Fridge toCreate = new Fridge();
        Fridge saved = new Fridge();
        saved.setId("fridge-new");
        saved.setHouseHold_id(new HouseHold("house-1"));

        when(resolveUseCase.executeWithoutFridge("owner1")).thenReturn(context);
        when(fridgeRepository.findFirstByHouseholdId("house-1")).thenReturn(java.util.Optional.empty());
        when(fridgeRepository.save(toCreate)).thenReturn(saved);

        Fridge result = useCase.execute("owner1", toCreate);

        assertEquals("fridge-new", result.getId());
        assertEquals("house-1", toCreate.getHouseHold_id().getId());
        verify(fridgeRepository).save(toCreate);
    }

    @Test
    void createMyFridge_shouldRejectWhenFridgeAlreadyExistsForHousehold() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        CreateMyFridgeUseCase useCase = new CreateMyFridgeUseCase(resolveUseCase, fridgeRepository);

        ResolveAuthenticatedHouseholdContextUseCase.AuthenticatedHouseholdContext context =
                buildContext(Role.ROLE_OWNER, "house-1", null);
        Fridge existing = new Fridge();
        existing.setId("fridge-existing");
        existing.setHouseHold_id(new HouseHold("house-1"));

        when(resolveUseCase.executeWithoutFridge("owner1")).thenReturn(context);
        when(fridgeRepository.findFirstByHouseholdId("house-1")).thenReturn(java.util.Optional.of(existing));

        FridgeAlreadyExistsException ex = assertThrows(
                FridgeAlreadyExistsException.class,
                () -> useCase.execute("owner1", new Fridge())
        );
        assertEquals("El hogar ya tiene una nevera creada", ex.getMessage());
        verify(fridgeRepository, never()).save(org.mockito.ArgumentMatchers.any(Fridge.class));
    }

    @Test
    void createMyFridge_shouldFailWhenUserIsNotOwner() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        CreateMyFridgeUseCase useCase = new CreateMyFridgeUseCase(resolveUseCase, fridgeRepository);

        when(resolveUseCase.executeWithoutFridge("member1")).thenReturn(buildContext(Role.ROLE_MEMBER, "house-1", null));

        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> useCase.execute("member1", new Fridge()));
        assertEquals("Solo el OWNER del hogar puede gestionar su nevera", ex.getMessage());
    }

    @Test
    void updateMyFridge_shouldUpdateCurrentHouseholdFridgeWhenOwner() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        UpdateMyFridgeUseCase useCase = new UpdateMyFridgeUseCase(resolveUseCase, fridgeRepository);

        ResolveAuthenticatedHouseholdContextUseCase.AuthenticatedHouseholdContext context =
                buildContext(Role.ROLE_OWNER, "house-1", "fridge-1");
        Fridge update = new Fridge();
        Fridge saved = new Fridge();
        saved.setId("fridge-1");
        saved.setHouseHold_id(new HouseHold("house-1"));

        when(resolveUseCase.execute("owner1")).thenReturn(context);
        when(fridgeRepository.save(update)).thenReturn(saved);

        Fridge result = useCase.execute("owner1", update);

        assertEquals("fridge-1", update.getId());
        assertEquals("house-1", update.getHouseHold_id().getId());
        assertEquals("fridge-1", result.getId());
        verify(fridgeRepository).save(update);
    }

    @Test
    void deleteMyFridge_shouldDeleteCurrentHouseholdFridgeWhenOwner() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        DeleteMyFridgeUseCase useCase = new DeleteMyFridgeUseCase(resolveUseCase, fridgeRepository);

        when(resolveUseCase.execute("owner1")).thenReturn(buildContext(Role.ROLE_OWNER, "house-1", "fridge-1"));

        useCase.execute("owner1");

        verify(fridgeRepository).deleteById("fridge-1");
    }

    @Test
    void updateMyFridge_shouldFailWhenUserIsNotOwner() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        UpdateMyFridgeUseCase useCase = new UpdateMyFridgeUseCase(resolveUseCase, fridgeRepository);

        when(resolveUseCase.execute("member1")).thenReturn(buildContext(Role.ROLE_MEMBER, "house-1", "fridge-1"));

        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> useCase.execute("member1", new Fridge()));
        assertEquals("Solo el OWNER del hogar puede gestionar su nevera", ex.getMessage());
        verify(fridgeRepository, never()).save(org.mockito.ArgumentMatchers.any(Fridge.class));
    }

    @Test
    void deleteMyFridge_shouldFailWhenUserIsNotOwner() {
        ResolveAuthenticatedHouseholdContextUseCase resolveUseCase = mock(ResolveAuthenticatedHouseholdContextUseCase.class);
        FridgeRepository fridgeRepository = mock(FridgeRepository.class);
        DeleteMyFridgeUseCase useCase = new DeleteMyFridgeUseCase(resolveUseCase, fridgeRepository);

        when(resolveUseCase.execute("member1")).thenReturn(buildContext(Role.ROLE_MEMBER, "house-1", "fridge-1"));

        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> useCase.execute("member1"));
        assertEquals("Solo el OWNER del hogar puede gestionar su nevera", ex.getMessage());
        verify(fridgeRepository, never()).deleteById(org.mockito.ArgumentMatchers.anyString());
    }

    private ResolveAuthenticatedHouseholdContextUseCase.AuthenticatedHouseholdContext buildContext(
            Role role,
            String householdId,
            String fridgeId
    ) {
        HouseHold household = new HouseHold();
        household.setId(householdId);

        User user = new User();
        user.setId("user-1");
        user.setName("user1");
        user.setRole(role);
        user.setHouseHold_id(household);

        Fridge fridge = null;
        if (fridgeId != null) {
            fridge = new Fridge();
            fridge.setId(fridgeId);
            fridge.setHouseHold_id(household);
        }

        return new ResolveAuthenticatedHouseholdContextUseCase.AuthenticatedHouseholdContext(
                user,
                householdId,
                fridge
        );
    }
}
