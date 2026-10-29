package pe.edu.upeu.autoandes.service.impl;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.autoandes.dto.AnularOrdenRequestDTO;
import pe.edu.upeu.autoandes.dto.DetalleRequestDTO;
import pe.edu.upeu.autoandes.dto.OrdenRequestDTO;
import pe.edu.upeu.autoandes.dto.OrdenResponseDTO;
import pe.edu.upeu.autoandes.dto.OrdenResumenDTO;
import pe.edu.upeu.autoandes.entity.DetalleOrden;
import pe.edu.upeu.autoandes.entity.EstadoOrden;
import pe.edu.upeu.autoandes.entity.OrdenServicio;
import pe.edu.upeu.autoandes.entity.Servicio;
import pe.edu.upeu.autoandes.entity.Vehiculo;
import pe.edu.upeu.autoandes.exception.RecursoNoEncontradoException;
import pe.edu.upeu.autoandes.exception.ReglaNegocioException;
import pe.edu.upeu.autoandes.exception.SolicitudInvalidaException;
import pe.edu.upeu.autoandes.mapper.OrdenMapper;
import pe.edu.upeu.autoandes.repository.OrdenServicioRepository;
import pe.edu.upeu.autoandes.repository.ServicioRepository;
import pe.edu.upeu.autoandes.repository.VehiculoRepository;
import pe.edu.upeu.autoandes.service.OrdenService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdenServiceImpl implements OrdenService {

    private final OrdenServicioRepository ordenRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ServicioRepository servicioRepository;
    private final OrdenMapper mapper;

    /**
     * Registro atomico de cabecera y detalles. Todo ocurre dentro de una sola transaccion:
     * si cualquier detalle o regla falla, la excepcion revierte la operacion completa y la base
     * no recibe ni la cabecera ni ningun detalle. Los precios y el total se calculan aqui,
     * nunca se aceptan del cliente.
     */
    @Override
    @Transactional
    public OrdenResponseDTO registrar(OrdenRequestDTO request, String usuario) {
        validarServiciosNoRepetidos(request.detalles());

        Vehiculo vehiculo = vehiculoRepository.findConClienteById(request.vehiculoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el vehículo con id " + request.vehiculoId()));

        OrdenServicio orden = new OrdenServicio();
        BigDecimal total = BigDecimal.ZERO;
        int linea = 0;
        for (DetalleRequestDTO item : request.detalles()) {
            linea++;
            Servicio servicio = servicioRepository.findById(item.servicioId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe el servicio con id " + item.servicioId()));
            // RN-03: todos los servicios de la orden deben estar activos
            if (!servicio.getActivo()) {
                throw new ReglaNegocioException("Detalle " + linea + ": el servicio " + servicio.getCodigo()
                        + " (" + servicio.getNombre() + ") está inactivo y no se puede incluir en una orden");
            }
            DetalleOrden detalle = new DetalleOrden();
            detalle.setServicio(servicio);
            detalle.setCantidad(item.cantidad());
            detalle.setPrecioUnitario(servicio.getPrecio());
            detalle.setSubtotal(servicio.getPrecio().multiply(BigDecimal.valueOf(item.cantidad())));
            orden.agregarDetalle(detalle);
            total = total.add(detalle.getSubtotal());
        }

        // RN-04: el kilometraje no puede ser menor que el de la ultima orden REGISTRADA del vehiculo
        ordenRepository.findFirstByVehiculoIdAndEstadoOrderByFechaDescIdDesc(vehiculo.getId(), EstadoOrden.REGISTRADA)
                .filter(ultima -> request.kilometraje() < ultima.getKilometraje())
                .ifPresent(ultima -> {
                    throw new ReglaNegocioException("El kilometraje " + request.kilometraje()
                            + " es menor que el de la última orden registrada del vehículo " + vehiculo.getPlaca()
                            + " (" + ultima.getKilometraje() + " km)");
                });

        orden.setNumero("OS-%06d".formatted(ordenRepository.siguienteNumero()));
        orden.setFecha(LocalDateTime.now());
        orden.setVehiculo(vehiculo);
        orden.setKilometraje(request.kilometraje());
        orden.setEstado(EstadoOrden.REGISTRADA);
        orden.setTotal(total);
        orden.setUsuarioRegistro(usuario);

        OrdenServicio guardada = ordenRepository.save(orden);
        log.info("Orden registrada: numero={}, vehiculo={}, detalles={}, total={}, usuario={}",
                guardada.getNumero(), vehiculo.getPlaca(), guardada.getDetalles().size(), total, usuario);
        return mapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public OrdenResponseDTO anular(Long id, AnularOrdenRequestDTO request) {
        OrdenServicio orden = ordenRepository.findConDetalleById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la orden con id " + id));

        String motivo = request.motivo().trim();
        if (motivo.length() < 10 || motivo.length() > 200) {
            throw new SolicitudInvalidaException("El motivo debe tener entre 10 y 200 caracteres");
        }
        // RN-05: solo se anula una orden REGISTRADA y en el mismo dia de su registro
        if (orden.getEstado() != EstadoOrden.REGISTRADA) {
            throw new ReglaNegocioException("La orden " + orden.getNumero() + " ya está " + orden.getEstado());
        }
        if (!orden.getFecha().toLocalDate().equals(LocalDate.now())) {
            throw new ReglaNegocioException("La orden " + orden.getNumero()
                    + " solo se puede anular el mismo día de su registro (" + orden.getFecha().toLocalDate() + ")");
        }
        orden.setEstado(EstadoOrden.ANULADA);
        orden.setMotivoAnulacion(motivo);
        log.info("Orden anulada: numero={}, motivo={}", orden.getNumero(), motivo);
        return mapper.toResponse(orden);
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenResponseDTO obtener(Long id) {
        return ordenRepository.findConDetalleById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la orden con id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenResumenDTO> listar(String placa, EstadoOrden estado, LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new SolicitudInvalidaException("La fecha 'desde' no puede ser posterior a la fecha 'hasta'");
        }
        Specification<OrdenServicio> filtros = (root, query, cb) -> {
            List<Predicate> predicados = new ArrayList<>();
            if (placa != null && !placa.isBlank()) {
                predicados.add(cb.like(cb.upper(root.get("vehiculo").get("placa")),
                        "%" + placa.trim().toUpperCase() + "%"));
            }
            if (estado != null) {
                predicados.add(cb.equal(root.get("estado"), estado));
            }
            if (desde != null) {
                predicados.add(cb.greaterThanOrEqualTo(root.get("fecha"), desde.atStartOfDay()));
            }
            if (hasta != null) {
                // "hasta" incluye todo ese dia
                predicados.add(cb.lessThan(root.get("fecha"), hasta.plusDays(1).atStartOfDay()));
            }
            return cb.and(predicados.toArray(new Predicate[0]));
        };
        return ordenRepository.findAll(filtros, Sort.by(Sort.Direction.DESC, "fecha", "id"))
                .stream().map(mapper::toResumen).toList();
    }

    private void validarServiciosNoRepetidos(List<DetalleRequestDTO> detalles) {
        Set<Long> vistos = new HashSet<>();
        for (DetalleRequestDTO d : detalles) {
            if (!vistos.add(d.servicioId())) {
                throw new SolicitudInvalidaException(
                        "El servicio con id " + d.servicioId() + " está repetido en la orden");
            }
        }
    }
}
