package pe.edu.upeu.autoandes.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.autoandes.dto.DetalleResponseDTO;
import pe.edu.upeu.autoandes.dto.OrdenResponseDTO;
import pe.edu.upeu.autoandes.dto.OrdenResumenDTO;
import pe.edu.upeu.autoandes.entity.DetalleOrden;
import pe.edu.upeu.autoandes.entity.OrdenServicio;

@Component
public class OrdenMapper {

    public OrdenResponseDTO toResponse(OrdenServicio o) {
        return new OrdenResponseDTO(o.getId(), o.getNumero(), o.getFecha(),
                o.getVehiculo().getId(), o.getVehiculo().getPlaca(), o.getVehiculo().getCliente().getNombres(),
                o.getKilometraje(), o.getEstado().name(), o.getTotal(), o.getUsuarioRegistro(),
                o.getMotivoAnulacion(), o.getDetalles().stream().map(this::toDetalle).toList());
    }

    public OrdenResumenDTO toResumen(OrdenServicio o) {
        return new OrdenResumenDTO(o.getId(), o.getNumero(), o.getFecha(),
                o.getVehiculo().getId(), o.getVehiculo().getPlaca(), o.getVehiculo().getCliente().getNombres(),
                o.getKilometraje(), o.getEstado().name(), o.getTotal(), o.getUsuarioRegistro());
    }

    private DetalleResponseDTO toDetalle(DetalleOrden d) {
        return new DetalleResponseDTO(d.getId(), d.getServicio().getId(), d.getServicio().getCodigo(),
                d.getServicio().getNombre(), d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal());
    }
}
