package pe.edu.upeu.autoandes.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/** Formato de error comun (seccion 3.5). "campos" solo aparece en los errores de validacion. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String ruta,
        Map<String, String> campos) {

    public static ErrorResponseDTO of(int status, String error, String mensaje, String ruta,
                                      Map<String, String> campos) {
        return new ErrorResponseDTO(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                status, error, mensaje, ruta, campos);
    }
}
