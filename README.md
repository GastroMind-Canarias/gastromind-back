# GastroMind API

API REST para inventario doméstico, tickets de compra y sugerencias de receta con Google Gemini. La app corre en el puerto **8081** y usa PostgreSQL como base principal.

**Objetivo de esta guía:** tener Postgres, Redis y la API funcionando en local en unos minutos.

## Stack

| Capa | Tecnología |
| --- | --- |
| Runtime | Java 21, Spring Boot 3.5 |
| API | Spring Web, Validation, Spring Security + JWT |
| Datos | PostgreSQL (JPA), Redis (caché y límites) |
| IA | Gemini API (`GEMINI_API_KEY`) |
| Docs | SpringDoc OpenAPI (Swagger) |
| Observabilidad | Spring Boot Actuator, Micrometer Prometheus |
| Contenedores | Docker Compose (Postgres, MongoDB, Redis, pgAdmin, Prometheus opcional) |

MongoDB aparece en `compose.yaml` y hay dependencia Maven, pero **el código de aplicación no persiste aún en MongoDB**; puedes ignorar ese servicio para arrancar solo lo que usa la API.

## Requisitos previos

- **JDK 21** y **Maven 3.9+**
- **Docker** y Docker Compose (para bases y Redis en local)
- **GEMINI_API_KEY** si vas a usar recetas por IA o extracción de tickets desde imagen (sin clave, esas operaciones devuelven error indicando que falta configuración)

## Inicio rápido

### 1. Alinear PostgreSQL con la aplicación

En [`src/main/resources/application.yaml`](src/main/resources/application.yaml) el datasource por defecto es:

- URL: `jdbc:postgresql://localhost:5432/gastromind_local`
- Usuario: `myuser`
- Contraseña: `secret`

Tu contenedor Postgres debe exponer **5432** en el host y usar ese mismo usuario, contraseña y base de datos **o** sobrescribe al arrancar:

```bash
set SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:PUERTO/BASE
set SPRING_DATASOURCE_USERNAME=usuario
set SPRING_DATASOURCE_PASSWORD=clave
```

(Linux/macOS: `export` en lugar de `set`.)

### 2. Variables para Docker Compose

El fichero [`compose.yaml`](compose.yaml) usa variables como `ENV_NAME`, `DB_NAME`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_PORT`, `REDIS_PORT`, etc. Define un `.env` en la raíz del repo (no está versionado) de modo que Postgres y Redis coincidan con lo que usará Spring (`localhost` y los puertos mapeados).

Ejemplo mínimo coherente con el YAML por defecto de la app:

```env
ENV_NAME=local
DB_NAME=gastromind_local
POSTGRES_USER=usuario
POSTGRES_PASSWORD=contraseña
POSTGRES_PORT=5432
MONGO_USER=usuario
MONGO_PASS=contraseña
MONGO_PORT=27017
REDIS_PORT=6379
PGADMIN_PASS=contraseña
PGADMIN_PORT=5050
```

### 3. Levantar infraestructura y la API

```bash
docker compose up -d postgres redis
```

Opcional: Prometheus para métricas (`docker compose up -d prometheus`). La UI de Prometheus suele estar en `http://localhost:9090`; las métricas de la aplicación se publican en **`http://localhost:8081/actuator/prometheus`** (véase [docs/OBSERVABILITY.md](docs/OBSERVABILITY.md)).

```bash
mvn spring-boot:run
```

El proyecto incluye `spring-boot-docker-compose`: si prefieres que Spring gestione el ciclo del Compose, revisa la documentación de Spring Boot 3 para activarlo según tu flujo; el comando anterior asume que ya tienes Postgres y Redis arriba.

### 4. Comprobar que responde

| Qué | URL |
| --- | --- |
| Salud | `GET http://localhost:8081/actuator/health` |
| Swagger UI | `http://localhost:8081/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8081/v3/api-docs` |

Para rutas de negocio necesitas JWT (registro/login bajo `/api/v1/auth/`).

## Documentación adicional

- [Arquitectura y ADR](docs/ARCHITECTURE.md)
- [Observabilidad (Prometheus, Actuator)](docs/OBSERVABILITY.md)
- [Integración con IA (Gemini)](docs/AI_INTEGRATION.md)
- [Casos de uso / flujos](docs/diagrama-flujo.md)
- [Especificación de diseño Fridge Me (histórico)](docs/superpowers/specs/2026-04-09-fridge-me-flow-design.md)
- [Plan de implementación relacionado](docs/superpowers/plans/2026-04-09-fridge-me-flow-implementation-plan.md)
