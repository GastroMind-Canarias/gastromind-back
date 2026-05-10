package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.ticket.ExtractedTicketReceipt;

/**
 * Extrae cabecera y líneas de un ticket a partir de bytes de imagen (Gemini u otro adaptador).
 */
public interface TicketExtractionPort {

    ExtractedTicketReceipt extractFromImage(byte[] imageBytes, String mimeType);
}
