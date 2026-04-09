package com.gastromind.api.infrastructure.security.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Cómo vincular el usuario a un hogar en el registro.
 * La app móvil puede enviar el modo explícito o deducirlo de {@code inviteToken} / {@code householdName}.
 */
@Schema(description = "CREATE_NEW: crear hogar y ser OWNER. JOIN_EXISTING: unirse con código de invitación como MEMBER.")
public enum HouseholdRegistrationMode {
    CREATE_NEW,
    JOIN_EXISTING
}
