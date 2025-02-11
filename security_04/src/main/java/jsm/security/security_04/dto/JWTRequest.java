package jsm.security.security_04.dto;

import lombok.Data;

@Data
public class JWTRequest {
    private String username;
    private String password;

}
