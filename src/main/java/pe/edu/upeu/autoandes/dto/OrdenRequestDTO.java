package pe.edu.upeu.autoandes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/** El cliente HTTP no envia precios, subtotales ni total: todo eso se calcula en el servicio. */
public record OrdenRequestDTO(

        @NotNull(message = "El vehículo es obligatorio")
        Long vehiculoId,

        @NotNull(message = "El kilometraje es obligatorio")
        @Min(value = 0, message = "El kilometraje no puede ser negativo")
        Integer kilometraje,

        @NotEmpty(message = "La orden debe tener al menos un detalle")
        @Size(max = 10, message = "La orden admite como máximo 10 detalles")
        List<@Valid DetalleRequestDTO> detalles) {
}
