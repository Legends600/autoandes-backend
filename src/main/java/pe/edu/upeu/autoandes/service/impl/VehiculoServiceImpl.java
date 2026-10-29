package pe.edu.upeu.autoandes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.autoandes.dto.VehiculoRequestDTO;
import pe.edu.upeu.autoandes.dto.VehiculoResponseDTO;
import pe.edu.upeu.autoandes.entity.Cliente;
import pe.edu.upeu.autoandes.entity.Vehiculo;
import pe.edu.upeu.autoandes.exception.RecursoNoEncontradoException;
import pe.edu.upeu.autoandes.exception.ReglaNegocioException;
import pe.edu.upeu.autoandes.mapper.VehiculoMapper;
import pe.edu.upeu.autoandes.repository.ClienteRepository;
import pe.edu.upeu.autoandes.repository.OrdenServicioRepository;
import pe.edu.upeu.autoandes.repository.VehiculoRepository;
import pe.edu.upeu.autoandes.service.VehiculoService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final ClienteRepository clienteRepository;
    private final OrdenServicioRepository ordenRepository;
    private final VehiculoMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<VehiculoResponseDTO> listar(Long clienteId) {
        List<Vehiculo> vehiculos = clienteId == null
                ? vehiculoRepository.findAllByOrderByPlacaAsc()
                : vehiculoRepository.findByClienteIdOrderByPlacaAsc(clienteId);
        return vehiculos.stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculoResponseDTO obtener(Long id) {
        return mapper.toResponse(buscar(id));
    }

    @Override
    @Transactional
    public VehiculoResponseDTO crear(VehiculoRequestDTO request) {
        Cliente cliente = buscarCliente(request.clienteId());
        String placa = request.placa().trim();
        // RN-02 (vehiculos): la placa es unica
        if (vehiculoRepository.existsByPlaca(placa)) {
            throw new ReglaNegocioException("Ya existe un vehículo con la placa " + placa);
        }
        Vehiculo vehiculo = new Vehiculo();
        mapper.copiar(request, vehiculo, cliente);
        Vehiculo guardado = vehiculoRepository.save(vehiculo);
        log.info("Vehiculo creado: id={}, placa={}, clienteId={}", guardado.getId(), guardado.getPlaca(), cliente.getId());
        return mapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public VehiculoResponseDTO actualizar(Long id, VehiculoRequestDTO request) {
        Vehiculo vehiculo = buscar(id);
        Cliente cliente = buscarCliente(request.clienteId());
        String placa = request.placa().trim();
        if (vehiculoRepository.existsByPlacaAndIdNot(placa, id)) {
            throw new ReglaNegocioException("Ya existe otro vehículo con la placa " + placa);
        }
        mapper.copiar(request, vehiculo, cliente);
        log.info("Vehiculo actualizado: id={}, placa={}", vehiculo.getId(), vehiculo.getPlaca());
        return mapper.toResponse(vehiculo);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Vehiculo vehiculo = buscar(id);
        // Un vehiculo con ordenes registradas no se puede eliminar
        if (ordenRepository.existsByVehiculoId(id)) {
            throw new ReglaNegocioException("El vehículo " + vehiculo.getPlaca()
                    + " tiene órdenes de servicio registradas y no se puede eliminar");
        }
        vehiculoRepository.delete(vehiculo);
        log.info("Vehiculo eliminado: id={}, placa={}", id, vehiculo.getPlaca());
    }

    private Vehiculo buscar(Long id) {
        return vehiculoRepository.findConClienteById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el vehículo con id " + id));
    }

    private Cliente buscarCliente(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el cliente con id " + clienteId));
    }
}
