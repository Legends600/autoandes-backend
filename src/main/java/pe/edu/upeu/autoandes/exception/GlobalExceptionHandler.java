package pe.edu.upeu.autoandes.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import pe.edu.upeu.autoandes.dto.ErrorResponseDTO;

import java.util.LinkedHashMap;
import java.util.Map;

/** Convierte cada excepcion al formato de error comun de la seccion 3.5. */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> validacion(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(f -> campos.putIfAbsent(f.getField(), f.getDefaultMessage()));
        log.warn("Validacion fallida en {}: {}", req.getRequestURI(), campos);
        return construir(HttpStatus.BAD_REQUEST, "Datos inválidos", req, campos);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> restricciones(ConstraintViolationException ex, HttpServletRequest req) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(v -> {
            String ruta = v.getPropertyPath().toString();
            campos.putIfAbsent(ruta.substring(ruta.lastIndexOf('.') + 1), v.getMessage());
        });
        log.warn("Restricciones incumplidas en {}: {}", req.getRequestURI(), campos);
        return construir(HttpStatus.BAD_REQUEST, "Datos inválidos", req, campos);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponseDTO> peticionMalFormada(Exception ex, HttpServletRequest req) {
        log.warn("Peticion mal formada en {}: {}", req.getRequestURI(), ex.getMessage());
        return construir(HttpStatus.BAD_REQUEST, "La petición está mal formada o tiene parámetros inválidos", req, null);
    }

    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<ErrorResponseDTO> solicitudInvalida(SolicitudInvalidaException ex, HttpServletRequest req) {
        log.warn("Solicitud invalida en {}: {}", req.getRequestURI(), ex.getMessage());
        return construir(HttpStatus.BAD_REQUEST, ex.getMessage(), req, null);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponseDTO> credenciales(CredencialesInvalidasException ex, HttpServletRequest req) {
        return construir(HttpStatus.UNAUTHORIZED, ex.getMessage(), req, null);
    }

    // @PreAuthorize lanza esta excepcion dentro de MVC: sin este metodo caeria en el 500 generico
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> accesoDenegado(AccessDeniedException ex, HttpServletRequest req) {
        log.warn("Acceso denegado a {}", req.getRequestURI());
        return construir(HttpStatus.FORBIDDEN, "No tiene permiso para realizar esta operación", req, null);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> noEncontrado(RecursoNoEncontradoException ex, HttpServletRequest req) {
        log.warn("Recurso no encontrado en {}: {}", req.getRequestURI(), ex.getMessage());
        return construir(HttpStatus.NOT_FOUND, ex.getMessage(), req, null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDTO> rutaInexistente(NoResourceFoundException ex, HttpServletRequest req) {
        return construir(HttpStatus.NOT_FOUND, "El recurso solicitado no existe", req, null);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponseDTO> reglaNegocio(ReglaNegocioException ex, HttpServletRequest req) {
        log.warn("Regla de negocio rechazada en {}: {}", req.getRequestURI(), ex.getMessage());
        return construir(HttpStatus.CONFLICT, ex.getMessage(), req, null);
    }

    // Red de seguridad: la base rechazo un dato (por ejemplo, un duplicado concurrente)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> integridad(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("Violacion de integridad en {}: {}", req.getRequestURI(), ex.getMostSpecificCause().getMessage());
        return construir(HttpStatus.CONFLICT, "La operación viola una restricción de integridad de los datos", req, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> metodoNoPermitido(HttpRequestMethodNotSupportedException ex,
                                                              HttpServletRequest req) {
        return construir(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP no permitido para esta ruta", req, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> inesperado(Exception ex, HttpServletRequest req) {
        log.error("Error inesperado en {}", req.getRequestURI(), ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", req, null);
    }

    private ResponseEntity<ErrorResponseDTO> construir(HttpStatus status, String mensaje, HttpServletRequest req,
                                                       Map<String, String> campos) {
        return ResponseEntity.status(status)
                .body(ErrorResponseDTO.of(status.value(), status.getReasonPhrase(), mensaje, req.getRequestURI(), campos));
    }
}
