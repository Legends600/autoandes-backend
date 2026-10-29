package pe.edu.upeu.autoandes.exception;

/** Regla de negocio rechazada (RN-01 a RN-05): responde 409. */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
