package com.gastromind.api.application.usecases;

import com.gastromind.api.domain.models.PendingStore;
import com.gastromind.api.domain.models.Ticket;

/**
 * Salida del import por foto: ticket persistido y, si hace falta, tienda detectada aún sin promover.
 */
public record ImportTicketFromImageResult(Ticket ticket, PendingStore pendingStore, String detectedStoreName) {
    public boolean unresolvedStore() {
        return pendingStore != null;
    }
}
