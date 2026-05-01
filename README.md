# GastroMind API: El Cerebro de la Cocina Inteligente YYZ

GastroMind no es solo una lista de la compra o un recetario; es un ecosistema de Inteligencia Artificial disenado para optimizar la economia domestica y erradicar el desperdicio alimentario. Actua como el centro neuralgico que conecta el ticket de compra, el inventario real y la mesa del usuario.

A diferencia de las soluciones pasivas, GastroMind es proactiva:
- **Conciencia de Inventario:** Sabe que tienes y cuando caduca.
- **Conciencia de Equipo (Hardware Aware):** Filtra su conocimiento basandose en tus electrodomesticos (Air Fryer, Horno, etc.).
- **Economia Circular:** Transforma datos de tickets borrosos en analiticas de gasto y stock en tiempo real mediante IA multimodal.

Este es un motor API REST disenado para procesar grandes volumenes de datos de inventario, gestionar la seguridad alimentaria de multiples hogares y servir como puente seguro entre la base de datos relacional.

## Arquitectura del Proyecto

El proyecto sigue una **Arquitectura Hexagonal (Puertos y Adaptadores)**, lo que permite una total independencia entre la logica de negocio (nucleo) y las tecnologias externas (bases de datos, frameworks, APIs).

### Estructura de Paquetes

* **`domain`**: Contiene el corazon de la aplicacion. Modelos de negocio pura, excepciones de dominio e interfaces (puertos) que definen como el sistema interactua con el exterior.
* **`infrastructure`**: Implementacion de los adaptadores.
* **`adapters.in.rest`**: Controladores que exponen la API y gestion global de excepciones.
* **`adapters.out.persistence`**: Implementaciones de persistencia utilizando **PostgreSQL** (JPA) y **MongoDB**.


* **`security`**: Configuracion centralizada de seguridad, gestion de JWT y politicas de acceso.

---

## Tecnologias y Dependencias

El stack tecnologico ha sido seleccionado para garantizar escalabilidad, seguridad y una documentacion automatica robusta.

### Core Framework

* **Spring Boot 3**: Framework base para el desarrollo de microservicios.
* **Spring Security & JWT**: Implementacion de seguridad basada en tokens para una autenticacion stateless.
* **Validation**: Aseguramiento de la integridad de los datos de entrada mediante anotaciones.

### Persistencia y Datos

* **Spring Data JPA & PostgreSQL**: Gestion de datos relacionales para usuarios y hogares.
* **Spring Data MongoDB**: Almacenamiento flexible para registros de inventario o logs.
* **MapStruct**: Mapeo eficiente de objetos entre capas (DTOs, Domain Models y Entities) para mantener el desacoplamiento.

### Documentacion y Desarrollo

* **SpringDoc OpenAPI (Swagger)**: Interfaz interactiva para pruebas y documentacion tecnica de los endpoints.
* **Docker Compose Support**: Gestion automatizada del entorno de desarrollo (PostgreSQL, MongoDB, pgAdmin) integrada con Spring Boot.

---

## Entorno de Desarrollo (Docker)

La infraestructura local se levanta de forma automatizada mediante contenedores, facilitando el despliegue inmediato del entorno de base de datos y herramientas de administracion.

| Servicio | Puerto | Descripcion |
| --- | --- | --- |
| **PostgreSQL** | `5432` | Base de datos relacional principal. |
| **MongoDB** | `27017` | Almacenamiento de documentos no relacionales. |
| **pgAdmin** | `5050` | Panel de administracion web para PostgreSQL. |
