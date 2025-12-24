# Flujos de Desarrollo

## Construcción del Proyecto

### Usando Maven Wrapper

```bash
# Compilar sin ejecutar tests
./mvnw clean install -DskipTests

# Compilar con tests
./mvnw clean install

# Solo ejecutar tests
./mvnw test

# Limpiar el proyecto
./mvnw clean

# Empaquetar
./mvnw package
```

### Artefacto Generado

Después de compilar, el JAR se genera en:
```
target/apikey-server-0.3.jar
```

## Ejecución Local

### Con Maven

```bash
# Ejecutar directamente con Maven
./mvnw spring-boot:run

# Con perfil específico
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Con JAR Compilado

```bash
# Después de compilar
java -jar target/apikey-server-0.3.jar

# Con perfil específico
java -jar target/apikey-server-0.3.jar --spring.profiles.active=dev

# Con propiedades personalizadas
java -jar target/apikey-server-0.3.jar --server.port=9090
```

### Verificar que Funciona

```bash
# Health check (requiere API key válida)
curl -H "Api-Key-Username: usuario" \
     -H "Api-Key: key" \
     http://localhost:8081/intranet-extranet-api/api/v1/saludo/hello
```

## Desarrollo con Docker

### Construir Imagen Docker

**Referencia**: Ver instrucciones completas en `Dockerfile:24-36`

```bash
# Paso 1: Compilar el JAR
./mvnw clean install -DskipTests

# Paso 2: Construir la imagen Docker
docker build -t fj2m/apikey-server:0.3 .

# Construir con tag personalizado
docker build -t fj2m/apikey-server:latest .
```

### Ejecutar Contenedor

```bash
# Ejecutar en modo standalone
docker run -d -p 8081:8081 --name=apikey-server fj2m/apikey-server:0.3

# Ejecutar en red Docker personalizada
docker run -d \
  -p 8081:8081 \
  --name=apikey-server \
  --network=apikey-network \
  fj2m/apikey-server:0.3

# Ejecutar con variables de entorno
docker run -d \
  -p 8081:8081 \
  -e APIKEY_MANAGER_URL=http://apikey-manager:8080/apikey-amanger/api/v1/keys \
  --name=apikey-server \
  --network=apikey-network \
  fj2m/apikey-server:0.3
```

### Gestión de Contenedores

```bash
# Ver logs
docker logs apikey-server

# Ver logs en tiempo real
docker logs -f apikey-server

# Detener contenedor
docker stop apikey-server

# Iniciar contenedor detenido
docker start apikey-server

# Eliminar contenedor
docker rm apikey-server

# Acceder al contenedor
docker exec -it apikey-server sh
```

### Notas Docker

- **Imagen base**: `openjdk:8-jdk-alpine`
- **Usuario**: Ejecuta como usuario no-root `spring:spring`
- **Puerto expuesto**: 8080 en Dockerfile (pero la app usa 8081 - considerar corregir)
- **Red recomendada**: `apikey-network` para comunicación con Key Manager

## Testing

### Estructura de Tests

**Ubicación**: `src/test/java/com/montesinos/securedbyheadertoken/server/`

Actualmente solo existe un test básico de contexto.

### Ejecutar Tests

```bash
# Todos los tests
./mvnw test

# Solo tests de una clase específica
./mvnw test -Dtest=RestapiSecuredByHeaderApiKeyServerApplicationTests

# Con cobertura (requiere configurar plugin)
./mvnw test jacoco:report
```

### Framework de Testing

- **JUnit**: 5 (Jupiter)
- **Spring Boot Test**: Incluido
- **Exclusiones**: junit-vintage-engine (para usar solo JUnit 5)

## Hot Reload en Desarrollo

El proyecto incluye `spring-boot-devtools` que permite hot reload:

1. Ejecuta la aplicación: `./mvnw spring-boot:run`
2. Modifica código Java
3. Recompila (en IDE o con Maven)
4. La aplicación se reinicia automáticamente

**Nota**: Solo funciona en modo desarrollo, no en producción.

## Depuración

### Depuración Local

```bash
# Con Maven
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# Con JAR
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar target/apikey-server-0.3.jar
```

Luego conecta tu IDE al puerto 5005.

### Depuración en Docker

```bash
docker run -d \
  -p 8081:8081 \
  -p 5005:5005 \
  -e JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" \
  --name=apikey-server \
  fj2m/apikey-server:0.3
```

## Logs

### Niveles de Log

Configurar en `application.properties`:

```properties
# Log global
logging.level.root=INFO

# Log del paquete específico
logging.level.com.montesinos.securedbyheadertoken=DEBUG

# Log de Spring
logging.level.org.springframework.web=DEBUG
```

### Ver Logs

```bash
# En ejecución local - aparecen en consola

# En Docker
docker logs apikey-server

# Últimas N líneas
docker logs --tail 100 apikey-server

# En tiempo real
docker logs -f apikey-server
```

## Ciclo de Desarrollo Recomendado

1. **Hacer cambios** en el código
2. **Ejecutar tests** localmente: `./mvnw test`
3. **Compilar**: `./mvnw clean install`
4. **Ejecutar localmente**: `./mvnw spring-boot:run`
5. **Probar manualmente** con curl o Postman
6. **Commit** de cambios
7. **Construir Docker** (si es necesario)
8. **Probar en Docker** con Key Manager

## Herramientas Recomendadas

- **IDE**: IntelliJ IDEA / Eclipse / VS Code con extensiones Java
- **API Testing**: Postman / Insomnia / curl
- **Docker**: Docker Desktop
- **Maven**: Instalado o usar wrapper incluido (./mvnw)
