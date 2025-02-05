package jsm.security.security_02.repository;

import jsm.security.security_02.entity.UserSecurity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSecurityRepository extends JpaRepository<UserSecurity, Long> {

    UserSecurity findByEmail(String email);


}
