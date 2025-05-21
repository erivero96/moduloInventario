package exceptions;

public class UsuarioIdInvalidoException extends IllegalArgumentException {
    private String usuarioId;

    public UsuarioIdInvalidoException() {
        super("El ID del usuario debe tener exactamente 8 caracteres.");
    }

    public UsuarioIdInvalidoException(String usuarioId) {
        super("El ID del usuario \"" + usuarioId + "\" es inválido. Debe tener exactamente 8 caracteres.");
        this.usuarioId = usuarioId;
    }

    public UsuarioIdInvalidoException(String usuarioId, Throwable cause) {
        super("El ID del usuario \"" + usuarioId + "\" es inválido.", cause);
        this.usuarioId = usuarioId;
    }

    public String getUsuarioId() {
        return usuarioId;
    }
}
