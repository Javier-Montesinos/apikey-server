# Arquitectura y Estructura del Código

## Estructura del Proyecto

```
apikey-server/
├── src/
│   ├── main/
│   │   ├── java/com/montesinos/securedbyheadertoken/server/
│   │   │   ├── RestapiSecuredByHeaderApiKeyServerApplication.java  # Aplicación principal
│   │   │   ├── domain/
│   │   │   │   └── Document.java                                   # Modelo de dominio
│   │   │   ├── filters/
│   │   │   │   └── CheckTokenHeaderFilter.java                     # Filtro de validación
│   │   │   └── rest/
│   │   │       ├── DocumentsRestController.java                    # Endpoint de documentos
│   │   │       └── HelloRestController.java                        # Endpoint de saludo
│   │   └── resources/
│   │       ├── application.properties                              # Configuración
│   │       └── META-INF/
│   │           └── additional-spring-configuration-metadata.json
│   └── test/
│       └── java/com/montesinos/securedbyheadertoken/server/
│           └── RestapiSecuredByHeaderApiKeyServerApplicationTests.java
├── pom.xml                                                         # Configuración Maven
├── Dockerfile                                                      # Definición Docker
└── .gitignore
```

## Organización de Paquetes

### Paquete Base
`com.montesinos.securedbyheadertoken.server`

### Sub-paquetes

- **`domain`** - Modelos de dominio y entidades
- **`filters`** - Filtros de Servlet para procesamiento de peticiones
- **`rest`** - Controladores REST para los endpoints

## Componentes Principales

### 1. CheckTokenHeaderFilter

**Ubicación**: `src/main/java/.../filters/CheckTokenHeaderFilter.java:28`

**Propósito**: Valida todas las peticiones entrantes contra el servicio API Key Manager.

**Características Clave**:
- Anotado con `@Component` y `@Order(1)` para ejecutarse primero
- Extrae cabeceras: `Api-Key-Username` y `Api-Key`
- Llama al API Key Manager externo con scope: `intranet-extranet-api`
- Usa RestTemplate para comunicación HTTP
- Retorna 404 para peticiones no autorizadas (intencionalmente poco informativo)

**Propiedades de Configuración**:
- `apikey.user.name` - Nombre de cabecera para username
- `apikey.user.value` - Valor de username para autenticar contra Key Manager
- `apikey.name` - Nombre de cabecera para API key
- `apikey.value` - Valor de API key para autenticar contra Key Manager
- `apikey.scope` - Identificador de scope para esta API
- `apikey.manager.url` - URL del servicio API Key Manager

### 2. Controladores REST

#### HelloRestController
**Ubicación**: `src/main/java/.../rest/HelloRestController.java:9`

- **Endpoint**: `GET /saludo/hello`
- **Propósito**: Endpoint simple de health check
- **Respuesta**: Texto plano "Hello world !"

#### DocumentsRestController
**Ubicación**: `src/main/java/.../rest/DocumentsRestController.java:14`

- **Endpoint**: `POST /documents/download`
- **Propósito**: Descarga de documentos (implementación de demostración)
- **Request Body**: Lista de objetos Document
- **Response**: Misma lista con campo `contentBase64` poblado
- **Nota**: Actualmente retorna contenido simulado, no documentos reales

### 3. Modelos de Dominio

#### Document
**Ubicación**: `src/main/java/.../domain/Document.java:3`

POJO simple con tres campos:
- `type` (int) - Identificador del tipo de documento
- `id` (String) - Identificador del documento
- `contentBase64` (String) - Contenido del documento en Base64

### 4. Clase Principal de Aplicación

**Ubicación**: `src/main/java/.../RestapiSecuredByHeaderApiKeyServerApplication.java:7`

Punto de entrada estándar de Spring Boot con anotación `@SpringBootApplication`.

## Modelo de Seguridad

### Patrón de Autenticación Basado en Filtros

Esta aplicación implementa un patrón de autenticación mediante filtros:

1. **Todas las peticiones** pasan por `CheckTokenHeaderFilter` (Order 1)
2. El filtro extrae dos cabeceras de las peticiones entrantes:
   - `Api-Key-Username` - El nombre de usuario/identificador del cliente
   - `Api-Key` - El valor de la API key
3. El filtro valida estas credenciales contra el servicio externo **API Key Manager**
4. Si la validación es exitosa, la petición continúa; de lo contrario retorna 404

### Flujo de Autenticación

```
Petición HTTP → CheckTokenHeaderFilter → API Key Manager → Validación
                        ↓                                        ↓
                   Extrae Headers                          true/false
                        ↓                                        ↓
                 Llama Manager ←────────────────────────────────┘
                        ↓
              ¿Autorizado? → Sí → Continuar a Controller
                        ↓
                       No → Retornar 404
```

## Dependencias Externas

### API Key Manager Service

La aplicación depende de un servicio externo de gestión de claves API:

- **URL**: Configurable mediante propiedad `apikey.manager.url`
- **Por defecto**: `http://localhost:8080/apikey-amanger/api/v1/keys`
- **Docker**: `http://apikey-manager:8080/apikey-amanger/api/v1/keys`
- **Endpoint de Autenticación**: `GET /auth/{scope}/{username}/{apiKey}`
- **Respuesta**: String booleano ("true"/"false")

### Autenticación con el Key Manager

Para llamar al Key Manager, este servicio debe autenticarse usando:
- Header `Api-Key-Username` con valor de `apikey.user.value`
- Header `Api-Key` con valor de `apikey.value`

## Arquitectura de Despliegue

### Entorno Local
- Aplicación en puerto 8081
- Key Manager esperado en localhost:8080

### Entorno Docker
- Red Docker: `apikey-network`
- Comunicación entre contenedores por nombre DNS
- Key Manager accesible como `apikey-manager:8080`
