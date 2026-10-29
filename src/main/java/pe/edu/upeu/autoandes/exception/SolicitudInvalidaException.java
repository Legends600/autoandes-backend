package pe.edu.upeu.autoandes.exception;

/** Solicitud que Bean Validation no puede detectar (por ejemplo, detalles repetidos): responde 400. */
public class SolicitudInvalidaException extends RuntimeException {
    public SolicitudInvalidaException(String mensaje) {
        super(mensaje);
    }
}
