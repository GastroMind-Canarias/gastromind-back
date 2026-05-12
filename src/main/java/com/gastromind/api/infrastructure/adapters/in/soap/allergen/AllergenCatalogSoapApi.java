package com.gastromind.api.infrastructure.adapters.in.soap.allergen;

import com.gastromind.api.infrastructure.adapters.in.soap.SoapNamespaces;
import com.gastromind.api.infrastructure.adapters.in.soap.dto.AllergenSoapDto;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

/**
 * Catálogo de alérgenos vía SOAP; mismo origen de datos que el panel REST de administración.
 */
@WebService(name = "AllergenCatalog", targetNamespace = SoapNamespaces.CATALOG)
public interface AllergenCatalogSoapApi {

    @WebMethod(operationName = "listAllergens")
    AllergenSoapDto[] listAllergens();

    @WebMethod(operationName = "getAllergenById")
    AllergenSoapDto getAllergenById(@WebParam(name = "id") String id);
}
