package pe.edu.upeu.autoandes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.autoandes.dto.IngresoPorServicioDTO;
import pe.edu.upeu.autoandes.service.ReporteService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Reportes de ingresos")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/ingresos-por-servicio")
    @PreAuthorize("hasAuthority('REPORTE_VER')")
    @Operation(summary = "Ingresos por servicio, sin contar las órdenes anuladas, de mayor a menor importe")
    public List<IngresoPorServicioDTO> ingresosPorServicio(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.ingresosPorServicio(desde, hasta);
    }
}
