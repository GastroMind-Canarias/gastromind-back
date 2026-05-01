package com.gastromind.api.infrastructure.adapters.out.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ai.ticket")
/**
 * Representa ticket image dentro del dominio de la aplicacion.
 */
public class TicketImageProperties {

    private long maxImageBytes = 10 * 1024 * 1024;
    /**
     * Devuelve max image bytes.
     * @return valor configurado.
     */

    public long getMaxImageBytes() {
        return maxImageBytes;
    }
    /**
     * Define max image bytes.
     * @param maxImageBytes valor a utilizar.
     */

    public void setMaxImageBytes(long maxImageBytes) {
        this.maxImageBytes = maxImageBytes;
    }
}




