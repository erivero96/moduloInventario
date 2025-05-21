package exceptions;

public class UsuarioIdInvalidoException extends IllegalArgumentException {
    public UsuarioIdInvalidoException() {
        super("El ID del usuario debe tener exactamente 8 caracteres.");
    }
}
