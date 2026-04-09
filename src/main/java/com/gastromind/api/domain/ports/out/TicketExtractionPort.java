package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.ticket.ExtractedTicketReceipt;

public interface TicketExtractionPort {

    /**
     * Lee una imagen de ticket y devuelve datos estructurados (Gemini multimodal).
     */
    ExtractedTicketReceipt extractFromImage(byte[] imageBytes, String mimeType);
}
