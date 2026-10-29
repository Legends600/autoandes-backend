package pe.edu.upeu.autoandes.dto;

import java.math.BigDecimal;

public record DetalleResponseDTO(
        Long id,
        Long servicioId,
        String servicioCodigo,
        String servicioNombre,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal) {
}
