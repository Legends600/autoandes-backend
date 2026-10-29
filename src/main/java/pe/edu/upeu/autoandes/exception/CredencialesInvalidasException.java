package pe.edu.upeu.autoandes.exception;

/**
 * Login rechazado. El mismo mensaje cubre usuario inexistente, clave incorrecta y cuenta inactiva,
 * para no revelar si el usuario existe. Responde 401.
 */
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException() {
        super("Usuario o contraseña incorrectos");
    }
}
