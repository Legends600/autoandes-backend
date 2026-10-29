package pe.edu.upeu.autoandes.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.autoandes.dto.ServicioRequestDTO;
import pe.edu.upeu.autoandes.dto.ServicioResponseDTO;
import pe.edu.upeu.autoandes.entity.Servicio;

@Component
public class ServicioMapper {

    public Servicio toEntity(ServicioRequestDTO dto) {
        Servicio servicio = new Servicio();
        copiar(dto, servicio);
        servicio.setActivo(true);
        return servicio;
    }

    public void copiar(ServicioRequestDTO dto, Servicio servicio) {
        servicio.setCodigo(dto.codigo().trim());
        servicio.setNombre(dto.nombre().trim());
        servicio.setPrecio(dto.precio());
        servicio.setDuracionMin(dto.duracionMin());
    }

    public ServicioResponseDTO toResponse(Servicio s) {
        return new ServicioResponseDTO(s.getId(), s.getCodigo(), s.getNombre(), s.getPrecio(),
                s.getDuracionMin(), s.getActivo());
    }
}
