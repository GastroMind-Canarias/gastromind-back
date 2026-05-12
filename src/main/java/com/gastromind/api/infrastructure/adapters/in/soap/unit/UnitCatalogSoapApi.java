package com.gastromind.api.infrastructure.adapters.in.soap.unit;

import com.gastromind.api.infrastructure.adapters.in.soap.SoapNamespaces;
import com.gastromind.api.infrastructure.adapters.in.soap.dto.UnitSoapDto;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

/**
 * Contrato de unidades de medida para clientes SOAP; espejo de solo lectura del servicio de aplicación.
 */
@WebService(name = "UnitCatalog", targetNamespace = SoapNamespaces.CATALOG)
public interface UnitCatalogSoapApi {

    @WebMethod(operationName = "listUnits")
    UnitSoapDto[] listUnits();

    @WebMethod(operationName = "getUnitById")
    UnitSoapDto getUnitById(@WebParam(name = "id") String id);
}
