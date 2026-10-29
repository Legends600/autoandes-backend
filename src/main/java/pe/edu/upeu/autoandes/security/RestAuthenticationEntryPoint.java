package pe.edu.upeu.autoandes.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pe.edu.upeu.autoandes.config.JwtAuthenticationFilter;
import pe.edu.upeu.autoandes.dto.ErrorResponseDTO;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

/** 401: sin token, token alterado o vencido. Responde con el formato de error comun. */
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final JsonMapper jsonMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        Object motivo = request.getAttribute(JwtAuthenticationFilter.ATRIBUTO_MOTIVO);
        String mensaje = motivo != null ? motivo.toString() : "Se requiere autenticación para acceder a este recurso";
        ErrorResponseDTO cuerpo = ErrorResponseDTO.of(HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(), mensaje, request.getRequestURI(), null);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(jsonMapper.writeValueAsString(cuerpo));
    }
}
