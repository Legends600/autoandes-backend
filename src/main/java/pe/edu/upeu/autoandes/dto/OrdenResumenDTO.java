package pe.edu.upeu.autoandes.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Fila de la consulta de ordenes: la cabecera sin detalle. */
public record OrdenResumenDTO(
        Long id,
        String numero,
        LocalDateTime fecha,
        Long vehiculoId,
        String placa,
        String clienteNombre,
        Integer kilometraje,
        String estado,
        BigDecimal total,
        String usuarioRegistro) {
}
