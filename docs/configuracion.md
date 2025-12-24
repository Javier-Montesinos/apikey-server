# Configuración y Endpoints

## Configuración de la Aplicación

### Archivo de Propiedades
**Ubicación**: `src/main/resources/application.properties`

### Configuración del Servidor

```properties
server.port=8081
server.servlet.context-path=/intranet-extranet-api/api/v1
```

**URL Base Completa**: `http://localhost:8081/intranet-extranet-api/api/v1`

### Credenciales para API Key Manager

Credenciales que usa este servicio para autenticarse contra el Key Manager:

```properties
apikey.user.name=Api-Key-Username
apikey.user.value=75889cd7-550b-48ff-bcf7-c14a8dafff38
apikey.name=Api-Key
apikey.value=63d5377c-f2e8-4d20-9a0b-0a02eebfa857
```

**Importante**:
- `apikey.user.name` y `apikey.name` son los nombres de las cabeceras HTTP
- `apikey.user.value` y `apikey.value` son las credenciales de este servicio

### Configuración de Seguridad

```properties
apikey.scope=intranet-extranet-api
apikey.manager.url=http://localhost:8080/apikey-amanger/api/v1/keys
```

**Notas**:
- El `scope` identifica esta API en el sistema de gestión de claves
- Para despliegues Docker, cambiar URL a `http://apikey-manager:8080/apikey-amanger/api/v1/keys`

## Endpoints de la API

Todos los endpoints están prefijados con `/intranet-extranet-api/api/v1` y requieren cabeceras de API key.

### GET /saludo/hello

Endpoint simple de saludo para verificar que el servicio funciona.

**URL Completa**:
```
http://localhost:8081/intranet-extranet-api/api/v1/saludo/hello
```

**Método**: GET

**Cabeceras Requeridas**:
```
Api-Key-Username: <usuario-valido>
Api-Key: <api-key-valida>
```

**Respuesta Exitosa**:
```
HTTP 200 OK
Content-Type: text/plain

Hello world !
```

**Ejemplo con curl**:
```bash
curl -X GET \
  http://localhost:8081/intranet-extranet-api/api/v1/saludo/hello \
  -H 'Api-Key-Username: usuario-ejemplo' \
  -H 'Api-Key: key-ejemplo'
```

### POST /documents/download

Endpoint para descargar documentos (implementación de demostración).

**URL Completa**:
```
http://localhost:8081/intranet-extranet-api/api/v1/documents/download
```

**Método**: POST

**Cabeceras Requeridas**:
```
Api-Key-Username: <usuario-valido>
Api-Key: <api-key-valida>
Content-Type: application/json
```

**Request Body**:
```json
[
  {
    "type": 1,
    "id": "doc-123"
  },
  {
    "type": 2,
    "id": "doc-456"
  }
]
```

**Respuesta Exitosa**:
```json
[
  {
    "type": 1,
    "id": "doc-123",
    "contentBase64": "contenido del documento con id: doc-123 de tipo: 1"
  },
  {
    "type": 2,
    "id": "doc-456",
    "contentBase64": "contenido del documento con id: doc-456 de tipo: 2"
  }
]
```

**Ejemplo con curl**:
```bash
curl -X POST \
  http://localhost:8081/intranet-extranet-api/api/v1/documents/download \
  -H 'Api-Key-Username: usuario-ejemplo' \
  -H 'Api-Key: key-ejemplo' \
  -H 'Content-Type: application/json' \
  -d '[
    {"type": 1, "id": "doc-123"},
    {"type": 2, "id": "doc-456"}
  ]'
```

## Respuestas de Error

### 404 Not Found

Retornado cuando:
- Las credenciales API key no son válidas
- El endpoint no existe

```
HTTP 404 Not Found

Recurso no disponible
```

**Nota de Seguridad**: El filtro retorna 404 (en lugar de 401/403) para peticiones no autorizadas como medida de seguridad por oscuridad.

## Variables de Entorno

Para externalizar la configuración en entornos productivos, puedes usar variables de entorno:

```bash
# Ejemplo de variables de entorno
export APIKEY_MANAGER_URL=http://key-manager-prod:8080/apikey-amanger/api/v1/keys
export APIKEY_USER_VALUE=<uuid-produccion>
export APIKEY_VALUE=<uuid-produccion>
```

Luego referenciarlas en `application.properties`:
```properties
apikey.manager.url=${APIKEY_MANAGER_URL:http://localhost:8080/apikey-amanger/api/v1/keys}
apikey.user.value=${APIKEY_USER_VALUE}
apikey.value=${APIKEY_VALUE}
```

## Perfiles de Spring

Para gestionar diferentes configuraciones por entorno, considera usar perfiles de Spring:

- `application-dev.properties` - Desarrollo local
- `application-docker.properties` - Entorno Docker
- `application-prod.properties` - Producción

Activar con:
```bash
java -jar app.jar --spring.profiles.active=docker
```
