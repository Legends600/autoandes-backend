package pe.edu.upeu.autoandes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.autoandes.dto.ServicioEstadoRequestDTO;
import pe.edu.upeu.autoandes.dto.ServicioRequestDTO;
import pe.edu.upeu.autoandes.dto.ServicioResponseDTO;
import pe.edu.upeu.autoandes.service.ServicioService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/servicios")
@RequiredArgsConstructor
@Tag(name = "Servicios", description = "Catálogo de servicios del taller")
public class ServicioController {

    private final ServicioService servicioService;

    @GetMapping
    @Operation(summary = "Lista los servicios")
    public List<ServicioResponseDTO> listar() {
        return servicioService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un servicio por id")
    public ServicioResponseDTO obtener(@PathVariable Long id) {
        return servicioService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra un servicio (RN-01: código único)")
    public ServicioResponseDTO crear(@Valid @RequestBody ServicioRequestDTO request) {
        return servicioService.crear(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edita un servicio")
    public ServicioResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody ServicioRequestDTO request) {
        return servicioService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Da de baja o reactiva un servicio")
    public ServicioResponseDTO cambiarEstado(@PathVariable Long id,
                                             @Valid @RequestBody ServicioEstadoRequestDTO request) {
        return servicioService.cambiarEstado(id, request.activo());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Elimina un servicio (RN-02: 409 si ya se usó en una orden)")
    public void eliminar(@PathVariable Long id) {
        servicioService.eliminar(id);
    }
}
