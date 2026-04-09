package com.gastromind.api.infrastructure.adapters.out.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ai.ticket")
public class TicketImageProperties {

    /**
     * Tamaño máximo aceptado para la imagen del ticket (bytes).
     */
    private long maxImageBytes = 10 * 1024 * 1024;

    public long getMaxImageBytes() {
        return maxImageBytes;
    }

    public void setMaxImageBytes(long maxImageBytes) {
        this.maxImageBytes = maxImageBytes;
    }
}
