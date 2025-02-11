package jsm.security.security_04.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// Esta clase gestiona las solicitudes no autenticadas, es decir, aquellas que no incluyen un JWT válido.
// Implementa la interfaz AuthenticationEntryPoint de Spring Security y su objetivo es manejar las peticiones
// que intentan acceder a recursos protegidos sin autenticación. Retorna el error de no autorización
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
