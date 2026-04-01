package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.HouseHoldServiceImpl;
import com.gastromind.api.application.services.UserServiceImpl;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.household.HouseHoldResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.HouseHoldRestMapper;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.HouseholdApplianceRestMapper;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.UserRestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class HouseHoldControllerTest {

    @Test
    void getMyHousehold_shouldResolveHouseholdFromAuthenticatedUser() throws Exception {
        HouseHoldServiceImpl holdServiceImpl = mock(HouseHoldServiceImpl.class);
        UserServiceImpl userServiceImpl = mock(UserServiceImpl.class);
        HouseHoldRestMapper houseHoldMapper = mock(HouseHoldRestMapper.class);
        HouseholdApplianceRestMapper applianceRestMapper = mock(HouseholdApplianceRestMapper.class);
        UserRestMapper userRestMapper = mock(UserRestMapper.class);
        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);

        HouseHoldController controller = new HouseHoldController();
        ReflectionTestUtils.setField(controller, "holdServiceImpl", holdServiceImpl);
        ReflectionTestUtils.setField(controller, "houseHoldMapper", houseHoldMapper);
        ReflectionTestUtils.setField(controller, "applianceRestMapper", applianceRestMapper);
        ReflectionTestUtils.setField(controller, "userRestMapper", userRestMapper);
        ReflectionTestUtils.setField(controller, "userServiceImpl", userServiceImpl);

        HouseHold myHousehold = new HouseHold();
        myHousehold.setId("house-123");
        myHousehold.setName("Mi Hogar");

        User authenticatedUser = new User();
        authenticatedUser.setId("user-1");
        authenticatedUser.setName("owner1");
        authenticatedUser.setRole(Role.ROLE_OWNER);
        authenticatedUser.setHouseHold_id(myHousehold);

        HouseHoldResponse response = new HouseHoldResponse(
                "house-123",
                "Mi Hogar",
                0,
                java.util.List.of(),
                java.util.List.of()
        );

        when(authentication.getName()).thenReturn("owner1");
        when(userServiceImpl.findByUsername("owner1")).thenReturn(authenticatedUser);
        when(holdServiceImpl.findById("house-123")).thenReturn(myHousehold);
        when(houseHoldMapper.toResponse(myHousehold)).thenReturn(response);

        var result = controller.getMyHousehold(authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("house-123", result.getBody().id());
        assertEquals("Mi Hogar", result.getBody().name());

        verify(userServiceImpl).findByUsername("owner1");
        verify(holdServiceImpl).findById("house-123");
    }
}
