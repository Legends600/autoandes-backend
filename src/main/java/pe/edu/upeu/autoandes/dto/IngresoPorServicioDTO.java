package pe.edu.upeu.autoandes.dto;

import java.math.BigDecimal;

/** Proyeccion del reporte de ingresos: una fila por servicio, armada directamente por la consulta JPQL. */
public record IngresoPorServicioDTO(
        String codigo,
        String servicio,
        Long cantidadTotal,
        BigDecimal importeTotal) {
}
