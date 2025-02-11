# 📌 Documentación del sistema de autenticación con JWT en Spring Boot


### ⚠️ Requisitos Previos

Antes de realizar pruebas, es necesario:

- Levantar el Docker Compose.
- Ejecutar los scripts de forma manual, que se encuentran en la carpeta sql.
- Importar los JSON de Postman para ejecutar pruebas sobre la API.

## Dependencias utilizadas
Para implementar la autenticación con JSON Web Tokens (JWT), hemos incluido las siguientes dependencias en el archivo `pom.xml`:

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
```

Esta versión de `jjwt` permite el uso de los métodos de `Jwts`, como el siguiente:

```java
return Jwts
    .parserBuilder()
    .setSigningKey(key)
    .build()
    .parseClaimsJws(token)
    .getBody();
```

## 🔐 Flujo de Autenticación

El sistema de autenticación con JWT en esta aplicación sigue el siguiente flujo:

1️⃣ Inicio de Sesión

El usuario envía sus credenciales (username y password) a la API a través de una petición POST al endpoint /authLogin.

La API recibe la solicitud y valida las credenciales consultando la base de datos.

2️⃣ Generación del Token

Si las credenciales son correctas, el sistema genera un token JWT firmado con la clave secreta.

Este token contiene la información del usuario y sus roles, lo que permitirá autorizar accesos en futuras peticiones.

La API devuelve el token al usuario en la respuesta.

3️⃣ Uso del Token en Peticiones Protegidas

El cliente debe incluir el token en cada petición a los endpoints protegidos, enviándolo en el header:

Authorization: Bearer token 

Por ejemplo, para acceder a /protected01, que requiere autenticación (según lo definido en SecurityConfig), el cliente debe enviar el token de inicio de sesión.

4️⃣ Validación del Token

En cada petición a un endpoint protegido, el sistema intercepta la solicitud y verifica el token JWT.
Esto se hace a través de la implementación de un filtro personalizado que extiende OncePerRequestFilter (interfaz que intercepta cada solicitud
http, se ejecuta una vez por petición y ejecuta el método doFilterInternal que se encarga de la validación)

Si el token es válido y no ha expirado, se extrae la información del usuario y se concede el acceso.

Si el token es inválido, ha expirado o no se envía, la API devuelve un error 401 Unauthorized.

5️⃣ Acceso a Recursos Protegidos

Si el token es válido y el usuario tiene los permisos adecuados, la API permite el acceso al recurso solicitado.

La autorización (qué usuarios pueden acceder a qué endpoints) está definida en la clase SecurityConfig.

## Explicación de las clases principales

### `JWTService`
Servicio encargado de:
- Generar tokens JWT.
- Extraer `claims` (información embebida en el token).
- Validar la expiración del token.

Ejemplo de generación de token:

```java
public String generateToken(UserDetails userDetails) {
    final Map<String, Object> claims = Collections.singletonMap("ROLES", userDetails.getAuthorities().toString());
    return this.getToken(claims, userDetails.getUsername());
}
```

### `JWTUserDetailService`
Implementa `UserDetailsService` para cargar los detalles del usuario desde la base de datos y asignarle sus roles:

```java
@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserSecurity userSecurity = repository.findByEmail(username);
    return Optional.ofNullable(userSecurity)
            .map(userDB -> {
                final var authorities = userDB.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                    .toList();
                return new User(userDB.getEmail(), userDB.getPwd(), authorities);
            })
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
}
```

### `JwtAuthenticationEntryPoint`
Maneja respuestas de error cuando un usuario no está autenticado:

```java
@Override
public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
}
```

### `AuthController`
Expone un endpoint `/authLogin` para autenticar a los usuarios y generar el token JWT:

```java
@PostMapping("/authLogin")
public ResponseEntity<JWTResponse> authLogin(@RequestBody JWTRequest request) {
    this.authenticate(request);
    final var userDetails = this.jwtUserDetailService.loadUserByUsername(request.getUsername());
    final var token = this.jwtService.generateToken(userDetails);
    return ResponseEntity.ok(new JWTResponse(token));
}
```

`AuthenticationManager` se encarga de validar las credenciales:

```java
private void authenticate(JWTRequest request) {
    try {
        this.authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    } catch (BadCredentialsException | DisabledException e) {
        throw new RuntimeException(e.getMessage());
    }
}
```

### `JWTValidationFilter`
Filtro que intercepta peticiones para validar el JWT enviado en el header `Authorization`:

```java
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    final var requestTokenHeader = request.getHeader(AUTHORIZATION_HEADER);
    String username = null;
    String jwt = null;

    if (Objects.nonNull(requestTokenHeader) && requestTokenHeader.startsWith(AUTHORIZATION_HEADER_BEARER)) {
        jwt = requestTokenHeader.substring(7);
        try {
            username = jwtService.getUsernameFromToken(jwt);
        } catch (IllegalArgumentException | ExpiredJwtException e) {
            log.warn(e.getMessage());
        }
    }

    if (Objects.nonNull(username) && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
        final var userDetails = this.jwtUserDetailService.loadUserByUsername(username);
        if (this.jwtService.validateToken(jwt, userDetails)) {
            var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }
    filterChain.doFilter(request, response);
}
```

### `SecurityConfig`
Configura la seguridad de los endpoints:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JWTValidationFilter jwtValidationFilter, CustomResponseHandler responseHandler) throws Exception {
    httpSecurity
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth ->
            auth.requestMatchers("app/security/authLogin").permitAll()
                .requestMatchers("app/security/protected01").hasRole("ADMIN")
                .requestMatchers("app/security/protected02").hasAnyRole("USER", "SUPERVISOR")
                .anyRequest().permitAll()
        )
        .exceptionHandling(ex -> ex.accessDeniedHandler(responseHandler))
        .httpBasic(Customizer.withDefaults());
    
    httpSecurity.addFilterAfter(jwtValidationFilter, BasicAuthenticationFilter.class);
    return httpSecurity.build();
}
```

### `AuthenticationManager`
Administra el proceso de autenticación:

```java
@Bean
AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
}
```

## Conclusión
Este sistema de autenticación con JWT permite un control seguro y eficiente de usuarios autenticados, con validación de tokens en cada petición. Se recomienda almacenar la clave JWT en un archivo de propiedades para mayor seguridad.

