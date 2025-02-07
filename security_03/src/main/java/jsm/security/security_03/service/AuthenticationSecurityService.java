package jsm.security.security_03.service;

import jsm.security.security_03.entity.UserSecurity;
import jsm.security.security_03.repository.UserSecurityRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
            throw new AuthenticationException("Invalid credentials") {
            };
        }

        var roles = userSecurityFromDB.getRoles();
        var authorities = roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName())) //NECESARIO "ROLE_" SI EN BASE DE DATOS NO ESTÁ.
                .toList();

        return new UsernamePasswordAuthenticationToken(userSecurityFromDB, pwd, authorities);
    }

    // Este método obligado de AuthenticationProvider le dice a Spring security, que puede
    // usar este AuthenticationProvider (gracias al UsernamePasswordAuthenticationToken). En caso false
    // spring buscaría otro AuthenticationProvider
    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
