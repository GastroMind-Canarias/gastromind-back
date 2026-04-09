package com.gastromind.api.domain.models.enums;

/**
 * Estado de revisión de una línea de ticket importada (p. ej. desde imagen + IA).
 */
public enum TicketLineVerificationStatus {
    /** Pendiente de que el usuario confirme cantidad/unidad/precio o el producto. */
    PENDING_REVIEW,
    /** Confirmada por el usuario o creada manualmente sin incidencias. */
    OK
}
