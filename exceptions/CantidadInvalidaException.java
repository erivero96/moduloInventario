package exceptions;

public class CantidadInvalidaException extends IllegalArgumentException {
    public CantidadInvalidaException() {
        super("La cantidad no puede ser cero.");
    }
}
