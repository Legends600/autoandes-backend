package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnularOrdenRequestDTO(

        @NotBlank(message = "El motivo de anulación es obligatorio")
        @Size(min = 10, max = 200, message = "El motivo debe tener entre 10 y 200 caracteres")
        String motivo) {
}
