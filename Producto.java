package moduloinventario;

public class Producto {
    public String codigo;
    public String nombre;
    public String descripcion;
    public double precio;
    public int categoria;
    public int stock;

    public Producto(String codigo, String nombre, String descripcion, double precio, int categoria, int stock) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precio=" + precio +
                ", categoria='" + categoria + '\'' +
                ", stock=" + stock +
                '}';
    }

}
