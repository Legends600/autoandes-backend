package pe.edu.upeu.autoandes.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Orden completa con su detalle. */
public record OrdenResponseDTO(
        Long id,
        String numero,
        LocalDateTime fecha,
        Long vehiculoId,
        String placa,
        String clienteNombre,
        Integer kilometraje,
        String estado,
        BigDecimal total,
        String usuarioRegistro,
        String motivoAnulacion,
        List<DetalleResponseDTO> detalles) {
}
