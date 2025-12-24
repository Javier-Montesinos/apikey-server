# Tareas Comunes

Guías rápidas para operaciones frecuentes en este proyecto.

## Agregar un Nuevo Endpoint

### 1. Crear el Controlador

Ubicación: `src/main/java/com/montesinos/securedbyheadertoken/server/rest/`

```java
package com.montesinos.securedbyheadertoken.server.rest;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuariosRestController {

    @GetMapping("/{id}")
    public Usuario obtenerUsuario(@PathVariable String id) {
        // Lógica aquí
        return usuario;
    }

    @PostMapping
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        // Lógica aquí
        return usuario;
    }
}
```

### 2. Crear el Modelo (si es necesario)

Ubicación: `src/main/java/com/montesinos/securedbyheadertoken/server/domain/`

```java
package com.montesinos.securedbyheadertoken.server.domain;

public class Usuario {
    private String id;
    private String nombre;

    // Constructores, getters, setters
}
```

### 3. Probar

**Con Maven:**
```bash
# Compilar
./mvnw clean install

# Ejecutar
./mvnw spring-boot:run
```

**Con Gradle:**
```bash
# Compilar
./gradlew clean build

# Ejecutar
./gradlew bootRun
```

**Probar endpoint:**
```bash
curl -H "Api-Key-Username: user" \
     -H "Api-Key: key" \
     http://localhost:8081/intranet-extranet-api/api/v1/usuarios/123
```

**Nota**: El filtro de seguridad se aplica automáticamente a todos los endpoints.

## Modificar Configuración

### Agregar Nueva Propiedad

**1. En `application.properties`:**
```properties
mi.nueva.propiedad=valor
```

**2. Inyectar en Componente:**
```java
@Component
public class MiComponente {

    @Value("${mi.nueva.propiedad}")
    private String miPropiedad;

    // Usar miPropiedad
}
```

### Cambiar Puerto del Servidor

```properties
# En application.properties
server.port=9090
```

O al ejecutar:
```bash
java -jar target/apikey-server-0.3.jar --server.port=9090
# o con Gradle
java -jar build/libs/apikey-server-0.3.jar --server.port=9090
```

### Configuración por Entorno

Crear archivos específicos:
- `application-dev.properties`
- `application-docker.properties`
- `application-prod.properties`

Activar con:
```bash
# Maven
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Gradle
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## Depurar Problemas de Autenticación

### 1. Verificar Logs del Filtro

Buscar en logs:
```
req : /intranet-extranet-api/api/v1/saludo/hello, user name: usuario, api key value: ***
```

### 2. Verificar API Key Manager

Probar directamente el endpoint del Key Manager:

```bash
curl -X GET \
  "http://localhost:8080/apikey-amanger/api/v1/keys/auth/intranet-extranet-api/usuario/api-key" \
  -H "Api-Key-Username: 75889cd7-550b-48ff-bcf7-c14a8dafff38" \
  -H "Api-Key: 63d5377c-f2e8-4d20-9a0b-0a02eebfa857"
```

Debe retornar: `true` o `false`

### 3. Verificar Headers

Asegurarse de enviar exactamente:
- `Api-Key-Username` (con mayúsculas correctas)
- `Api-Key` (con mayúsculas correctas)

### 4. Verificar Conectividad

```bash
# Verificar que Key Manager está accesible
curl http://localhost:8080/apikey-amanger/api/v1/keys/health

# En Docker, verificar red
docker network inspect apikey-network
```

### 5. Aumentar Nivel de Log

En `application.properties`:
```properties
logging.level.com.montesinos.securedbyheadertoken.server.filters=DEBUG
```

## Agregar un Nuevo Filtro

### 1. Crear Clase de Filtro

```java
@Component
@Order(2)  // Ejecutar después de CheckTokenHeaderFilter
public class MiNuevoFiltro implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(MiNuevoFiltro.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Lógica antes de la petición
        LOG.info("Antes de procesar petición");

        // Continuar cadena
        chain.doFilter(request, response);

        // Lógica después de la petición
        LOG.info("Después de procesar petición");
    }
}
```

### 2. Configurar Orden

- `@Order(1)` - CheckTokenHeaderFilter (autenticación)
- `@Order(2)` - Tu nuevo filtro
- Menor número = mayor prioridad

## Actualizar Versión del Proyecto

### 1. Modificar Archivos de Configuración

**En `pom.xml`:**
```xml
<version>0.4</version>
```

**En `build.gradle`:**
```gradle
version = '0.4'
```

### 2. Recompilar

```bash
# Maven
./mvnw clean install

# Gradle
./gradlew clean build
```

### 3. Actualizar Docker Tag

```bash
# Con Maven
docker build -t fj2m/apikey-server:0.4 .

# Con Gradle
docker build --build-arg JAR_FILE=build/libs/*.jar -t fj2m/apikey-server:0.4 .
```

### 4. Actualizar Documentación

Actualizar referencias de versión en:
- CLAUDE.md
- docs/*.md
- README (si existe)
- Dockerfile

## Agregar Tests

### 1. Crear Clase de Test

Ubicación: `src/test/java/com/montesinos/securedbyheadertoken/server/`

```java
package com.montesinos.securedbyheadertoken.server;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DocumentTest {

    @Test
    void deberiaCrearDocumentoCorrectamente() {
        // Arrange
        Document doc = new Document(1, "doc-123");

        // Act
        doc.setContentBase64("contenido");

        // Assert
        assertEquals("doc-123", doc.getId());
        assertEquals(1, doc.getType());
        assertEquals("contenido", doc.getContentBase64());
    }
}
```

### 2. Ejecutar Tests

```bash
# Maven
./mvnw test

# Gradle
./gradlew test
```

## Trabajar con Docker

### Reconstruir Imagen

**Con Maven:**
```bash
# Recompilar aplicación
./mvnw clean install -DskipTests

# Eliminar imagen anterior
docker rmi fj2m/apikey-server:0.3

# Construir nueva imagen
docker build -t fj2m/apikey-server:0.3 .
```

**Con Gradle:**
```bash
# Recompilar aplicación
./gradlew clean build -x test

# Eliminar imagen anterior
docker rmi fj2m/apikey-server:0.3

# Construir nueva imagen
docker build --build-arg JAR_FILE=build/libs/*.jar -t fj2m/apikey-server:0.3 .
```

### Conectar con Key Manager

```bash
# Crear red (si no existe)
docker network create apikey-network

# Ejecutar Key Manager (ejemplo)
docker run -d \
  --name apikey-manager \
  --network apikey-network \
  -p 8080:8080 \
  fj2m/apikey-manager:latest

# Ejecutar este servicio
docker run -d \
  --name apikey-server \
  --network apikey-network \
  -p 8081:8081 \
  fj2m/apikey-server:0.3
```

### Ver Logs en Tiempo Real

```bash
docker logs -f apikey-server
```

## Cambiar URL del Key Manager

### Desarrollo Local

En `application.properties`:
```properties
apikey.manager.url=http://localhost:8080/apikey-amanger/api/v1/keys
```

### Docker

En `application.properties`:
```properties
apikey.manager.url=http://apikey-manager:8080/apikey-amanger/api/v1/keys
```

### Con Variable de Entorno

```bash
docker run -d \
  -e APIKEY_MANAGER_URL=http://otro-host:8080/api/v1/keys \
  --name apikey-server \
  fj2m/apikey-server:0.3
```

Requiere modificar `application.properties`:
```properties
apikey.manager.url=${APIKEY_MANAGER_URL:http://localhost:8080/apikey-amanger/api/v1/keys}
```

## Solucionar Problemas Comunes

### Error: "Connection refused" al llamar Key Manager

**Causa**: Key Manager no está accesible

**Solución**:
1. Verificar que Key Manager está corriendo
2. Verificar URL en configuración
3. En Docker, verificar que están en la misma red

### Error: Retorna siempre 404

**Causa**: Autenticación fallando

**Solución**:
1. Verificar headers (mayúsculas/minúsculas)
2. Verificar que Key Manager retorna "true"
3. Revisar logs del filtro

### Error: Tests fallan

**Causa**: Contexto Spring no se levanta

**Solución con Maven**:
```bash
# Limpiar y recompilar
./mvnw clean install

# Verificar dependencias
./mvnw dependency:tree
```

**Solución con Gradle**:
```bash
# Limpiar y recompilar
./gradlew clean build

# Verificar dependencias
./gradlew dependencies
```

### Error: Port already in use

**Causa**: Puerto 8081 ocupado

**Solución**:
```bash
# Cambiar puerto temporalmente con Maven
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=9090

# Cambiar puerto temporalmente con Gradle
./gradlew bootRun --args='--server.port=9090'

# O encontrar y matar proceso
lsof -i :8081
kill -9 <PID>
```

## Generar Documentación del Código

### Javadoc

**Con Maven:**
```bash
# Generar Javadoc
./mvnw javadoc:javadoc

# Ver en: target/site/apidocs/index.html
```

**Con Gradle:**
```bash
# Generar Javadoc
./gradlew javadoc

# Ver en: build/docs/javadoc/index.html
```

## Ejecutar Análisis de Código

### SonarQube

**Con Maven:**
```bash
./mvnw clean verify sonar:sonar \
  -Dsonar.projectKey=apikey-server \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=<token>
```

**Con Gradle:**
```bash
./gradlew sonar \
  -Dsonar.projectKey=apikey-server \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=<token>
```

### Checkstyle

**Maven** - Agregar a `pom.xml`:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.1.2</version>
</plugin>
```

Ejecutar:
```bash
./mvnw checkstyle:check
```

**Gradle** - Agregar a `build.gradle`:
```gradle
plugins {
    id 'checkstyle'
}

checkstyle {
    toolVersion = '9.3'
}
```

Ejecutar:
```bash
./gradlew checkstyleMain checkstyleTest
```
