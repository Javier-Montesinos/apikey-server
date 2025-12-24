# Convenciones de Código

## Estructura de Paquetes

### Paquete Base
```
com.montesinos.securedbyheadertoken.server
```

### Organización de Sub-paquetes

- **`domain`** - Modelos de dominio y entidades de negocio
- **`filters`** - Filtros de Servlet para procesamiento de peticiones
- **`rest`** - Controladores REST para endpoints HTTP

## Convenciones de Nombres

### Clases

- **Controladores REST**: `*RestController`
  - Ejemplo: `HelloRestController`, `DocumentsRestController`

- **Filtros**: `*Filter`
  - Ejemplo: `CheckTokenHeaderFilter`

- **Modelos de Dominio**: Sustantivos descriptivos
  - Ejemplo: `Document`, `User`, `ApiKey`

- **Servicios** (si se agregan): `*Service`
  - Ejemplo: `DocumentService`, `AuthenticationService`

- **Repositorios** (si se agregan): `*Repository`
  - Ejemplo: `DocumentRepository`

### Métodos

- **Verbos en inglés** para acciones
- **CamelCase** comenzando con minúscula
- Ejemplos: `doFilter()`, `authApiKey()`, `downloadDocuments()`

### Variables

- **CamelCase** comenzando con minúscula
- Nombres descriptivos: `apiKey`, `userName`, `apiKeyManagerUrl`
- Constantes en UPPER_CASE: `LOG`, `DEFAULT_TIMEOUT`

### Propiedades de Configuración

Formato: `palabra.separada.por.puntos`

Ejemplos:
```properties
apikey.user.name
apikey.manager.url
server.port
```

## Anotaciones de Spring

### Componentes

```java
@Component          // Componentes genéricos (filtros, utilidades)
@RestController     // Controladores REST
@Service            // Servicios de lógica de negocio
@Repository         // Repositorios de datos
@Configuration      // Clases de configuración
```

### Inyección de Dependencias

```java
@Value("${property.name}")           // Inyectar propiedades
@Autowired                           // Inyectar beans (preferir constructor)
private final Service service;       // Preferir inyección por constructor
```

### Mapeo de Endpoints

```java
@RequestMapping("/path")             // A nivel de clase (prefijo)
@GetMapping("/subpath")              // Para HTTP GET
@PostMapping("/subpath")             // Para HTTP POST
@PutMapping("/subpath")              // Para HTTP PUT
@DeleteMapping("/subpath")           // Para HTTP DELETE
```

### Parámetros de Request

```java
@RequestBody                         // Para cuerpo de petición (JSON)
@PathVariable                        // Para variables en URL (/users/{id})
@RequestParam                        // Para query parameters (?page=1)
@RequestHeader                       // Para headers HTTP
```

### Otros

```java
@Order(1)                           // Orden de ejecución de filtros
@SpringBootApplication              // Clase principal de aplicación
```

## Patrones de Código

### Controladores REST

```java
@RestController
@RequestMapping("/recursos")
public class RecursoRestController {

    @GetMapping("/listar")
    public List<Recurso> listarRecursos() {
        // lógica
        return recursos;
    }

    @PostMapping("/crear")
    public Recurso crearRecurso(@RequestBody Recurso recurso) {
        // lógica
        return recurso;
    }
}
```

### Filtros

```java
@Component
@Order(1)
public class MiFiltro implements Filter {

    @Value("${mi.propiedad}")
    private String miPropiedad;

    private static final Logger LOG = LoggerFactory.getLogger(MiFiltro.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // lógica del filtro
        chain.doFilter(request, response);
    }
}
```

### Modelos de Dominio

```java
public class MiModelo {

    private Long id;
    private String nombre;

    // Constructor vacío
    public MiModelo() {
    }

    // Constructor con parámetros
    public MiModelo(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // ... más getters/setters
}
```

## Logging

### Uso de SLF4J

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiClase {

    private static final Logger LOG = LoggerFactory.getLogger(MiClase.class);

    public void miMetodo() {
        LOG.info("Mensaje informativo");
        LOG.debug("Mensaje de debug: {}", variable);
        LOG.warn("Advertencia");
        LOG.error("Error: {}", mensaje, excepcion);
    }
}
```

### Niveles de Log

- **TRACE** - Información muy detallada
- **DEBUG** - Información de depuración
- **INFO** - Mensajes informativos importantes
- **WARN** - Advertencias
- **ERROR** - Errores

## Gestión de Errores

### En Controladores

```java
@GetMapping("/recurso/{id}")
public ResponseEntity<Recurso> obtenerRecurso(@PathVariable Long id) {
    try {
        Recurso recurso = service.obtener(id);
        return ResponseEntity.ok(recurso);
    } catch (NoEncontradoException e) {
        return ResponseEntity.notFound().build();
    }
}
```

### En Filtros

```java
if (!esValido) {
    ((HttpServletResponse) response).sendError(HttpStatus.NOT_FOUND.value(), "Mensaje");
    return;
}
```

## Mejores Prácticas

### Inyección de Dependencias

**Preferir constructor sobre field injection**:

```java
// ✅ BIEN - Inyección por constructor
@RestController
public class MiController {

    private final MiService service;

    public MiController(MiService service) {
        this.service = service;
    }
}

// ❌ EVITAR - Field injection
@RestController
public class MiController {

    @Autowired
    private MiService service;
}
```

### Inmutabilidad

```java
// Usar final cuando sea posible
private final String constante = "valor";
private final MiService service;
```

### Separación de Responsabilidades

- **Controladores**: Solo manejo de HTTP (request/response)
- **Servicios**: Lógica de negocio
- **Repositorios**: Acceso a datos
- **Filtros**: Procesamiento transversal de peticiones

### Validación

```java
// Validar entrada de usuario
if (userName == null || userName.isEmpty()) {
    throw new IllegalArgumentException("Usuario requerido");
}
```

## Comentarios

### Javadoc para Métodos Públicos

```java
/**
 * Valida contra el apikey-manager si el usuario y apikey son correctos
 * @param userName el nombre de usuario a validar
 * @param apiKey la clave API a validar
 * @return true si las credenciales son válidas, false en caso contrario
 */
private boolean authApiKey(String userName, String apiKey) {
    // implementación
}
```

### Comentarios en Línea

- Solo cuando la lógica no sea evidente
- Explicar el "por qué", no el "qué"
- Mantener comentarios actualizados con el código

## Testing

### Nombrado de Tests

```java
@Test
void deberiaRetornarHelloWorld() {
    // arrange
    // act
    // assert
}

@Test
void deberiaLanzarExcepcionCuandoUsuarioEsNulo() {
    // arrange
    // act & assert
}
```

### Patrón AAA (Arrange-Act-Assert)

```java
@Test
void deberiaCrearDocumento() {
    // Arrange - preparar datos
    Document documento = new Document(1, "doc-123");

    // Act - ejecutar acción
    Document resultado = service.crear(documento);

    // Assert - verificar resultado
    assertNotNull(resultado);
    assertEquals("doc-123", resultado.getId());
}
```
