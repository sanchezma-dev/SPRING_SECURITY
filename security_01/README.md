# Spring Security - Example 1

Esta es una aplicación de demostración simple de **Spring Security** con configuración básica.

## 📌 Características
- Protege endpoints específicos utilizando **Spring Security**.
- Usa **autenticación básica (Basic Auth)** y **formulario de inicio de sesión**.
- Implementa seguridad con `SecurityFilterChain`.


## 📜 Configuraciones de Seguridad
### 🔹 Cambios en Spring Security 6
- **`@EnableWebSecurity` ya no es necesario** (antes de la versión 6 era obligatorio, ahora viene por defecto en Spring Boot).
- **No se necesita extender `WebSecurityConfigurerAdapter`** (ahora se usa `SecurityFilterChain`).

### 🔹 Configuración de Seguridad (`SecurityConfig`)
```java
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth ->
                        auth.requestMatchers("app/security/protected01", "app/security/protected02")
                                .authenticated()
                                .anyRequest().permitAll()) // Permitir todos los demás endpoints sin autenticación
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());

        return httpSecurity.build();
    }
}
```
📌 **Explicación:**
- Se requiere autenticación para los endpoints `/protected01` y `/protected02`.
- Todos los demás endpoints son de acceso público.
- Soporta autenticación **Basic Auth** y **formulario de login**.

Prueba los endpoints:
   - `GET http://localhost:8080/app/security/greetings` → **Sin autenticación** ✅
   - `GET http://localhost:8080/app/security/protected01` → **Requiere autenticación** 🔒
   - `GET http://localhost:8080/app/security/protected02` → **Requiere autenticación** 🔒

## 📖 Recursos adicionales
- 📚 **Documentación oficial de propiedades de seguridad**: [Spring Boot Security Properties](https://docs.spring.io/spring-boot/appendix/application-properties/#appendix.application-properties.security)


