package jsm.security.security_03.service;

import jsm.security.security_03.entity.UserSecurity;
import jsm.security.security_03.repository.UserSecurityRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
/* Como las peticiones a endpoint en SecurityConfig requieren autorización, spring security pasa automaticamente por la implementación
de la interfaz UserDetailsService para comprobar el usuario.
 */
public class UserSecurityServiceDetails implements UserDetailsService {

    private final UserSecurityRepository repository;


    @Override
    public UserDetails loadUserByUsername(String username) {
        UserSecurity userSecurity = repository.findByEmail(username);

        if (userSecurity == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        var authorities = List.of(new SimpleGrantedAuthority(userSecurity.getRol()));
        // User, de security, objeto propio del framework
        return new User(userSecurity.getEmail(), userSecurity.getPwd(), authorities);

    }
}
