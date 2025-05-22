package org.example;

public class Producto {
    private int codigo;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
    private int categoria_id;
    private String categoria;
    private int stock_minimo;
    private boolean estado;

    public Producto(String nombre, String descripcion, double precio, int stock, int categoria_id, int stock_minimo, boolean estado) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria_id = categoria_id;
        this.stock_minimo = stock_minimo;
        this.estado = estado;
    }

    public Producto(int codigo, String nombre, String descripcion, double precio, int stock, int categoria_id, int stock_minimo, boolean estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria_id = categoria_id;
        this.stock_minimo = stock_minimo;
        this.estado = estado;
    }

    public Producto(int codigo, String nombre, String descripcion, double precio, int stock, String categoria, int stock_minimo, boolean estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
        this.stock_minimo = stock_minimo;
        this.estado = estado;
    }

    public Producto() {};




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

    public int getCategoria_id() {
        return categoria_id;
    }
    public void setCategoria_id(int categoria_id) {
        this.categoria_id = categoria_id;
    }
    public int getStock_minimo() {
        return stock_minimo;
    }
    public void setStock_minimo(int stock_minimo) {
        this.stock_minimo = stock_minimo;
    }
    public boolean isEstado() {
        return estado;
    }
    public void setEstado(boolean estado) {
        this.estado = estado;
    }
    public int getCodigo() {
        return codigo;
    }
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

}


