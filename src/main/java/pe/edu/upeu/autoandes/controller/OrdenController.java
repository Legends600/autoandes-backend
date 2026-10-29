package pe.edu.upeu.autoandes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.autoandes.dto.AnularOrdenRequestDTO;
import pe.edu.upeu.autoandes.dto.OrdenRequestDTO;
import pe.edu.upeu.autoandes.dto.OrdenResponseDTO;
import pe.edu.upeu.autoandes.dto.OrdenResumenDTO;
import pe.edu.upeu.autoandes.entity.EstadoOrden;
import pe.edu.upeu.autoandes.service.OrdenService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ordenes")
@RequiredArgsConstructor
@Tag(name = "Órdenes de servicio", description = "Registro, consulta y anulación de órdenes")
public class OrdenController {

    private final OrdenService ordenService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra una orden con su detalle en una sola transacción")
    public OrdenResponseDTO registrar(@Valid @RequestBody OrdenRequestDTO request, Authentication authentication) {
        return ordenService.registrar(request, authentication.getName());
    }

    @GetMapping
    @Operation(summary = "Consulta órdenes con filtros opcionales y combinables, de la más reciente a la más antigua")
    public List<OrdenResumenDTO> listar(
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) EstadoOrden estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ordenService.listar(placa, estado, desde, hasta);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una orden con su detalle")
    public OrdenResponseDTO obtener(@PathVariable Long id) {
        return ordenService.obtener(id);
    }

    @PatchMapping("/{id}/anular")
    @Operation(summary = "Anula una orden REGISTRADA del mismo día (RN-05)")
    public OrdenResponseDTO anular(@PathVariable Long id, @Valid @RequestBody AnularOrdenRequestDTO request) {
        return ordenService.anular(id, request);
    }
}
