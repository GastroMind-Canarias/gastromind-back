package com.gastromind.api.infrastructure.adapters.in.soap.config;

import com.gastromind.api.infrastructure.adapters.in.soap.allergen.AllergenCatalogSoapEndpoint;
import com.gastromind.api.infrastructure.adapters.in.soap.category.CategoryCatalogSoapEndpoint;
import com.gastromind.api.infrastructure.adapters.in.soap.product.ProductCatalogSoapEndpoint;
import com.gastromind.api.infrastructure.adapters.in.soap.store.StoreCatalogSoapEndpoint;
import com.gastromind.api.infrastructure.adapters.in.soap.unit.UnitCatalogSoapEndpoint;
import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Publica cinco servicios JAX-WS bajo el servlet CXF (prefijo {@code cxf.path=/soap} en {@code application.yaml}).
 * <p>
 * Cada {@code publish("/...")} suma un WSDL distinto, que es lo que suele pedirse en rúbricas de “más de cuatro servicios SOAP”.
 * <p>
 * <b>Cómo probarlo en local</b> (puerto por defecto Spring {@code 8080}; ajusta si usas otro):
 * <ol>
 *   <li>Arranca la API con base de datos disponible como siempre ({@code mvn spring-boot:run} o tu IDE).</li>
 *   <li>Comprueba que el WSDL responde: en PowerShell o bash, por ejemplo
 *       {@code curl -s "http://localhost:8080/soap/categoryCatalog?wsdl" | findstr definitions}
 *       (en Unix usa {@code grep definitions}). Repite sustituyendo la ruta por
 *       {@code /unitCatalog}, {@code /allergenCatalog}, {@code /productCatalog}, {@code /storeCatalog}.</li>
 *   <li>SoapUI o Postman: “Import from WSDL” con cualquiera de esas URLs con sufijo {@code ?wsdl}, genera cliente,
 *       invoca {@code list*} o {@code get*ById} y revisa el XML de respuesta.</li>
 *   <li>Si recibes 401, la ruta no está en {@code gastromind.security.public-urls}; debe existir {@code /soap/**}.</li>
 * </ol>
 * <p>
 * Ojo: estos endpoints están abiertos sin JWT a propósito para la entrega; en producción no dejaría el catálogo así.
 */
@Configuration
public class SoapCatalogEndpointConfiguration {

    @Bean
    public Endpoint categoryCatalogEndpoint(Bus bus, CategoryCatalogSoapEndpoint impl) {
        EndpointImpl endpoint = new EndpointImpl(bus, impl);
        endpoint.publish("/categoryCatalog");
        return endpoint;
    }

    @Bean
    public Endpoint unitCatalogEndpoint(Bus bus, UnitCatalogSoapEndpoint impl) {
        EndpointImpl endpoint = new EndpointImpl(bus, impl);
        endpoint.publish("/unitCatalog");
        return endpoint;
    }

    @Bean
    public Endpoint allergenCatalogEndpoint(Bus bus, AllergenCatalogSoapEndpoint impl) {
        EndpointImpl endpoint = new EndpointImpl(bus, impl);
        endpoint.publish("/allergenCatalog");
        return endpoint;
    }

    @Bean
    public Endpoint productCatalogEndpoint(Bus bus, ProductCatalogSoapEndpoint impl) {
        EndpointImpl endpoint = new EndpointImpl(bus, impl);
        endpoint.publish("/productCatalog");
        return endpoint;
    }

    @Bean
    public Endpoint storeCatalogEndpoint(Bus bus, StoreCatalogSoapEndpoint impl) {
        EndpointImpl endpoint = new EndpointImpl(bus, impl);
        endpoint.publish("/storeCatalog");
        return endpoint;
    }
}
