package jsm.security.security_01.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
// @EnableWebSecurity para versiones anteriores de spring security 6
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth ->
//                        auth.requestMatchers("app/security/**") // para todos los path a partir de ...
                        auth.requestMatchers("app/security/protected01", "app/security/protected02")
                                .authenticated()
                                .anyRequest().permitAll()) // Sin permisos todos los demás endpoint
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());

        return httpSecurity.build();

    }
}
