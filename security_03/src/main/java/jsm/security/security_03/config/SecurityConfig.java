package jsm.security.security_03.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
        // Bean solo para no tener que codificar el pass de usuarios, es deprecated, solo para local
        // Es decir, permite hacer el match de PasswordEncoder sin codificar esa comparación (ya que en bbdd se encuentra
        // sin para esta prueba)
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }


}
