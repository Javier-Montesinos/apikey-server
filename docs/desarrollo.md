# Flujos de Desarrollo

## Construcción del Proyecto

Este proyecto soporta **Maven** y **Gradle** como sistemas de build. Puedes usar el que prefieras.

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

### Usando Gradle Wrapper

```bash
# Compilar sin ejecutar tests
./gradlew clean build -x test

# Compilar con tests
./gradlew clean build

# Solo ejecutar tests
./gradlew test

# Limpiar el proyecto
./gradlew clean

# Empaquetar
./gradlew bootJar
```

### Artefactos Generados

Después de compilar, el JAR se genera en:
- **Maven**: `target/apikey-server-0.3.jar`
- **Gradle**: `build/libs/apikey-server-0.3.jar`

## Ejecución Local

### Con Maven

```bash
# Ejecutar directamente con Maven
./mvnw spring-boot:run

# Con perfil específico
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Con Gradle

```bash
# Ejecutar directamente con Gradle
./gradlew bootRun

# Con perfil específico
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Con JAR Compilado

```bash
# Después de compilar con Maven
java -jar target/apikey-server-0.3.jar

# Después de compilar con Gradle
java -jar build/libs/apikey-server-0.3.jar

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

**Referencia**: Ver instrucciones completas en `Dockerfile:27-39`

#### Opción A: Con Maven

```bash
# Paso 1: Compilar el JAR con Maven
./mvnw clean install -DskipTests

# Paso 2: Construir la imagen Docker
docker build -t fj2m/apikey-server:0.3 .

# Construir con tag personalizado
docker build -t fj2m/apikey-server:latest .
```

#### Opción B: Con Gradle

```bash
# Paso 1: Compilar el JAR con Gradle
./gradlew clean build -x test

# Paso 2: Construir la imagen Docker (especificando ubicación del JAR)
docker build --build-arg JAR_FILE=build/libs/*.jar -t fj2m/apikey-server:0.3 .

# Construir con tag personalizado
docker build --build-arg JAR_FILE=build/libs/*.jar -t fj2m/apikey-server:latest .
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
- **Puerto expuesto**: 8081 (corregido)
- **Red recomendada**: `apikey-network` para comunicación con Key Manager
- **Build flexible**: Soporta JAR desde Maven (`target/`) o Gradle (`build/libs/`)

## Testing

### Estructura de Tests

**Ubicación**: `src/test/java/com/montesinos/securedbyheadertoken/server/`

Actualmente solo existe un test básico de contexto.

### Ejecutar Tests

**Con Maven:**
```bash
# Todos los tests
./mvnw test

# Solo tests de una clase específica
./mvnw test -Dtest=RestapiSecuredByHeaderApiKeyServerApplicationTests

# Con cobertura (requiere configurar plugin)
./mvnw test jacoco:report
```

**Con Gradle:**
```bash
# Todos los tests
./gradlew test

# Solo tests de una clase específica
./gradlew test --tests RestapiSecuredByHeaderApiKeyServerApplicationTests

# Con reporte HTML
./gradlew test --info

# Limpiar y ejecutar tests
./gradlew clean test
```

### Framework de Testing

- **JUnit**: 5 (Jupiter)
- **Spring Boot Test**: Incluido
- **Exclusiones**: junit-vintage-engine (para usar solo JUnit 5)

## Hot Reload en Desarrollo

El proyecto incluye `spring-boot-devtools` que permite hot reload:

**Con Maven:**
1. Ejecuta la aplicación: `./mvnw spring-boot:run`
2. Modifica código Java
3. Recompila (en IDE o con Maven)
4. La aplicación se reinicia automáticamente

**Con Gradle:**
1. Ejecuta la aplicación: `./gradlew bootRun`
2. Modifica código Java
3. Recompila (en IDE o con `./gradlew classes`)
4. La aplicación se reinicia automáticamente

**Nota**: Solo funciona en modo desarrollo, no en producción.

## Depuración

### Depuración Local

**Con Maven:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

**Con Gradle:**
```bash
./gradlew bootRun --debug-jvm
```

**Con JAR directamente:**
```bash
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

### Con Maven
1. **Hacer cambios** en el código
2. **Ejecutar tests**: `./mvnw test`
3. **Compilar**: `./mvnw clean install`
4. **Ejecutar localmente**: `./mvnw spring-boot:run`
5. **Probar manualmente** con curl o Postman
6. **Commit** de cambios
7. **Construir Docker** (si es necesario)
8. **Probar en Docker** con Key Manager

### Con Gradle
1. **Hacer cambios** en el código
2. **Ejecutar tests**: `./gradlew test`
3. **Compilar**: `./gradlew clean build`
4. **Ejecutar localmente**: `./gradlew bootRun`
5. **Probar manualmente** con curl o Postman
6. **Commit** de cambios
7. **Construir Docker** (si es necesario)
8. **Probar en Docker** con Key Manager

## Herramientas Recomendadas

- **IDE**: IntelliJ IDEA / Eclipse / VS Code con extensiones Java
- **API Testing**: Postman / Insomnia / curl
- **Docker**: Docker Desktop
- **Build Tools**:
  - Maven: Instalado o usar wrapper incluido (./mvnw)
  - Gradle: Wrapper incluido (./gradlew) - versión 7.6.4

## Diferencias entre Maven y Gradle

### Cuándo Usar Cada Uno

**Maven:**
- ✅ Más tradicional y conocido
- ✅ Configuración XML declarativa
- ✅ Ciclos de vida bien definidos
- ✅ Gran cantidad de plugins disponibles

**Gradle:**
- ✅ Más rápido (builds incrementales, cache)
- ✅ Configuración más concisa (Groovy/Kotlin DSL)
- ✅ Más flexible para builds complejos
- ✅ Build scans para análisis de rendimiento

### Comandos Equivalentes

| Acción | Maven | Gradle |
|--------|-------|--------|
| Limpiar | `./mvnw clean` | `./gradlew clean` |
| Compilar | `./mvnw compile` | `./gradlew compileJava` |
| Ejecutar tests | `./mvnw test` | `./gradlew test` |
| Empaquetar | `./mvnw package` | `./gradlew bootJar` |
| Build completo | `./mvnw clean install` | `./gradlew clean build` |
| Ejecutar app | `./mvnw spring-boot:run` | `./gradlew bootRun` |
| Skip tests | `./mvnw install -DskipTests` | `./gradlew build -x test` |

### Archivos de Configuración

**Maven:**
- `pom.xml` - Configuración del proyecto
- `.mvn/` - Configuración del wrapper

**Gradle:**
- `build.gradle` - Script de build del proyecto
- `settings.gradle` - Configuración del proyecto raíz
- `gradle/` - Wrapper y configuración
