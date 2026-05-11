package com.gastromind.api.infrastructure.adapters.in.soap;

/**
 * Namespace único del WSDL de catálogo SOAP; evita choques con otros módulos si algún día se añaden más servicios.
 */
public final class SoapNamespaces {

    public static final String CATALOG = "http://gastromind.com/soap/catalog";

    private SoapNamespaces() {
    }
}
