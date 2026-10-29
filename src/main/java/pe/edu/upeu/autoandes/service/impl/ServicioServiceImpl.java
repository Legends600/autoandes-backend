package pe.edu.upeu.autoandes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.autoandes.dto.ServicioRequestDTO;
import pe.edu.upeu.autoandes.dto.ServicioResponseDTO;
import pe.edu.upeu.autoandes.entity.Servicio;
import pe.edu.upeu.autoandes.exception.RecursoNoEncontradoException;
import pe.edu.upeu.autoandes.exception.ReglaNegocioException;
import pe.edu.upeu.autoandes.mapper.ServicioMapper;
import pe.edu.upeu.autoandes.repository.DetalleOrdenRepository;
import pe.edu.upeu.autoandes.repository.ServicioRepository;
import pe.edu.upeu.autoandes.service.ServicioService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository servicioRepository;
    private final DetalleOrdenRepository detalleOrdenRepository;
    private final ServicioMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<ServicioResponseDTO> listar() {
        return servicioRepository.findAll(Sort.by("codigo")).stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ServicioResponseDTO obtener(Long id) {
        return mapper.toResponse(buscar(id));
    }

    @Override
    @Transactional
    public ServicioResponseDTO crear(ServicioRequestDTO request) {
        String codigo = request.codigo().trim();
        // RN-01: el codigo de servicio es unico
        if (servicioRepository.existsByCodigo(codigo)) {
            throw new ReglaNegocioException("Ya existe un servicio con el código " + codigo);
        }
        Servicio guardado = servicioRepository.save(mapper.toEntity(request));
        log.info("Servicio creado: id={}, codigo={}", guardado.getId(), guardado.getCodigo());
        return mapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public ServicioResponseDTO actualizar(Long id, ServicioRequestDTO request) {
        Servicio servicio = buscar(id);
        String codigo = request.codigo().trim();
        if (servicioRepository.existsByCodigoAndIdNot(codigo, id)) {
            throw new ReglaNegocioException("Ya existe otro servicio con el código " + codigo);
        }
        mapper.copiar(request, servicio);
        log.info("Servicio actualizado: id={}, codigo={}", servicio.getId(), servicio.getCodigo());
        return mapper.toResponse(servicio);
    }

    @Override
    @Transactional
    public ServicioResponseDTO cambiarEstado(Long id, boolean activo) {
        Servicio servicio = buscar(id);
        servicio.setActivo(activo);
        log.info("Servicio {} {}: id={}", servicio.getCodigo(), activo ? "activado" : "dado de baja", id);
        return mapper.toResponse(servicio);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Servicio servicio = buscar(id);
        // RN-02: un servicio usado en alguna orden no se elimina fisicamente, se da de baja con PATCH
        if (detalleOrdenRepository.existsByServicioId(id)) {
            throw new ReglaNegocioException("El servicio " + servicio.getCodigo()
                    + " está usado en órdenes y no se puede eliminar; use PATCH /servicios/" + id + "/estado para darlo de baja");
        }
        servicioRepository.delete(servicio);
        log.info("Servicio eliminado: id={}, codigo={}", id, servicio.getCodigo());
    }

    private Servicio buscar(Long id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el servicio con id " + id));
    }
}
