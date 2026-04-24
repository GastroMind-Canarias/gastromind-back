package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.UserServiceImpl;
import com.gastromind.api.application.usecases.UpdateMyPreferencesUseCase;
import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Appliance;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.allergen.AllergenIdListRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.allergen.AllergenResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.user.UpdateMyPreferencesRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.user.UserRequest;
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
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    void profileAllergenAndAdminEndpoints_shouldDelegate() {
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

        User user = new User();
        user.setId("u-1");
        user.setName("owner");
        UserResponse response = new UserResponse("u-1", "owner", "owner@gm.com", "h1", Role.ROLE_OWNER, List.of());
        when(authentication.getName()).thenReturn("owner");
        when(userServiceImpl.findByUsername("owner")).thenReturn(user);
        when(userServiceImpl.findById("u-1")).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);
        when(userMapper.toDomain(any(UserRequest.class))).thenReturn(user);
        when(userServiceImpl.updateProfile("u-1", user)).thenReturn(user);
        when(userServiceImpl.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponseList(List.of(user))).thenReturn(List.of(response));
        when(userServiceImpl.updateUserRole("u-1", Role.ROLE_ADMIN)).thenReturn(user);

        Allergen allergen = new Allergen("a1", "Gluten");
        AllergenResponse allergenResponse = new AllergenResponse("a1", "Gluten");
        when(userServiceImpl.listAllergens("u-1")).thenReturn(List.of(allergen));
        when(allergenRestMapper.toResponseList(List.of(allergen))).thenReturn(List.of(allergenResponse));

        assertEquals(HttpStatus.UNAUTHORIZED, controller.getMyProfile(null).getStatusCode());
        assertEquals(HttpStatus.OK, controller.getMyProfile(authentication).getStatusCode());
        assertEquals(HttpStatus.OK, controller.getById("u-1").getStatusCode());
        assertEquals(HttpStatus.OK, controller.updateMyProfile(authentication, mock(UserRequest.class)).getStatusCode());
        assertEquals(HttpStatus.OK, controller.listMyAllergens(authentication).getStatusCode());
        assertEquals(HttpStatus.CREATED, controller.addMyAllergen(authentication, "a1").getStatusCode());
        assertEquals(HttpStatus.CREATED, controller.addMyAllergensBatch(authentication, new AllergenIdListRequest(List.of("a1"))).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, controller.replaceMyAllergens(authentication, new AllergenIdListRequest(List.of("a1"))).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, controller.removeMyAllergen(authentication, "a1").getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, controller.removeMyAllergensBatch(authentication, new AllergenIdListRequest(List.of("a1"))).getStatusCode());
        assertEquals(HttpStatus.OK, controller.getAll().getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, controller.delete("u-1").getStatusCode());
        assertEquals(HttpStatus.OK, controller.changeUserRole("u-1", Role.ROLE_ADMIN).getStatusCode());

        verify(userServiceImpl).addAllergen("u-1", "a1");
        verify(userServiceImpl).replaceAllergens("u-1", List.of("a1"));
        verify(userServiceImpl).removeAllergensBulk("u-1", List.of("a1"));
    }
}
