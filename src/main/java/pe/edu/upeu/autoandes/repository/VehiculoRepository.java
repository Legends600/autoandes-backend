package pe.edu.upeu.autoandes.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.autoandes.entity.Vehiculo;

import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    boolean existsByPlaca(String placa);

    boolean existsByPlacaAndIdNot(String placa, Long id);

    // El grafo trae el cliente en la misma consulta y evita una consulta por cada vehiculo
    @EntityGraph(attributePaths = "cliente")
    List<Vehiculo> findAllByOrderByPlacaAsc();

    @EntityGraph(attributePaths = "cliente")
    List<Vehiculo> findByClienteIdOrderByPlacaAsc(Long clienteId);

    @EntityGraph(attributePaths = "cliente")
    Optional<Vehiculo> findConClienteById(Long id);
}
