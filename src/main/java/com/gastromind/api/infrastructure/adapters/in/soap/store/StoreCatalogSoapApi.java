package com.gastromind.api.infrastructure.adapters.in.soap.store;

import com.gastromind.api.infrastructure.adapters.in.soap.SoapNamespaces;
import com.gastromind.api.infrastructure.adapters.in.soap.dto.StoreSoapDto;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

/**
 * Tiendas canónicas consultables por SOAP; incluye nombre normalizado usado internamente para deduplicar tickets.
 */
@WebService(name = "StoreCatalog", targetNamespace = SoapNamespaces.CATALOG)
public interface StoreCatalogSoapApi {

    @WebMethod(operationName = "listStores")
    StoreSoapDto[] listStores();

    @WebMethod(operationName = "getStoreById")
    StoreSoapDto getStoreById(@WebParam(name = "id") String id);
}
