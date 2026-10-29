package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VehiculoRequestDTO(

        @NotBlank(message = "La placa es obligatoria")
        @Pattern(regexp = "^[A-Z]{3}-\\d{3}$", message = "La placa debe tener el formato ABC-123")
        String placa,

        @NotBlank(message = "La marca es obligatoria")
        @Size(max = 40, message = "La marca no puede superar 40 caracteres")
        String marca,

        @NotBlank(message = "El modelo es obligatorio")
        @Size(max = 40, message = "El modelo no puede superar 40 caracteres")
        String modelo,

        @NotNull(message = "El año es obligatorio")
        @Min(value = 1990, message = "El año no puede ser menor que 1990")
        @Max(value = 2027, message = "El año no puede ser mayor que 2027")
        Integer anio,

        @NotNull(message = "El cliente es obligatorio")
        Long clienteId) {
}
