# Observabilidad

## Stack

| Pieza | Función |
| --- | --- |
| **Spring Boot Actuator** | Expone endpoints operativos (`health`, `info`, métricas). |
| **Micrometer + registry Prometheus** | Exporta métricas en formato que Prometheus puede recolectar. |
| **Prometheus** | Almacena series temporales y ofrece UI/API en el puerto **9090** del contenedor (sin Grafana en este repo). |

No hay Grafana ni otros frontends de métricas versionados aquí; si necesitas dashboards, puedes apuntar Grafana u otra herramienta al mismo Prometheus.

## Cómo encajan los puertos

Importante no confundir puertos:

| Puerto | Servicio |
| --- | --- |
| **8081** | API Spring Boot (incluye `/actuator/*` en el mismo proceso HTTP). |
| **9090** | Servidor **Prometheus** en Docker (`compose.yaml`), interfaz y API de consulta de Prometheus. |

Las métricas de la aplicación salen en:

`http://<host>:8081/actuator/prometheus`

La configuración de scrape está en [`prometheus/prometheus.yml`](../prometheus/prometheus.yml): intervalo **15s**, job `gastromind-api`, `metrics_path: /actuator/prometheus`, target por defecto `host.docker.internal:8081` para que el contenedor Prometheus alcance la API en el host.

## Métricas útiles (orientativas)

Micrometer exporta el conjunto habitual de Spring Boot 3, entre otras:

- **Salud:** `GET /actuator/health` (y detalle si lo habilitas en configuración).
- **HTTP:** `http.server.requests` (latencias, status, métodos).
- **JVM:** `jvm.memory.used`, `jvm.gc.pause`, `jvm.threads.live`, etc.
- **Proceso:** `process.uptime`, `process.cpu.usage`.
- **Hilo del servidor:** métricas del contenedor embebido Tomcat si aplican.

Los nombres exactos pueden variar con la versión de Boot; inspecciona `/actuator/prometheus` o la UI de Prometheus en **Targets** para confirmar que el scrape es **UP**.

## Seguridad: estado actual

- En [`src/main/resources/application.yaml`](../src/main/resources/application.yaml), la lista `gastromind.security.public-urls` incluye **`/actuator/**`**. El filtro JWT no exige token para esas rutas.
- Prometheus en desarrollo publica **`9090:9090`** en el host: cualquier proceso en la máquina puede abrir la UI de Prometheus si no hay firewall local.

Esto facilita el scrape y la depuración en local; **no es un modelo listo para exponer a Internet sin capas adicionales.**

## Endurecimiento recomendado (producción)

Sin cambiar este documento por código nuevo, suele aplicarse:

1. **Red:** No exponer Prometheus (9090) hacia Internet; usar red Docker privada, firewall o VPN; si solo necesitas métricas internas, evita publicar el puerto en el host y deja scrape solo entre contenedores en la misma red.
2. **Actuator:** Valorar sacar `/actuator/**` de URLs públicas y proteger `/actuator/prometheus` con autenticación (reverse proxy con Basic Auth, mTLS o IP allowlist), o exponer métricas solo en una interfaz interna.
3. **Puerto de gestión:** Opción habitual en Spring Boot es `management.server.port` en una interfaz solo interna o `management.server.address` acotada; así la API pública y las métricas no comparten el mismo socket expuesto.
4. **Prometheus:** Arrancar con `--web.listen-address` solo en bind interno si el scrape ocurre en la misma red.

La política exacta depende del entorno (Kubernetes sidecar, service mesh, etc.); aquí solo se documentan líneas maestras.
