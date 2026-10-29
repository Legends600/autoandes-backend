package pe.edu.upeu.autoandes.service;

import pe.edu.upeu.autoandes.dto.AnularOrdenRequestDTO;
import pe.edu.upeu.autoandes.dto.OrdenRequestDTO;
import pe.edu.upeu.autoandes.dto.OrdenResponseDTO;
import pe.edu.upeu.autoandes.dto.OrdenResumenDTO;
import pe.edu.upeu.autoandes.entity.EstadoOrden;

import java.time.LocalDate;
import java.util.List;

public interface OrdenService {

    OrdenResponseDTO registrar(OrdenRequestDTO request, String usuario);

    OrdenResponseDTO anular(Long id, AnularOrdenRequestDTO request);

    OrdenResponseDTO obtener(Long id);

    /** Todos los filtros son opcionales y se combinan entre si. */
    List<OrdenResumenDTO> listar(String placa, EstadoOrden estado, LocalDate desde, LocalDate hasta);
}
