package pe.edu.upeu.autoandes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.autoandes.dto.VehiculoRequestDTO;
import pe.edu.upeu.autoandes.dto.VehiculoResponseDTO;
import pe.edu.upeu.autoandes.service.VehiculoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehiculos")
@RequiredArgsConstructor
@Tag(name = "Vehículos", description = "Vehículos de los clientes")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    @GetMapping
    @Operation(summary = "Lista los vehículos, opcionalmente de un cliente")
    public List<VehiculoResponseDTO> listar(@RequestParam(required = false) Long clienteId) {
        return vehiculoService.listar(clienteId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un vehículo por id")
    public VehiculoResponseDTO obtener(@PathVariable Long id) {
        return vehiculoService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra un vehículo (placa única, cliente existente)")
    public VehiculoResponseDTO crear(@Valid @RequestBody VehiculoRequestDTO request) {
        return vehiculoService.crear(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edita un vehículo")
    public VehiculoResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody VehiculoRequestDTO request) {
        return vehiculoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Elimina un vehículo (409 si tiene órdenes registradas)")
    public void eliminar(@PathVariable Long id) {
        vehiculoService.eliminar(id);
    }
}
