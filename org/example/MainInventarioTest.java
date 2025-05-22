package org.example;

import java.util.List;

public class MainInventarioTest {
    public static void main(String[] args) {
        InventarioDao dao = new InventarioDao();

        try {
            //Login
            LoginService loginService = new LoginService(dao.getConnection());
            String correo = "ana.perez@ucsm.edu.pe";
            String contraseña = "clave123";
            System.out.println(loginService.login(correo, contraseña));
            
            // Crear un nuevo producto
            Producto nuevoProducto = new Producto(
                1,                          // Código (usar número corto <= 4 dígitos)
                "Laptop A111AA1111",              // Nombre
                "Laptop para oficina",        // Descripción
                2500.00,                      // Precio
                20,                           // Stock
                "Electrónica",                       // ID categoría (verifica que exista)
                5,                            // Stock mínimo
                true                          // Estado
            );

            // Registrar producto
            dao.registerProduct(nuevoProducto);

            // Consultar productos por nombre parcial
            List<Producto> productos = dao.consultarProductos(null, "Laptop", null, true, "nombre", true);
            for (Producto p : productos) {
                System.out.println("Producto encontrado: " + p.getNombre() + " - Estado: " + p.isEstado());
            }

            // Cambiar estado del producto
            dao.cambiarEstadoProducto(1, false);

            // Verificar nuevo estado
            boolean estadoNuevo = dao.obtenerEstadoProducto(1);
            System.out.println("Nuevo estado del producto 11: " + estadoNuevo);

            // Actualizar stock
            dao.actualizarStock(nuevoProducto, 5, "Reabastecimiento", "20220001", "M1224");

            // Listar movimientos por producto
            List<MovimientoStock> movimientos = dao.listarMovimientosPorProducto(nuevoProducto);
            for (MovimientoStock m : movimientos) {
                System.out.println("Movimiento: " + m.getId() + " - Cantidad: " + m.getCantidad() + " - Motivo: " + m.getMotivo());
            }

            // Eliminar producto
            dao.deleteProduct("Laptop Lenovo");

        } catch (Exception e) {
            System.err.println("Error durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
