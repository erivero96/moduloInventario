package exceptions;

public class CodigoProductoInvalidoException extends IllegalArgumentException {
    private String codigo;

    public CodigoProductoInvalidoException() {
        super("El código del producto debe ser un entero positivo.");
    }

    public CodigoProductoInvalidoException(String codigo) {
        super("El código de producto \"" + codigo + "\" es inválido. Debe ser un entero positivo.");
        this.codigo = codigo;
    }

    public CodigoProductoInvalidoException(String codigo, Throwable cause) {
        super("El código de producto \"" + codigo + "\" es inválido.", cause);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
