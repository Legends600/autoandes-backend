package pe.edu.upeu.autoandes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.autoandes.dto.IngresoPorServicioDTO;
import pe.edu.upeu.autoandes.entity.DetalleOrden;

import java.time.LocalDateTime;
import java.util.List;

public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {

    boolean existsByServicioId(Long servicioId);

    // Reporte resuelto en la base de datos: GROUP BY con proyeccion, sin findAll() ni bucles en Java.
    // Las ordenes ANULADAS quedan fuera y el rango de fechas es [desde, hasta).
    @Query("""
            select new pe.edu.upeu.autoandes.dto.IngresoPorServicioDTO(
                s.codigo, s.nombre, sum(d.cantidad), sum(d.subtotal))
            from DetalleOrden d
            join d.servicio s
            join d.orden o
            where o.estado <> pe.edu.upeu.autoandes.entity.EstadoOrden.ANULADA
              and o.fecha >= :desde
              and o.fecha < :hasta
            group by s.codigo, s.nombre
            order by sum(d.subtotal) desc
            """)
    List<IngresoPorServicioDTO> ingresosPorServicio(@Param("desde") LocalDateTime desde,
                                                   @Param("hasta") LocalDateTime hasta);
}
