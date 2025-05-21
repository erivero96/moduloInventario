package exceptions;

public class CantidadInvalidaException extends IllegalArgumentException {
    private int cantidad;

    public CantidadInvalidaException() {
        super("La cantidad no puede ser cero.");
    }

    public CantidadInvalidaException(int cantidad) {
        super("La cantidad especificada (" + cantidad + ") es inválida. No puede ser cero.");
        this.cantidad = cantidad;
    }

    public CantidadInvalidaException(int cantidad, Throwable cause) {
        super("La cantidad especificada (" + cantidad + ") es inválida.", cause);
        this.cantidad = cantidad;
    }

    public int getCantidad() {
        return cantidad;
    }
}
