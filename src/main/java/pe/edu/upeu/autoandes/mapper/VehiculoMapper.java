package pe.edu.upeu.autoandes.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.autoandes.dto.VehiculoRequestDTO;
import pe.edu.upeu.autoandes.dto.VehiculoResponseDTO;
import pe.edu.upeu.autoandes.entity.Cliente;
import pe.edu.upeu.autoandes.entity.Vehiculo;

@Component
public class VehiculoMapper {

    public void copiar(VehiculoRequestDTO dto, Vehiculo vehiculo, Cliente cliente) {
        vehiculo.setPlaca(dto.placa().trim());
        vehiculo.setMarca(dto.marca().trim());
        vehiculo.setModelo(dto.modelo().trim());
        vehiculo.setAnio(dto.anio());
        vehiculo.setCliente(cliente);
    }

    public VehiculoResponseDTO toResponse(Vehiculo v) {
        return new VehiculoResponseDTO(v.getId(), v.getPlaca(), v.getMarca(), v.getModelo(), v.getAnio(),
                v.getCliente().getId(), v.getCliente().getNombres());
    }
}
