package com.gastromind.api.infrastructure.adapters.in.soap;

import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.soap.SOAPFault;
import jakarta.xml.ws.soap.SOAPFaultException;

import javax.xml.namespace.QName;

/**
 * Convierte fallos de dominio legibles en culpa SOAP para que SoapUI muestre un mensaje entendible.
 * Arma el {@link SOAPFault} con {@link SOAPFactory#createFault()} vacío y luego rellena código y texto;
 * así evitamos la sobrecarga que exige un {@code QName} de subcódigo y suele romperse en entornos mínimos.
 */
public final class SoapCatalogFaults {

    private SoapCatalogFaults() {
    }

    /**
     * Recurso pedido no existe en base de datos; equivale a HTTP 404 pero en mundo SOAP.
     */
    public static SOAPFaultException notFound(String message) {
        try {
            SOAPFactory factory = SOAPFactory.newInstance();
            SOAPFault fault = factory.createFault();
            fault.setFaultString(message != null ? message : "Not found");
            fault.setFaultCode(new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE, "Client"));
            return new SOAPFaultException(fault);
        } catch (SOAPException e) {
            throw new IllegalStateException("No se pudo construir SOAPFault", e);
        }
    }
}
