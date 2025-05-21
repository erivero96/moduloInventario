package exceptions;

public class MotivoInvalidoException extends IllegalArgumentException {
    public MotivoInvalidoException() {
        super("El motivo no puede ser vacío.");
    }
}
