package com.gastromind.api.infrastructure.adapters.in.soap;

import jakarta.xml.ws.soap.SOAPFaultException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SoapCatalogFaultsTest {

    @Test
    void notFound_wrapsMessage() {
        SOAPFaultException ex = SoapCatalogFaults.notFound("missing");
        assertEquals("missing", ex.getFault().getFaultString());
    }

    @Test
    void notFound_nullMessage_usesDefault() {
        SOAPFaultException ex = SoapCatalogFaults.notFound(null);
        assertEquals("Not found", ex.getFault().getFaultString());
    }

    @Test
    void utilityClass_privateConstructor() throws Exception {
        var c = SoapCatalogFaults.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(c.getModifiers()));
    }
}
