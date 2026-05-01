package com.gastromind.api.infrastructure.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gastromind.security")
/**
 * Propiedades con rutas publicas excluidas de autenticacion.
 */
public class SecurityPathsProperties {
    private String[] publicUrls;
    /** Devuelve el listado de URLs publicas configuradas. */

    public String[] getPublicUrls() {
        return publicUrls;
    }
    /** Sustituye el listado de URLs publicas permitidas. */

    public void setPublicUrls(String[] publicUrls) {
        this.publicUrls = publicUrls;
    }
}




