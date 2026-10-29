package pe.edu.upeu.autoandes.security;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static pe.edu.upeu.autoandes.security.Permiso.*;

/** Matriz de roles y permisos (seccion 3.2), centralizada en un solo lugar. */
public final class PermisosPorRol {

    private static final Map<String, Set<Permiso>> MATRIZ = Map.of(
            "ADMIN", EnumSet.allOf(Permiso.class),
            "ASESOR", EnumSet.of(SERVICIO_LEER, VEHICULO_LEER, VEHICULO_ESCRIBIR, ORDEN_LEER, ORDEN_REGISTRAR),
            "JEFE_TALLER", EnumSet.of(SERVICIO_LEER, SERVICIO_ESCRIBIR, VEHICULO_LEER, ORDEN_LEER, ORDEN_ANULAR,
                    REPORTE_VER));

    private PermisosPorRol() {
    }

    /** Un usuario con varios roles recibe la union de los permisos de todos ellos. */
    public static List<String> permisosDe(Collection<String> roles) {
        Set<Permiso> union = EnumSet.noneOf(Permiso.class);
        for (String rol : roles) {
            union.addAll(MATRIZ.getOrDefault(rol, Set.of()));
        }
        return union.stream().map(Permiso::name).toList();
    }
}
