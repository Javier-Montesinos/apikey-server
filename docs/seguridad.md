# Consideraciones de Seguridad

## Gestión de Credenciales

### ⚠️ API Keys en Configuración

**Problema Actual**: El archivo `application.properties` contiene API keys en texto plano:

```properties
apikey.user.value=75889cd7-550b-48ff-bcf7-c14a8dafff38
apikey.value=63d5377c-f2e8-4d20-9a0b-0a02eebfa857
```

### ✅ Recomendaciones para Producción

1. **Variables de Entorno**
   ```bash
   export APIKEY_USER_VALUE=<valor-secreto>
   export APIKEY_VALUE=<valor-secreto>
   ```

2. **Sistemas de Gestión de Secretos**
   - AWS Secrets Manager
   - HashiCorp Vault
   - Azure Key Vault
   - Kubernetes Secrets

3. **Spring Cloud Config**
   - Servidor de configuración centralizado
   - Encriptación de valores sensibles

4. **Nunca Commitear Credenciales Reales**
   - Usar valores de ejemplo en repositorio
   - Documentar qué variables se necesitan
   - Usar `.gitignore` para archivos con secretos

### Externalización de Configuración

```properties
# application.properties
apikey.user.value=${APIKEY_USER_VALUE}
apikey.value=${APIKEY_VALUE}
apikey.manager.url=${APIKEY_MANAGER_URL:http://localhost:8080/apikey-amanger/api/v1/keys}
```

## Estrategia de Respuestas de Error

### Comportamiento Actual

El filtro retorna **404 NOT_FOUND** para peticiones no autenticadas:

```java
res.sendError(HttpStatus.NOT_FOUND.value(), "Recurso no disponible");
```

### Razón: Seguridad por Oscuridad

**Ventajas**:
- No revela que el endpoint existe
- Dificulta enumeración de recursos
- Confunde a atacantes automatizados

**Desventajas**:
- Dificulta debugging
- No es estándar REST (debería ser 401/403)
- Puede confundir a desarrolladores legítimos

### Considerar para Mejora

```java
// Más estándar
res.sendError(HttpStatus.UNAUTHORIZED.value(), "Credenciales inválidas");

// O con más detalle (solo en dev)
if (isDevelopment) {
    res.sendError(HttpStatus.UNAUTHORIZED.value(), "API Key inválida");
} else {
    res.sendError(HttpStatus.NOT_FOUND.value(), "Recurso no disponible");
}
```

## Logging de Información Sensible

### ⚠️ Problema Actual

El filtro registra valores de API key en logs:

```java
LOG.info("req : {}, user name: {}, api key value: {}",
    req.getRequestURI(), userName, apiKey);
```

### ✅ Recomendación: Enmascarar Datos Sensibles

```java
// Mejor práctica
LOG.info("req : {}, user name: {}, api key value: {}",
    req.getRequestURI(), userName, maskApiKey(apiKey));

private String maskApiKey(String apiKey) {
    if (apiKey == null || apiKey.length() < 8) {
        return "***";
    }
    return apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4);
}
```

Resultado: `ab12****xy89` en lugar de la clave completa.

## RestTemplate: Consideraciones

### Problema Actual

Se crea una nueva instancia de `RestTemplate` en cada petición:

```java
RestTemplate restTemplate = new RestTemplate();
```

### Mejoras Recomendadas

1. **Inyectar como Bean** (mejor performance)
   ```java
   @Configuration
   public class AppConfig {
       @Bean
       public RestTemplate restTemplate() {
           return new RestTemplate();
       }
   }
   ```

2. **Configurar Timeouts** (prevenir bloqueos)
   ```java
   @Bean
   public RestTemplate restTemplate() {
       SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
       factory.setConnectTimeout(3000);
       factory.setReadTimeout(3000);
       return new RestTemplate(factory);
   }
   ```

3. **Migrar a WebClient** (más moderno)
   ```java
   // Spring WebFlux
   WebClient webClient = WebClient.builder()
       .baseUrl(apiKeyManagerUrl)
       .defaultHeader(apiKeyName, apiKeyValue)
       .build();
   ```

## Manejo de Errores de Red

### Problema Actual

No hay manejo explícito de errores al llamar al API Key Manager. Fallos de red se propagan como excepciones.

### Mejora Recomendada

```java
private boolean authApiKey(String userName, String apiKey) {
    try {
        RestTemplate restTemplate = new RestTemplate();
        // ... configuración
        ResponseEntity<String> response = restTemplate.exchange(...);
        return Boolean.parseBoolean(response.getBody());

    } catch (RestClientException e) {
        LOG.error("Error al validar API key: {}", e.getMessage());
        // Política: denegar acceso si no podemos validar
        return false;
    }
}
```

## Protección contra Ataques Comunes

### Prevención de Inyección

✅ **Buenas prácticas actuales**:
- Uso de Spring Framework que previene muchas inyecciones
- No se construyen queries SQL dinámicas (no hay BD)
- No se ejecutan comandos del sistema

### Rate Limiting

⚠️ **No implementado actualmente**

Considerar agregar:
```java
// Con librería como Bucket4j
@Component
public class RateLimitingFilter implements Filter {
    // Limitar intentos de autenticación por IP
}
```

### Validación de Entrada

Agregar validación en controladores:

```java
@PostMapping("/documents/download")
public List<Document> downloadDocuments(@RequestBody @Valid List<Document> documents) {
    // Spring validará automáticamente
}
```

En el modelo:
```java
import javax.validation.constraints.*;

public class Document {
    @NotNull
    @Min(1)
    private int type;

    @NotBlank
    @Size(max = 100)
    private String id;
}
```

## HTTPS/TLS

### Recomendaciones

Para producción, configurar HTTPS:

```properties
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat
```

O mejor: usar un reverse proxy (nginx, Apache) que maneje TLS.

## CORS (Cross-Origin Resource Sharing)

Si la API será consumida desde navegadores:

```java
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("https://trusted-domain.com")
                    .allowedMethods("GET", "POST")
                    .allowedHeaders("Api-Key", "Api-Key-Username");
            }
        };
    }
}
```

## Auditoría

### Agregar Registro de Accesos

```java
@Component
public class AuditFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String user = req.getHeader("Api-Key-Username");
        String endpoint = req.getRequestURI();

        LOG.info("AUDIT: User {} accessed {} at {}",
            user, endpoint, Instant.now());

        chain.doFilter(request, response);
    }
}
```

## Checklist de Seguridad

Antes de desplegar a producción:

- [ ] Externalizar todas las credenciales
- [ ] Configurar HTTPS/TLS
- [ ] Implementar rate limiting
- [ ] Enmascarar datos sensibles en logs
- [ ] Configurar timeouts en RestTemplate
- [ ] Agregar validación de entrada
- [ ] Implementar auditoría de accesos
- [ ] Revisar que `.gitignore` excluya archivos sensibles
- [ ] Configurar CORS adecuadamente
- [ ] Actualizar dependencias con vulnerabilidades conocidas
- [ ] Realizar análisis con SonarQube u herramienta similar

## Análisis de Seguridad

### SonarQube

El proyecto menciona revisión con SonarQube (commit 3c73d86).

Ejecutar regularmente:
```bash
./mvnw sonar:sonar \
  -Dsonar.projectKey=apikey-server \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=${SONAR_TOKEN}
```

### OWASP Dependency Check

Verificar vulnerabilidades en dependencias:
```bash
./mvnw org.owasp:dependency-check-maven:check
```
