package org.example;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        InventarioDao inventarioDao = new InventarioDao();
        inventarioDao.cambiarEstadoProducto(21 , false);
        System.out.println(inventarioDao.obtenerEstadoProducto(12));


    }
}
