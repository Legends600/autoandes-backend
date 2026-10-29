package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Una linea de la orden: solo viaja el servicio y la cantidad; el precio lo pone el backend. */
public record DetalleRequestDTO(

        @NotNull(message = "El servicio es obligatorio")
        Long servicioId,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad mínima es 1")
        @Max(value = 5, message = "La cantidad máxima es 5")
        Integer cantidad) {
}
