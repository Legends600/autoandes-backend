package pe.edu.upeu.autoandes.dto;

/** Nunca incluye la entidad Cliente anidada: solo su id y su nombre. */
public record VehiculoResponseDTO(
        Long id,
        String placa,
        String marca,
        String modelo,
        Integer anio,
        Long clienteId,
        String clienteNombre) {
}
