package moduloinventario;

public class Producto {
    private int codigo; 
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
    private int categoriaId; 
    private int stockMinimo;  
    private boolean estado;  

    public Producto(int codigo, String nombre, String descripcion, double precio, 
                   int categoriaId, int stock, int stockMinimo, boolean estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoriaId = categoriaId;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.estado = estado;
    }
    // Getters y Setters
    public int getCodigo() {
        return codigo;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public double getPrecio() {
        return precio;
    }
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }
    public int getCategoriaId() {
        return categoriaId;
    }
    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }
    public int getStockMinimo() {
        return stockMinimo;
    }
    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }
    public boolean isEstado() {
        return estado;
    }
    public void setEstado(boolean estado) {
        this.estado = estado;
    }
    @Override
    public String toString() {
        return "Producto{" +
                "codigo=" + codigo +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precio=" + precio +
                ", categoriaId=" + categoriaId +
                ", stock=" + stock +
                ", stockMinimo=" + stockMinimo +
                ", estado=" + estado +
                '}';
    }
}