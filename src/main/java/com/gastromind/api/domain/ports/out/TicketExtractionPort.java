package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.ticket.ExtractedTicketReceipt;

/**
 * Define el contrato de persistencia o integracion para ticket extraction.
 */
public interface TicketExtractionPort {

    ExtractedTicketReceipt extractFromImage(byte[] imageBytes, String mimeType);
}
