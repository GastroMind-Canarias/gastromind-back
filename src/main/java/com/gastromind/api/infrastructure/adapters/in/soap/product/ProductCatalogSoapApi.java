package com.gastromind.api.infrastructure.adapters.in.soap.product;

import com.gastromind.api.infrastructure.adapters.in.soap.SoapNamespaces;
import com.gastromind.api.infrastructure.adapters.in.soap.dto.ProductSoapDto;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

/**
 * Productos del catálogo global expuestos como operaciones SOAP de consulta.
 */
@WebService(name = "ProductCatalog", targetNamespace = SoapNamespaces.CATALOG)
public interface ProductCatalogSoapApi {

    @WebMethod(operationName = "listProducts")
    ProductSoapDto[] listProducts();

    @WebMethod(operationName = "getProductById")
    ProductSoapDto getProductById(@WebParam(name = "id") String id);
}
