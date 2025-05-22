package exceptions;

public class MotivoInvalidoException extends IllegalArgumentException {
    private String motivo;

    public MotivoInvalidoException() {
        super("El motivo no puede ser vacío.");
    }

    public MotivoInvalidoException(String motivo) {
        super("El motivo \"" + motivo + "\" es inválido. No puede estar vacío o en blanco.");
        this.motivo = motivo;
    }

    public MotivoInvalidoException(String motivo, Throwable cause) {
        super("El motivo \"" + motivo + "\" es inválido.", cause);
        this.motivo = motivo;
    }

    public String getMotivo() {
        return motivo;
    }
}
