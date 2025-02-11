package jsm.security.security_04.service;

import jsm.security.security_04.entity.UserSecurity;
import jsm.security.security_04.repository.UserSecurityRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class JWTUserDetailService implements UserDetailsService {


    private final UserSecurityRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserSecurity userSecurity = repository.findByEmail(username);

        return Optional.ofNullable(userSecurity)
                .map(userDB -> {
                    final var authorities = userDB.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())).toList();

                    return new User(userDB.getEmail(), userDB.getPwd(), authorities);
                }).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
