# AutoAndes API

Backend REST de **AutoAndes**, un taller mecánico con dos locales: registro de órdenes de servicio (cabecera–detalle), catálogo de servicios, vehículos de clientes, reporte de ingresos y seguridad con JWT por roles y permisos.

> Examen Parcial II – Lenguaje de Programación II – Versión A. El frontend está en [autoandes-frontend](https://github.com/Legends600/autoandes-frontend).

**Stack:** Java 21 · Spring Boot 4.1 · Spring Data JPA (Hibernate) · Spring Security + JWT (HS256) · Flyway · Oracle (Free 23ai) · springdoc-openapi (Swagger UI) · Lombok.

## Cómo ejecutar

### 1. Oracle

Cualquier Oracle con un esquema/usuario propio. Con Docker:

```bash
docker run -d --name oracle-local -p 1522:1521 -e ORACLE_PASSWORD=<clave-sys> -e APP_USER=autoandes -e APP_USER_PASSWORD=<clave> gvenzl/oracle-free:latest
```

### 2. Perfiles: desarrollo y producción

El perfil activo por defecto es **dev** (`spring.profiles.active: dev` en `application.yaml`). **Ninguna credencial se sube al repositorio.**

| Perfil | Archivo | Credenciales |
|---|---|---|
| `dev` (por defecto) | `application-dev.yaml` (local, en `.gitignore`) | Se crea a partir de `application-dev.yaml.example` con tu usuario y contraseña de Oracle y una clave JWT |
| `prod` | `application-prod.yaml` | `${DB_URL}`, `${DB_USER}`, `${DB_PASSWORD}` y `${JWT_SECRET}` llegan por variables de entorno |

Para ejecutar en desarrollo, copia la plantilla y completa tus valores:

```bash
cp src/main/resources/application-dev.yaml.example src/main/resources/application-dev.yaml
```

Para ejecutar en producción:

```bash
SPRING_PROFILES_ACTIVE=prod DB_URL=... DB_USER=... DB_PASSWORD=... JWT_SECRET=... ./mvnw spring-boot:run
```

### 3. Levantar con un solo comando

```bash
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

Al arrancar, **Flyway** aplica `V1__esquema_y_semilla.sql` (tablas, 6 clientes, 8 servicios, 8 vehículos, 4 órdenes, roles y usuarios con contraseña BCrypt) y `V2__secuencia_numero_orden.sql` (secuencia del correlativo `OS-000001`).

- API: `http://localhost:8080/api/v1`
- Swagger UI: `http://localhost:8080/swagger-ui.html` (botón **Authorize** para pegar el token)

Para volver a los datos semilla: `sqlplus autoandes/<clave>@localhost:1522/FREEPDB1 @scripts/reset-bd.sql` y reiniciar el backend.

## Usuarios de prueba

| Usuario | Contraseña | Rol | Estado |
|---|---|---|---|
| `admin` | `Auto2026*` | ADMIN | Activo |
| `asesor` | `Auto2026*` | ASESOR | Activo |
| `jefe` | `Auto2026*` | JEFE_TALLER | Activo |
| `inactivo` | `Auto2026*` | ASESOR | Inactivo (login → 401) |

## Roles y permisos

Los permisos se conceden por rol y **siempre se verifican contra el permiso, nunca contra el nombre del rol**. La matriz vive en un solo lugar: [`PermisosPorRol`](src/main/java/pe/edu/upeu/autoandes/security/PermisosPorRol.java). Un usuario con varios roles recibe la unión de sus permisos.

| Permiso | ADMIN | ASESOR | JEFE_TALLER |
|---|:-:|:-:|:-:|
| `SERVICIO_LEER` | ✔ | ✔ | ✔ |
| `SERVICIO_ESCRIBIR` | ✔ | – | ✔ |
| `VEHICULO_LEER` (también clientes) | ✔ | ✔ | ✔ |
| `VEHICULO_ESCRIBIR` | ✔ | ✔ | – |
| `ORDEN_LEER` | ✔ | ✔ | ✔ |
| `ORDEN_REGISTRAR` | ✔ | ✔ | – |
| `ORDEN_ANULAR` | ✔ | – | ✔ |
| `REPORTE_VER` | ✔ | – | ✔ |

## Contrato del API (`/api/v1`)

Todas las rutas exigen `Authorization: Bearer <token>`, salvo el login.

| Verbo | Ruta | Permiso | Códigos |
|---|---|---|---|
| POST | `/auth/login` | público | 200 · 400 · 401 |
| GET | `/auth/me` | autenticado | 200 · 401 |
| GET | `/clientes` | `VEHICULO_LEER` | 200 · 401 · 403 |
| GET, POST | `/servicios` | `SERVICIO_LEER` / `SERVICIO_ESCRIBIR` | 200 · 201 · 400 · 401 · 403 · 409 |
| GET, PUT, DELETE | `/servicios/{id}` | `SERVICIO_LEER` / `SERVICIO_ESCRIBIR` | 200 · 204 · 400 · 404 · 409 |
| PATCH | `/servicios/{id}/estado` | `SERVICIO_ESCRIBIR` | 200 · 400 · 401 · 403 · 404 |
| GET, POST | `/vehiculos?clienteId` | `VEHICULO_LEER` / `VEHICULO_ESCRIBIR` | 200 · 201 · 400 · 404 · 409 |
| GET, PUT, DELETE | `/vehiculos/{id}` | `VEHICULO_LEER` / `VEHICULO_ESCRIBIR` | 200 · 204 · 400 · 404 · 409 |
| POST | `/ordenes` | `ORDEN_REGISTRAR` | 201 · 400 · 401 · 403 · 404 · 409 |
| GET | `/ordenes?placa&estado&desde&hasta` | `ORDEN_LEER` | 200 · 400 · 401 · 403 |
| GET | `/ordenes/{id}` | `ORDEN_LEER` | 200 · 401 · 403 · 404 |
| PATCH | `/ordenes/{id}/anular` | `ORDEN_ANULAR` | 200 · 400 · 401 · 403 · 404 · 409 |
| GET | `/reportes/ingresos-por-servicio?desde&hasta` | `REPORTE_VER` | 200 · 400 · 401 · 403 |

### Formato de error común

```json
{
  "timestamp": "2026-10-29T09:14:22",
  "status": 400,
  "error": "Bad Request",
  "mensaje": "Datos inválidos",
  "ruta": "/api/v1/vehiculos",
  "campos": { "placa": "La placa debe tener el formato ABC-123" }
}
```

Todos los errores (400, 401, 403, 404, 409 y 500) usan este formato, también los generados por Spring Security. `campos` solo aparece en errores de validación.

## Reglas de negocio (capa de servicio)

| Código | Regla | Respuesta |
|---|---|---|
| RN-01 | El código de servicio es único | 409 |
| RN-02 | Un servicio usado en alguna orden no se elimina: `DELETE` → 409; se da de baja con `PATCH /servicios/{id}/estado`. La placa de vehículo es única (409), el cliente debe existir (404) y un vehículo con órdenes no se elimina (409) | 409 / 404 |
| RN-03 | Una orden tiene de 1 a 10 detalles, sin repetir servicio, cantidad de 1 a 5 (400); todos los servicios deben estar activos | 400 / 409 |
| RN-04 | El kilometraje no puede ser menor que el de la última orden REGISTRADA del vehículo (el mensaje incluye ese kilometraje) | 409 |
| RN-05 | Solo se anula una orden REGISTRADA y del mismo día; el motivo es obligatorio (10 a 200 caracteres) | 400 / 409 |

## Decisiones de diseño

- **Capas**: `controller` (solo HTTP) → `service` (reglas y `@Transactional`) → `repository`. El controlador nunca usa el repositorio. Las entidades no salen al exterior: se responde con DTO (`VehiculoResponseDTO` lleva `clienteId` y `clienteNombre`, no la entidad anidada).
- **Orden atómica**: `OrdenServiceImpl.registrar` es `@Transactional`. El precio unitario se toma de la base de datos, el subtotal y el total se calculan en el servicio (nunca se aceptan del cliente). Cualquier detalle inválido lanza una excepción y la transacción se revierte completa. Las validaciones se ejecutan antes de persistir, de modo que un rechazo no consume el correlativo.
- **Correlativo** `OS-000001`: secuencia de Oracle (`seq_orden_numero`).
- **Reporte**: una sola consulta JPQL con `GROUP BY` y proyección (`IngresoPorServicioDTO`), ordenada por importe descendente y excluyendo las órdenes ANULADAS. No se usa `findAll()` ni bucles en Java.
- **Consulta de órdenes**: filtros opcionales y combinables con `Specification`; orden por fecha descendente; sin resultados responde 200 con lista vacía.
- **Seguridad**: API `STATELESS`; `JwtAuthenticationFilter` valida firma y expiración en cada petición; solo `/auth/login`, Swagger y `OPTIONS` son públicos. Sin token, alterado o vencido → **401**; autenticado sin permiso → **403**. Los permisos se declaran con `hasAuthority` en `SecurityConfig` y `@PreAuthorize` (reporte). Ningún controlador compara nombres de rol.
- **Token**: JWT HS256 con `sub`, `nombreCompleto`, `roles`, `permisos`, `iat` y `exp` (3600 s). El login responde el mismo mensaje 401 para usuario inexistente, clave incorrecta y cuenta inactiva.
- **CORS**: origen `http://localhost:4200`; métodos GET, POST, PUT, PATCH, DELETE y OPTIONS; cabeceras `Authorization`, `Content-Type` y `Accept`.
- **Logs**: INFO en cada alta, cambio y baja; WARN en cada regla rechazada y en cada acceso denegado.

## Estructura

```
src/main/java/pe/edu/upeu/autoandes/
  config/      SecurityConfig, JwtService, JwtAuthenticationFilter, CorsConfig, OpenApiConfig
  security/    Permiso, PermisosPorRol, UsuarioDetailsService, UsuarioPrincipal,
               RestAuthenticationEntryPoint (401), RestAccessDeniedHandler (403)
  entity/      Cliente, Servicio, Vehiculo, OrdenServicio, DetalleOrden, Usuario, Rol
  repository/  interfaces JpaRepository y la consulta JPQL del reporte
  service/     interfaces e implementaciones con las reglas RN y @Transactional
  controller/  AuthController y controladores REST bajo /api/v1
  dto/         Request, Response, proyección del reporte y ErrorResponseDTO
  mapper/      conversiones entidad ⇄ DTO
  exception/   GlobalExceptionHandler y excepciones del dominio
src/main/resources/db/migration/   V1__esquema_y_semilla.sql, V2__secuencia_numero_orden.sql
postman/                           AutoAndes.postman_collection.json
scripts/reset-bd.sql               vuelve a los datos semilla
```

## Pruebas

```bash
./mvnw test                  # pruebas unitarias: token (vencido, alterado, otra clave) y matriz de permisos
```

La colección [`postman/AutoAndes.postman_collection.json`](postman/AutoAndes.postman_collection.json) (53 peticiones, 70 aserciones) cubre 200, 201, 204, 400, 401, 403, 404 y 409. Impórtala en Postman y ejecútala de arriba hacia abajo con el *Collection Runner* (los logins guardan los tokens que usan las demás carpetas), o desde la terminal: `npx newman run postman/AutoAndes.postman_collection.json`.
