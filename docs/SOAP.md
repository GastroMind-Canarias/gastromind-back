# SOAP (catálogos de solo lectura)

La API expone además de REST varios **servicios SOAP 1.1** (JAX-WS) montados con **Apache CXF**. Sirven para requisitos académicos o integraciones “legacy”; el producto principal sigue siendo REST + JWT.

## Qué hay implementado

| Servicio (WSDL relativo a `/soap`) | Operaciones | Origen de datos |
| --- | --- | --- |
| `categoryCatalog` | `listCategories`, `getCategoryById` | `CategoryServiceImpl` |
| `unitCatalog` | `listUnits`, `getUnitById` | `UnitServiceImpl` |
| `allergenCatalog` | `listAllergens`, `getAllergenById` | `AllergenServiceImpl` |
| `productCatalog` | `listProducts`, `getProductById` | `ProductServiceImpl` |
| `storeCatalog` | `listStores`, `getStoreById` | `StoreServiceImpl` |

Código: paquete `com.gastromind.api.infrastructure.adapters.in.soap`, configuración de publicación en `SoapCatalogEndpointConfiguration`.

## URL base y WSDL

El prefijo del servlet CXF se define con `cxf.path` (por defecto en este repo: **`/soap`**). Con la API en el puerto configurado en `server.port` (en `application.yaml` suele ser **8081**), el WSDL de cada servicio es:

`http://localhost:8081/soap/<nombreServicio>?wsdl`

Sustituye `<nombreServicio>` por `categoryCatalog`, `unitCatalog`, `allergenCatalog`, `productCatalog` o `storeCatalog`.

Ejemplo con curl (salida larga; basta ver que empieza por XML del WSDL):

```bash
curl -s "http://localhost:8081/soap/categoryCatalog?wsdl"
```

En Windows puedes abrir la misma URL en el navegador y comprobar que devuelve XML con `definitions`.

## Seguridad

Las rutas bajo `/soap/**` están en la lista de **URLs públicas** (`gastromind.security.public-urls`) para poder demostrar los servicios sin JWT. Eso implica que cualquiera que alcance la red puede leer esos catálogos. Para un entorno real habría que sustituir esto por API key, mTLS, VPN o WS-Security según el caso.

## Cómo probar operaciones

1. **SoapUI** o **Postman**: importar proyecto desde WSDL (`…?wsdl`), generar la petición SOAP y enviar (por ejemplo `listCategories` sin cuerpo o `getCategoryById` con el `id` que pida el contrato).
2. **Cliente Java generado**: herramientas tipo `wsimport` / equivalentes Jakarta contra la URL del WSDL (queda fuera del alcance de este documento, pero el contrato es estándar WSDL 1.1).

## Dependencias Maven relevantes

- `cxf-spring-boot-starter-jaxws` (JAX-WS sobre el mismo Tomcat embebido).
- `saaj-impl` (implementación SAAJ para `SOAPFactory` en runtime y en tests).

## Tests automáticos

Los endpoints tienen tests unitarios con Mockito bajo `src/test/java/.../infrastructure/adapters/in/soap/`.
