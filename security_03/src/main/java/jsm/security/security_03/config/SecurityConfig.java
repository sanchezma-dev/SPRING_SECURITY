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
//                        auth.requestMatchers("app/security/**") // para todos los path a partir de ...
                        auth.requestMatchers("app/security/protected01", "app/security/protected02")
                                .authenticated()
                                .anyRequest().permitAll()) // Sin control para todos los demás endpoint
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());

        return httpSecurity.build();

    }


    @Bean // Bean solo para no tener que codificar el pass de usuarios, es deprecated, solo para local
    PasswordEncoder passwordEncoder() {
        return  NoOpPasswordEncoder.getInstance();
    }
}
