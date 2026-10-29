package pe.edu.upeu.autoandes.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.autoandes.dto.IngresoPorServicioDTO;
import pe.edu.upeu.autoandes.exception.SolicitudInvalidaException;
import pe.edu.upeu.autoandes.repository.DetalleOrdenRepository;
import pe.edu.upeu.autoandes.service.ReporteService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    // Limites amplios para cuando el usuario no indica una de las fechas del rango
    private static final LocalDate FECHA_MINIMA = LocalDate.of(1900, 1, 1);
    private static final LocalDate FECHA_MAXIMA = LocalDate.of(2999, 12, 31);

    private final DetalleOrdenRepository detalleOrdenRepository;

    @Override
    @Transactional(readOnly = true)
    public List<IngresoPorServicioDTO> ingresosPorServicio(LocalDate desde, LocalDate hasta) {
        LocalDate inicio = desde != null ? desde : FECHA_MINIMA;
        LocalDate fin = hasta != null ? hasta : FECHA_MAXIMA;
        if (inicio.isAfter(fin)) {
            throw new SolicitudInvalidaException("La fecha 'desde' no puede ser posterior a la fecha 'hasta'");
        }
        // La agregacion (GROUP BY) la hace la base de datos, no un bucle en Java
        return detalleOrdenRepository.ingresosPorServicio(inicio.atStartOfDay(), fin.plusDays(1).atStartOfDay());
    }
}
