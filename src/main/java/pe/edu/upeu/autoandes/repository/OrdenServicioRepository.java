package pe.edu.upeu.autoandes.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.autoandes.entity.EstadoOrden;
import pe.edu.upeu.autoandes.entity.OrdenServicio;

import java.util.List;
import java.util.Optional;

public interface OrdenServicioRepository
        extends JpaRepository<OrdenServicio, Long>, JpaSpecificationExecutor<OrdenServicio> {

    boolean existsByVehiculoId(Long vehiculoId);

    // El grafo trae vehiculo y cliente junto con las ordenes para armar la lista sin consultas extra
    @Override
    @EntityGraph(attributePaths = {"vehiculo", "vehiculo.cliente"})
    List<OrdenServicio> findAll(Specification<OrdenServicio> spec, Sort sort);

    // Ultima orden en el estado dado (para RN-04: el kilometraje no puede retroceder)
    Optional<OrdenServicio> findFirstByVehiculoIdAndEstadoOrderByFechaDescIdDesc(Long vehiculoId, EstadoOrden estado);

    @Query("""
            select distinct o from OrdenServicio o
            join fetch o.vehiculo v
            join fetch v.cliente
            left join fetch o.detalles d
            left join fetch d.servicio
            where o.id = :id
            """)
    Optional<OrdenServicio> findConDetalleById(@Param("id") Long id);

    // El numero correlativo OS-000001 sale de una secuencia de Oracle (migracion V2)
    @Query(value = "SELECT seq_orden_numero.NEXTVAL FROM dual", nativeQuery = true)
    Long siguienteNumero();
}
