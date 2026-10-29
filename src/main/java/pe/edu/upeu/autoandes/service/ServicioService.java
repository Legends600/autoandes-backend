package pe.edu.upeu.autoandes.service;

import pe.edu.upeu.autoandes.dto.ServicioRequestDTO;
import pe.edu.upeu.autoandes.dto.ServicioResponseDTO;

import java.util.List;

public interface ServicioService {

    List<ServicioResponseDTO> listar();

    ServicioResponseDTO obtener(Long id);

    ServicioResponseDTO crear(ServicioRequestDTO request);

    ServicioResponseDTO actualizar(Long id, ServicioRequestDTO request);

    ServicioResponseDTO cambiarEstado(Long id, boolean activo);

    void eliminar(Long id);
}
