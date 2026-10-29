package pe.edu.upeu.autoandes.security;

/** Permisos de la matriz de la seccion 3.2. Toda autorizacion se hace contra un permiso, nunca contra un rol. */
public enum Permiso {
    SERVICIO_LEER,
    SERVICIO_ESCRIBIR,
    VEHICULO_LEER,
    VEHICULO_ESCRIBIR,
    ORDEN_LEER,
    ORDEN_REGISTRAR,
    ORDEN_ANULAR,
    REPORTE_VER
}
