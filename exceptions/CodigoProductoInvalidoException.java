package exceptions;

public class CodigoProductoInvalidoException extends IllegalArgumentException {
    public CodigoProductoInvalidoException() {
        super("El código del producto debe ser un entero positivo.");
    }
}