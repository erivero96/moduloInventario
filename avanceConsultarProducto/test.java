package moduloinventario;

import java.util.List;

public class test {

    public static void main(String[] args) {
        try {
            // 1. Consultar todos los productos activos
            System.out.println("=== Todos los productos activos ===");
            List<Producto> productos = ProductoDAO.consultarProductos(null, null, null, true, null, true);
            productos.forEach(System.out::println);

            // 2. Consultar productos electrónicos (categoría 1)
            System.out.println("\n=== Productos electrónicos ===");
            productos = ProductoDAO.consultarProductos(null, null, 1, true, null, true);
            productos.forEach(System.out::println);

            // 3. Buscar productos con "lap" en el nombre
            System.out.println("\n=== Productos con 'lap' en el nombre ===");
            productos = ProductoDAO.consultarProductos(null, "lap", null, true, null, true);
            productos.forEach(System.out::println);

            // 4. Buscar por código exacto
            System.out.println("\n=== Producto con código 1005 ===");
            productos = ProductoDAO.consultarProductos("1005", null, null, true, null, true);
            productos.forEach(System.out::println);

            // 5. Productos ordenados por precio descendente
            System.out.println("\n=== Productos ordenados por precio descendente ===");
            productos = ProductoDAO.consultarProductos(null, null, null, true, "precio", false);
            productos.forEach(System.out::println);

            // 6. Prueba de validación - código inválido
            System.out.println("\n=== Prueba de validación - código inválido ===");
            try {
                productos = ProductoDAO.consultarProductos("abc123", null, null, true, null, true);
            } catch (IllegalArgumentException e) {
                System.out.println("Error esperado: " + e.getMessage());
            }

            // 7. Prueba de validación - orden inválido
            System.out.println("\n=== Prueba de validación - orden inválido ===");
            try {
                productos = ProductoDAO.consultarProductos(null, null, null, true, "fecha", true);
            } catch (IllegalArgumentException e) {
                System.out.println("Error esperado: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Error en las pruebas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}