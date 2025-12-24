# Documentación para Asistentes IA - API Key Server

Bienvenido al proyecto **API Key Server**. Esta documentación está diseñada para ayudar a asistentes IA a comprender y trabajar eficientemente con este código.

## 📋 Resumen del Proyecto

**Nombre**: API Key Server Example
**Versión**: 0.3
**Tecnología**: Spring Boot 2.3.4 + Java 8 + Maven/Gradle
**Propósito**: API REST que valida peticiones mediante API keys en las cabeceras HTTP, verificando contra un servicio externo de gestión de claves.

## 🗂️ Estructura de Documentación

Esta documentación está organizada en módulos para facilitar su consulta:

### Información Básica
- **[Arquitectura y Estructura](docs/arquitectura.md)** - Estructura del código, componentes principales y modelo de seguridad
- **[Configuración y Endpoints](docs/configuracion.md)** - Propiedades, configuración y documentación de API

### Desarrollo
- **[Flujos de Desarrollo](docs/desarrollo.md)** - Construcción, ejecución, testing y Docker
- **[Convenciones de Código](docs/convenciones.md)** - Estándares de código, patrones y organización

### Notas Importantes
- **[Consideraciones de Seguridad](docs/seguridad.md)** - Aspectos de seguridad y mejores prácticas
- **[Tareas Comunes](docs/tareas-comunes.md)** - Guías rápidas para operaciones frecuentes

## 🚀 Inicio Rápido

### Construir y Ejecutar

**Con Maven:**
```bash
# Compilar el proyecto
./mvnw clean install -DskipTests

# Ejecutar localmente
./mvnw spring-boot:run
```

**Con Gradle:**
```bash
# Compilar el proyecto
./gradlew clean build -x test

# Ejecutar localmente
./gradlew bootRun
```

La aplicación estará disponible en:
`http://localhost:8081/intranet-extranet-api/api/v1`

### Ejecutar con Docker

```bash
# Construir imagen
docker build -t fj2m/apikey-server:0.3 .

# Ejecutar contenedor
docker run -d -p 8081:8081 --name=apikey-server fj2m/apikey-server:0.3
```

## 🔑 Stack Tecnológico

- **Framework**: Spring Boot 2.3.4.RELEASE
- **Java**: 1.8 (Java 8)
- **Build**: Maven 3.6.x / Gradle 7.6.4 (soporta ambos)
- **Container**: Docker (OpenJDK 8 Alpine)
- **Testing**: JUnit 5

## 📦 Dependencias Externas

Este servicio requiere un **API Key Manager** en ejecución:
- **URL por defecto**: `http://localhost:8080/apikey-amanger/api/v1/keys`
- **Propósito**: Validar las credenciales API key + username

## 📝 Punto de Entrada Principal

**Clase**: `RestapiSecuredByHeaderApiKeyServerApplication`
**Ubicación**: `src/main/java/com/montesinos/securedbyheadertoken/server/`

## 🔒 Modelo de Seguridad

Todas las peticiones pasan por un filtro (`CheckTokenHeaderFilter`) que:
1. Extrae las cabeceras `Api-Key-Username` y `Api-Key`
2. Valida contra el API Key Manager externo
3. Permite la petición si es válida, o retorna 404 si no lo es

## 📚 Para Más Información

Consulta los documentos vinculados arriba para información detallada sobre cada aspecto del proyecto.

---

**Última actualización**: 2025-12-24
**Mantenido para**: Soporte de desarrollo con asistentes IA
