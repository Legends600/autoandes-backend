package pe.edu.upeu.autoandes.exception;

/** Recurso inexistente: responde 404. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
