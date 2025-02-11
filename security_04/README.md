# Proyecto de Seguridad con Spring Boot

## Requisitos previos
Antes de realizar pruebas, es necesario:
1. **Levantar el Docker Compose**.
2. **Ejecutar los scripts de forma manual**, que se encuentran en la carpeta `sql`.
3. **Importar los JSON de Postman** para ejecutar pruebas sobre la API.

---

## Estructura del Proyecto
El proyecto implementa seguridad en una API con **Spring Security**, gestionando autenticación y autorización a través de roles.

### Entidades

#### `UserSecurity`
Entidad que representa a un usuario dentro del sistema.

#### `RolesSecurity`
Entidad que define los roles asignados a los usuarios. Un usuario puede tener uno o más roles.

Relación entre ambas:
- **Un usuario puede tener múltiples roles**.
- **Cada rol pertenece a un usuario**.

---

## Implementación de Seguridad

### `SimpleGrantedAuthority`
`SimpleGrantedAuthority` es una implementación de `GrantedAuthority` de Spring Security. Representa un rol o permiso asignado a un usuario dentro del sistema.

#### Importante sobre los roles:
- **Spring Security siempre busca roles con el prefijo `ROLE_`**.
- Si en la base de datos los roles se almacenan sin este prefijo, es necesario concatenarlo en el servicio.
- Alternativamente, los roles pueden guardarse directamente con `ROLE_` en la base de datos.

Ejemplo de conversión:
```java
var authorities = roles.stream()
    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
    .toList();
```

---

## Flujo de Autenticación y Autorización

### `SecurityConfig`
```java
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth ->
                        auth.requestMatchers("app/security/protected01")
                                .hasRole("ADMIN")
                                .requestMatchers("app/security/protected02")
                                .hasAnyRole("USER", "SUPERVISOR")
                                .anyRequest().permitAll())
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());

        return httpSecurity.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // ⚠ Deprecated, solo para pruebas locales.
    }
}
```
**Nota:** `NoOpPasswordEncoder` se usa solo para desarrollo local, ya que no realiza cifrado de contraseñas. En producción, se recomienda usar `BCryptPasswordEncoder` u otro mecanismo seguro.

---

## Servicio de Autenticación

```java
@Service
@AllArgsConstructor
public class AuthenticationSecurityService implements AuthenticationProvider {

    private final UserSecurityRepository repository;
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final var username = authentication.getName();
        final var pwd = authentication.getCredentials().toString();

        UserSecurity userSecurityFromDB = this.repository.findByEmail(username);
        if (userSecurityFromDB == null || !passwordEncoder.matches(pwd, userSecurityFromDB.getPwd())) {
            throw new AuthenticationException("Invalid credentials") {};
        }

        var roles = userSecurityFromDB.getRoles();
        var authorities = roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName())) // 🔹 Necesario si en BBDD no está ROLE_
                .toList();

        return new UsernamePasswordAuthenticationToken(userSecurityFromDB, pwd, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
```

---

## Controlador de Seguridad

```java
@RestController
@RequestMapping("app/security")
public class MySecurityController {

    @GetMapping("/greetings")
    public ResponseEntity<String> greetings() {
        return ResponseEntity.ok("Welcome!!");
    }

    @GetMapping("/protected01")
    public ResponseEntity<String> protected01() {
        return ResponseEntity.ok("Protected resource: 01!!");
    }

    @GetMapping("/protected02")
    public ResponseEntity<String> protected02() {
        return ResponseEntity.ok("Protected resource: 02!!");
    }
}
```

---

## Conclusión
Este proyecto proporciona una base para implementar autenticación y autorización con Spring Security, permitiendo definir roles y proteger endpoints según permisos. Para pruebas, seguir los pasos indicados en los **Requisitos previos**.

