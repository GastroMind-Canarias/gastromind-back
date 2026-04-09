package com.gastromind.api.infrastructure.security.auth.services.impl;

import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.domain.ports.out.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("securityService")
public class SecurityService {

    @Autowired
    private UserRepository userRepository;

    public boolean isOwnerOfHousehold(String householdId) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() == Role.ROLE_ADMIN) {
            return true;
        }
        return currentUser.getRole() == Role.ROLE_OWNER
                && currentUser.getHouseHold_id() != null
                && currentUser.getHouseHold_id().getId().equals(householdId);
    }

    /** Miembro del hogar (solo lectura) o administrador global. */
    public boolean isMemberOfHousehold(String householdId) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() == Role.ROLE_ADMIN) {
            return true;
        }
        return currentUser.getHouseHold_id() != null
                && currentUser.getHouseHold_id().getId().equals(householdId);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new ForbiddenException("Usuario no autenticado");
        }

        String principal = authentication.getName();

        return userRepository.findByName(principal)
                .or(() -> userRepository.findByEmail(principal))
                .orElseThrow(() -> new ForbiddenException("No se pudo resolver el usuario autenticado"));
    }
}
