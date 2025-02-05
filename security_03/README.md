# Spring Security - Example 2

## Descripción

Este proyecto implementa **Spring Security** con validación de usuarios a través de base de datos. Se utiliza **UserDetailsService** para cargar los datos de usuario desde una entidad JPA.

Para desplegar, se hace por docker-compose y desde spring se crea la entidad e inserts en la base de datos

## Configuración de Seguridad

La seguridad en esta aplicación se basa en los siguientes conceptos clave:

### **UserDetailsService**

Spring Security requiere un servicio que implemente la interfaz `UserDetailsService` para obtener los detalles de autenticación de un usuario. En esta aplicación, la implementación carga los usuarios desde la base de datos:

```java
@Service
@AllArgsConstructor
public class UserSecurityServiceDetails implements UserDetailsService {

    private final UserSecurityRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserSecurity userSecurity = repository.findByEmail(username);

        if (userSecurity == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        var authorities = List.of(new SimpleGrantedAuthority(userSecurity.getRol()));
        return new User(userSecurity.getEmail(), userSecurity.getPwd(), authorities);
    }
}
```

### **Configuración de Seguridad**

La configuración de **Spring Security** se realiza mediante `SecurityFilterChain`:

```java
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth ->
                        auth.requestMatchers("app/security/protected01", "app/security/protected02")
                                .authenticated()
                                .anyRequest().permitAll())
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());

        return httpSecurity.build();
    }

    @Bean // Bean solo para no tener que codificar el pass de usuarios, es deprecated, solo para local
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
```

### **Notas importantes**

- La seguridad se basa en la validación de usuarios desde la base de datos.
- La aplicación utiliza `UserDetailsService` para cargar el usuario por email.
- Spring Security **siempre** pasa por `UserDetailsService` antes de procesar la petición si el endpoint requiere autenticación.
- Se usa `NoOpPasswordEncoder`, pero **en producción se debe utilizar un encriptador seguro como BCrypt**.

## Recursos

- Propiedades de Spring Security: [Spring Boot Security Properties](https://docs.spring.io/spring-boot/appendix/application-properties/#appendix.application-properties.security)



