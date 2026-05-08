package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.HouseHoldServiceImpl;
import com.gastromind.api.application.services.UserServiceImpl;
import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.HouseholdAppliance;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Appliance;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.household.ApplianceIdListRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.household.ApplianceSingleUpdateRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.household.ApplianceTypeListRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.household.HouseHoldRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.household.ApplianceResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.household.HouseHoldResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.user.UserResponse;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.same;

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

    @Test
    void adminAndOwnerEndpoints_shouldDelegateToServiceAndMapper() {
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

        HouseHold house = new HouseHold();
        house.setId("house-1");
        HouseHoldResponse houseResp = mock(HouseHoldResponse.class);
        UserResponse userResp = mock(UserResponse.class);
        HouseholdAppliance appliance = new HouseholdAppliance();
        ApplianceResponse applianceResp = mock(ApplianceResponse.class);

        User owner = new User();
        owner.setId("u-1");
        owner.setName("owner1");
        owner.setRole(Role.ROLE_OWNER);
        owner.setHouseHold_id(house);
        when(authentication.getName()).thenReturn("owner1");
        when(userServiceImpl.findByUsername("owner1")).thenReturn(owner);

        when(holdServiceImpl.findAll()).thenReturn(java.util.List.of(house));
        when(houseHoldMapper.toResponseList(java.util.List.of(house))).thenReturn(java.util.List.of(houseResp));
        when(holdServiceImpl.findById("house-1")).thenReturn(house);
        when(houseHoldMapper.toResponse(house)).thenReturn(houseResp);
        when(holdServiceImpl.create(any(HouseHold.class))).thenReturn(house);
        when(houseHoldMapper.toDomain(any(HouseHoldRequest.class))).thenReturn(house);
        when(holdServiceImpl.generateInviteToken("house-1")).thenReturn("tok-1");
        when(holdServiceImpl.addAppliance("house-1", Appliance.HORNO)).thenReturn(appliance);
        when(applianceRestMapper.toResponse(appliance)).thenReturn(applianceResp);
        when(holdServiceImpl.listAppliances("house-1")).thenReturn(java.util.List.of(appliance));
        when(applianceRestMapper.toResponseList(java.util.List.of(appliance))).thenReturn(java.util.List.of(applianceResp));
        when(holdServiceImpl.addAppliancesBulk(eq("house-1"), any())).thenReturn(java.util.List.of(appliance));
        when(holdServiceImpl.updateAppliance("house-1", "ar-1", Appliance.HORNO)).thenReturn(appliance);
        when(holdServiceImpl.promoteToOwner("house-1", "u-2")).thenReturn(owner);
        when(userRestMapper.toResponse(owner)).thenReturn(userResp);
        when(holdServiceImpl.listMembers("house-1")).thenReturn(java.util.List.of(owner));
        when(userRestMapper.toResponseList(java.util.List.of(owner))).thenReturn(java.util.List.of(userResp));
        when(holdServiceImpl.addMemberByToken("tok-join", "u-1")).thenReturn(owner);

        assertEquals(HttpStatus.OK, controller.getAll().getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, controller.delete("house-1").getStatusCode());
        assertEquals(HttpStatus.CREATED, controller.addAppliance("house-1", Appliance.HORNO).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, controller.removeMember("house-1", "u-2").getStatusCode());
        assertEquals(HttpStatus.OK, controller.invite("house-1").getStatusCode());
        assertEquals(HttpStatus.OK, controller.promoteToOwner("house-1", "u-2").getStatusCode());
        assertEquals(HttpStatus.OK, controller.getById("house-1").getStatusCode());
        assertEquals(HttpStatus.OK, controller.listMembers("house-1").getStatusCode());
        assertEquals(HttpStatus.CREATED, controller.create(mock(HouseHoldRequest.class)).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, controller.leave(authentication).getStatusCode());
        assertEquals(HttpStatus.OK, controller.listMyMembers(authentication).getStatusCode());
        assertEquals(HttpStatus.OK, controller.inviteMyHousehold(authentication).getStatusCode());
        assertEquals(HttpStatus.OK, controller.listMyAppliances(authentication).getStatusCode());
        assertEquals(HttpStatus.CREATED, controller.addMyAppliance(authentication, Appliance.HORNO).getStatusCode());
        assertEquals(HttpStatus.CREATED, controller.addMyAppliancesBatch(authentication, new ApplianceTypeListRequest(java.util.List.of(Appliance.HORNO))).getStatusCode());
        assertEquals(HttpStatus.OK, controller.updateMyAppliance(authentication, "ar-1", new ApplianceSingleUpdateRequest(Appliance.HORNO)).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, controller.deleteMyAppliance(authentication, "ar-1").getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, controller.deleteMyAppliancesBatch(authentication, new ApplianceIdListRequest(java.util.List.of("ar-1"))).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, controller.removeMyMember(authentication, "u-2").getStatusCode());
        assertEquals(HttpStatus.OK, controller.promoteMyMemberToOwner(authentication, "u-2").getStatusCode());
        assertEquals(HttpStatus.OK, controller.joinMyUserWithInvite(authentication, "tok-join").getStatusCode());
    }

    @Test
    void ownerOnlyEndpoints_shouldRejectNonOwner() {
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

        HouseHold house = new HouseHold();
        house.setId("house-1");
        User member = new User();
        member.setName("member1");
        member.setRole(Role.ROLE_MEMBER);
        member.setHouseHold_id(house);
        when(authentication.getName()).thenReturn("member1");
        when(userServiceImpl.findByUsername("member1")).thenReturn(member);

        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> controller.addMyAppliance(authentication, Appliance.HORNO));
        assertEquals("Solo el OWNER del hogar puede gestionar los electrodomesticos", ex.getMessage());
    }

    @Test
    void addMyAppliancesBatch_shouldReturnExpectedPostBody() {
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

        HouseHold house = new HouseHold();
        house.setId("house-1");
        User owner = new User();
        owner.setName("owner1");
        owner.setRole(Role.ROLE_OWNER);
        owner.setHouseHold_id(house);

        ApplianceTypeListRequest request = new ApplianceTypeListRequest(java.util.List.of(
                Appliance.HORNO,
                Appliance.MICROONDAS
        ));

        HouseholdAppliance row1 = new HouseholdAppliance("ap-1", Appliance.HORNO, "house-1");
        HouseholdAppliance row2 = new HouseholdAppliance("ap-2", Appliance.MICROONDAS, "house-1");
        java.util.List<HouseholdAppliance> serviceOut = java.util.List.of(row1, row2);

        ApplianceResponse response1 = new ApplianceResponse();
        response1.setId("ap-1");
        response1.setAppliance(Appliance.HORNO);
        response1.setHouseholdId("house-1");
        ApplianceResponse response2 = new ApplianceResponse();
        response2.setId("ap-2");
        response2.setAppliance(Appliance.MICROONDAS);
        response2.setHouseholdId("house-1");
        java.util.List<ApplianceResponse> mapped = java.util.List.of(response1, response2);

        when(authentication.getName()).thenReturn("owner1");
        when(userServiceImpl.findByUsername("owner1")).thenReturn(owner);
        when(holdServiceImpl.addAppliancesBulk(eq("house-1"), same(request.appliances()))).thenReturn(serviceOut);
        when(applianceRestMapper.toResponseList(serviceOut)).thenReturn(mapped);

        var result = controller.addMyAppliancesBatch(authentication, request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());
        assertEquals("ap-1", result.getBody().get(0).getId());
        assertEquals(Appliance.HORNO, result.getBody().get(0).getAppliance());
        assertEquals("house-1", result.getBody().get(0).getHouseholdId());
        assertEquals("ap-2", result.getBody().get(1).getId());
        assertEquals(Appliance.MICROONDAS, result.getBody().get(1).getAppliance());
        assertEquals("house-1", result.getBody().get(1).getHouseholdId());
        assertTrue(result.getBody() == mapped);
    }
}
