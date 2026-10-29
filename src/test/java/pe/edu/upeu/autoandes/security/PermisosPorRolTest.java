package pe.edu.upeu.autoandes.security;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PermisosPorRolTest {

    @Test
    void adminTieneTodosLosPermisos() {
        assertEquals(Permiso.values().length, PermisosPorRol.permisosDe(List.of("ADMIN")).size());
    }

    @Test
    void asesorRegistraOrdenesPeroNoAnulaNiVeReportes() {
        List<String> permisos = PermisosPorRol.permisosDe(List.of("ASESOR"));

        assertTrue(permisos.contains("ORDEN_REGISTRAR"));
        assertTrue(permisos.contains("VEHICULO_ESCRIBIR"));
        assertFalse(permisos.contains("ORDEN_ANULAR"));
        assertFalse(permisos.contains("REPORTE_VER"));
        assertFalse(permisos.contains("SERVICIO_ESCRIBIR"));
    }

    @Test
    void jefeDeTallerAnulaYVeReportesPeroNoRegistraOrdenes() {
        List<String> permisos = PermisosPorRol.permisosDe(List.of("JEFE_TALLER"));

        assertTrue(permisos.contains("ORDEN_ANULAR"));
        assertTrue(permisos.contains("REPORTE_VER"));
        assertTrue(permisos.contains("SERVICIO_ESCRIBIR"));
        assertFalse(permisos.contains("ORDEN_REGISTRAR"));
        assertFalse(permisos.contains("VEHICULO_ESCRIBIR"));
    }

    @Test
    void variosRolesRecibenLaUnionDeSusPermisos() {
        List<String> permisos = PermisosPorRol.permisosDe(List.of("ASESOR", "JEFE_TALLER"));

        assertTrue(permisos.contains("ORDEN_REGISTRAR"));
        assertTrue(permisos.contains("ORDEN_ANULAR"));
        assertEquals(Permiso.values().length, permisos.size());
    }

    @Test
    void unRolDesconocidoNoTienePermisos() {
        assertTrue(PermisosPorRol.permisosDe(List.of("INVENTADO")).isEmpty());
    }
}
