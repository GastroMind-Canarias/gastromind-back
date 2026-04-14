package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.UserServiceImpl;
import com.gastromind.api.application.usecases.UpdateMyPreferencesUseCase;
import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Appliance;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.user.UpdateMyPreferencesRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.user.UserResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.AllergenRestMapper;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.UserRestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Test
    void updateMyPreferences_shouldReplaceAllergensAndAppliances() {
        UserServiceImpl userServiceImpl = mock(UserServiceImpl.class);
        UserRestMapper userMapper = mock(UserRestMapper.class);
        AllergenRestMapper allergenRestMapper = mock(AllergenRestMapper.class);
        UpdateMyPreferencesUseCase updateMyPreferencesUseCase = mock(UpdateMyPreferencesUseCase.class);
        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);

        UserController controller = new UserController();
        ReflectionTestUtils.setField(controller, "userServiceImpl", userServiceImpl);
        ReflectionTestUtils.setField(controller, "userMapper", userMapper);
        ReflectionTestUtils.setField(controller, "allergenRestMapper", allergenRestMapper);
        ReflectionTestUtils.setField(controller, "updateMyPreferencesUseCase", updateMyPreferencesUseCase);

        UpdateMyPreferencesRequest request = new UpdateMyPreferencesRequest(
                List.of("allergen-1", "allergen-2"),
                List.of(Appliance.HORNO, Appliance.BATIDORA)
        );

        User updatedUser = new User();
        updatedUser.setId("user-1");

        UserResponse response = new UserResponse("user-1", "owner", "owner@gastromind.com", "house-1", null, List.of());

        when(authentication.getName()).thenReturn("owner");
        when(updateMyPreferencesUseCase.execute("owner", request.allergenIds(), request.appliances())).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(response);

        var result = controller.updateMyPreferences(authentication, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("user-1", result.getBody().id());
        verify(updateMyPreferencesUseCase).execute("owner", request.allergenIds(), request.appliances());
    }

    @Test
    void updateMyPreferences_shouldFailWhenUserWithoutHousehold() {
        UserServiceImpl userServiceImpl = mock(UserServiceImpl.class);
        UserRestMapper userMapper = mock(UserRestMapper.class);
        AllergenRestMapper allergenRestMapper = mock(AllergenRestMapper.class);
        UpdateMyPreferencesUseCase updateMyPreferencesUseCase = mock(UpdateMyPreferencesUseCase.class);
        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);

        UserController controller = new UserController();
        ReflectionTestUtils.setField(controller, "userServiceImpl", userServiceImpl);
        ReflectionTestUtils.setField(controller, "userMapper", userMapper);
        ReflectionTestUtils.setField(controller, "allergenRestMapper", allergenRestMapper);
        ReflectionTestUtils.setField(controller, "updateMyPreferencesUseCase", updateMyPreferencesUseCase);

        UpdateMyPreferencesRequest request = new UpdateMyPreferencesRequest(List.of(), List.of());
        when(authentication.getName()).thenReturn("owner");
        when(updateMyPreferencesUseCase.execute("owner", request.allergenIds(), request.appliances()))
                .thenThrow(new ForbiddenException("El usuario no pertenece a ningún hogar"));

        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> controller.updateMyPreferences(authentication, request));

        assertTrue(ex.getMessage().contains("ningún hogar"));
    }
}
