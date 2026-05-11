package com.gastromind.api.infrastructure.adapters.in.soap.category;

import com.gastromind.api.infrastructure.adapters.in.soap.SoapNamespaces;
import com.gastromind.api.infrastructure.adapters.in.soap.dto.CategorySoapDto;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

/**
 * Contrato WSDL de categorías: solo lectura sobre el mismo catálogo que ya alimenta el REST.
 */
@WebService(name = "CategoryCatalog", targetNamespace = SoapNamespaces.CATALOG)
public interface CategoryCatalogSoapApi {

    @WebMethod(operationName = "listCategories")
    CategorySoapDto[] listCategories();

    @WebMethod(operationName = "getCategoryById")
    CategorySoapDto getCategoryById(@WebParam(name = "id") String id);
}
