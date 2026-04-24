package com.gastromind.api.infrastructure.security.auth.services.impl;

import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.domain.ports.out.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SecurityService securityService;

    @BeforeEach
    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void isOwnerOfHousehold_adminAlwaysTrue() {
        User admin = user(Role.ROLE_ADMIN, "h1");
        auth("admin");
        when(userRepository.findByName("admin")).thenReturn(Optional.of(admin));
        assertTrue(securityService.isOwnerOfHousehold("any"));
    }

    @Test
    void isMemberOfHousehold_adminAlwaysTrue() {
        User admin = user(Role.ROLE_ADMIN, "h1");
        auth("adm2");
        when(userRepository.findByName("adm2")).thenReturn(Optional.of(admin));
        assertTrue(securityService.isMemberOfHousehold("any-house"));
    }

    @Test
    void isOwnerOfHousehold_ownerMatchingHouse() {
        User owner = user(Role.ROLE_OWNER, "h1");
        auth("o");
        when(userRepository.findByName("o")).thenReturn(Optional.of(owner));
        assertTrue(securityService.isOwnerOfHousehold("h1"));
        assertFalse(securityService.isOwnerOfHousehold("h2"));
    }

    @Test
    void isMemberOfHousehold_memberOrAdmin() {
        User member = user(Role.ROLE_MEMBER, "h1");
        auth("m");
        when(userRepository.findByName("m")).thenReturn(Optional.of(member));
        assertTrue(securityService.isMemberOfHousehold("h1"));
        assertFalse(securityService.isMemberOfHousehold("h2"));
    }

    @Test
    void getCurrentUser_fallsBackToEmail() {
        User u = user(Role.ROLE_OWNER, "h1");
        auth("e@x.com");
        when(userRepository.findByName("e@x.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("e@x.com")).thenReturn(Optional.of(u));
        assertTrue(securityService.isMemberOfHousehold("h1"));
    }

    @Test
    void getCurrentUser_throwsWhenAnonymous() {
        SecurityContextHolder.getContext().setAuthentication(
                new AnonymousAuthenticationToken("key", "anon",
                        AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));
        assertThrows(ForbiddenException.class, () -> securityService.isMemberOfHousehold("h1"));
    }

    @Test
    void getCurrentUser_throwsWhenUserNotResolved() {
        auth("ghost");
        when(userRepository.findByName("ghost")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("ghost")).thenReturn(Optional.empty());
        assertThrows(ForbiddenException.class, () -> securityService.isMemberOfHousehold("h1"));
    }

    @Test
    void getCurrentUser_throwsWhenAuthenticationNull() {
        SecurityContextHolder.clearContext();
        assertThrows(ForbiddenException.class, () -> securityService.isMemberOfHousehold("h1"));
    }

    @Test
    void getCurrentUser_throwsWhenNotAuthenticated() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("u", "p"));
        assertThrows(ForbiddenException.class, () -> securityService.isMemberOfHousehold("h1"));
    }

    @Test
    void isOwnerOfHousehold_falseForMember() {
        User member = user(Role.ROLE_MEMBER, "h1");
        auth("mem");
        when(userRepository.findByName("mem")).thenReturn(Optional.of(member));
        assertFalse(securityService.isOwnerOfHousehold("h1"));
    }

    @Test
    void isOwnerOfHousehold_falseWhenOwnerHasNoHousehold() {
        User owner = user(Role.ROLE_OWNER, "h1");
        owner.setHouseHold_id(null);
        auth("o2");
        when(userRepository.findByName("o2")).thenReturn(Optional.of(owner));
        assertFalse(securityService.isOwnerOfHousehold("h1"));
    }

    @Test
    void isMemberOfHousehold_falseWhenHouseholdNull() {
        User u = user(Role.ROLE_MEMBER, "h1");
        u.setHouseHold_id(null);
        auth("nom");
        when(userRepository.findByName("nom")).thenReturn(Optional.of(u));
        assertFalse(securityService.isMemberOfHousehold("h1"));
    }

    private static void auth(String name) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(name, "x", java.util.List.of()));
    }

    private static User user(Role role, String householdId) {
        HouseHold h = new HouseHold();
        h.setId(householdId);
        User u = new User();
        u.setRole(role);
        u.setHouseHold_id(h);
        return u;
    }
}
