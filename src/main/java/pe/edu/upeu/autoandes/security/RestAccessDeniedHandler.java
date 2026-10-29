package pe.edu.upeu.autoandes.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;
import pe.edu.upeu.autoandes.dto.ErrorResponseDTO;

import java.io.IOException;

/** 403: el token es valido pero el usuario no tiene el permiso. Responde con el formato de error comun. */
@Slf4j
@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final JsonMapper jsonMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.warn("Acceso denegado: {} {} (usuario {})", request.getMethod(), request.getRequestURI(),
                request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "desconocido");
        ErrorResponseDTO cuerpo = ErrorResponseDTO.of(HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(), "No tiene permiso para realizar esta operación",
                request.getRequestURI(), null);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(jsonMapper.writeValueAsString(cuerpo));
    }
}
