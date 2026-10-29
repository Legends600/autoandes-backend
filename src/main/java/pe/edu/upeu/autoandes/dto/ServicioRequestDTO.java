package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ServicioRequestDTO(

        @NotBlank(message = "El código es obligatorio")
        @Pattern(regexp = "^SRV-\\d{3}$", message = "El código debe tener el formato SRV-000")
        String codigo,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres")
        String nombre,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.00", inclusive = false, message = "El precio debe ser mayor que cero")
        BigDecimal precio,

        @NotNull(message = "La duración es obligatoria")
        @Min(value = 15, message = "La duración mínima es de 15 minutos")
        @Max(value = 480, message = "La duración máxima es de 480 minutos")
        Integer duracionMin) {
}
