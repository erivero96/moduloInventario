package exceptions;

public class MovimientoIdInvalidoException extends IllegalArgumentException {
    public MovimientoIdInvalidoException() {
        super("El ID del movimiento debe tener exactamente 5 caracteres.");
    }
}
