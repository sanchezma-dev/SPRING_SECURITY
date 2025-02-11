package jsm.security.security_04.config;

import jsm.security.security_04.CustomResponseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class SecurityConfig {

    // FIXME .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    //  ESTO Desactiva la gestión de sesiones, haciendo que el sistema sea sin estado (stateless).
    //  Esto significa que no se almacena información de la sesión del usuario en el servidor.
    //  Cada solicitud debe incluir un JWT válido para ser autenticada y autorizada, ya que el servidor
    //  no mantiene el estado entre las peticiones.


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JWTValidationFilter jwtValidationFilter, CustomResponseHandler responseHandler) throws Exception {
        httpSecurity
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT sin sesiones
//                .cors(Customizer.withDefaults()) // Habilita CORS //FIXME No es necesario su habilitación
                .csrf(csrf -> csrf.disable()) // Deshabilita CSRF para permitir peticiones externas como Postman/cURL
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("app/security/authLogin").permitAll() // Permite el login sin autenticación
                                .requestMatchers("app/security/protected01").hasRole("ADMIN")
                                .requestMatchers("app/security/protected02").hasAnyRole("USER", "SUPERVISOR")
                                .anyRequest().permitAll() // Resto de rutas requieren autenticación

                )
                .exceptionHandling(ex -> ex.accessDeniedHandler(responseHandler))
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());
        httpSecurity.addFilterAfter(jwtValidationFilter, BasicAuthenticationFilter.class);

        return httpSecurity.build();
    }


    @Bean
        // Bean solo para no tener que codificar el pass de usuarios, es deprecated, solo para local
        // Es decir, permite hacer el match de PasswordEncoder sin codificar esa comparación (ya que en bbdd se encuentra
        // sin para esta prueba)
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }


    // AuthenticationManager. Esta interfaz de Spring Security es el punto central para gestionar el proceso de autenticación en una aplicación.
    // responsable de autenticar a un usuario proporcionando credenciales (como nombre de usuario y contraseña) y verificando si esas credenciales son correctas.
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
