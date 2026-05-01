package com.gastromind.api.infrastructure.security.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "CREATE_NEW: crear hogar y ser OWNER. JOIN_EXISTING: unirse con codigo de invitacion como MEMBER.")
/**
 * Declara los valores permitidos para household registration mode.
 */
public enum HouseholdRegistrationMode {
    CREATE_NEW,
    JOIN_EXISTING
}






