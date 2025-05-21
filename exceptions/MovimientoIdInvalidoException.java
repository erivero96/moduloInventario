package exceptions;

public class MovimientoIdInvalidoException extends IllegalArgumentException {
    private String movId;

    public MovimientoIdInvalidoException() {
        super("El ID del movimiento debe tener exactamente 5 caracteres.");
    }

    public MovimientoIdInvalidoException(String movId) {
        super("El ID del movimiento \"" + movId + "\" es inválido. Debe tener exactamente 5 caracteres.");
        this.movId = movId;
    }

    public MovimientoIdInvalidoException(String movId, Throwable cause) {
        super("El ID del movimiento \"" + movId + "\" es inválido.", cause);
        this.movId = movId;
    }

    public String getMovId() {
        return movId;
    }
}
