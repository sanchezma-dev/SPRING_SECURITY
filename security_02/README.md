# Spring Security - Example 2

## Descripcion

Este proyecto implementa **Spring Security** con validacion de usuarios a traves de base de datos. Se utiliza **UserDetailsService** para cargar los datos de usuario desde una entidad JPA.

Para desplegar, se hace por **docker-compose**, y desde Spring se crea la entidad e inserts en la base de datos.

## Configuracion de Seguridad

La seguridad en esta aplicacion se basa en los siguientes conceptos clave:

### **UserDetailsService** ⚠️🔐  

> **ATENCION:**  
> Spring Security **siempre** ejecuta `UserDetailsService` automaticamente en cada peticion que requiere autenticacion.  
> - Si un endpoint esta configurado en `SecurityConfig` como **protegido**, Spring invoca `UserDetailsService` antes de llegar al controlador.  
> - Si un endpoint **no** esta protegido, **Spring no pasa por `UserDetailsService` y permite el acceso sin autenticacion**.

Spring Security requiere un servicio que implemente la interfaz `UserDetailsService` para obtener los detalles de autenticacion de un usuario. En esta aplicacion, la implementacion carga los usuarios desde la base de datos:

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

### **Configuracion de Seguridad** 🔐  

La configuracion de **Spring Security** se realiza mediante `SecurityFilterChain`:

```java
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth ->
                        auth.requestMatchers("app/security/protected01", "app/security/protected02")
                                .authenticated()
                                .anyRequest().permitAll()) // Los demas endpoints son publicos
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

### **Notas importantes** ⚠️

- 🔹 **Spring Security protege los endpoints** configurados en `SecurityConfig`, ejecutando `UserDetailsService` automaticamente en cada peticion autenticada.  
- 🔹 Los endpoints **publicos** no pasan por `UserDetailsService`.  
- 🔹 Se utiliza `NoOpPasswordEncoder`, pero **en produccion se debe usar un encriptador seguro como BCrypt**.  

## Recursos 📚  

- Propiedades de Spring Security: [Spring Boot Security Properties](https://docs.spring.io/spring-boot/appendix/application-properties/#appendix.application-properties.security)  

