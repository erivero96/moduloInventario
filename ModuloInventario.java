
package moduloinventario;

/**
 *
 * @author river
 */

public class ModuloInventario {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Aquí puedes probar la funcionalidad del módulo de inventario
        // Por ejemplo, registrar un nuevo producto 
        registrarProducto.registrarNuevoProducto("P001", "Laptop", "Laptop de 15 pulgadas", 70000.00, "Electrónica", 10);
        registrarProducto.registrarNuevoProducto("P002", "Mouse", "Mouse inalámbrico", 25.00, "Accesorios", 50);
        registrarProducto.registrarNuevoProducto("P003", "Teclado", "Teclado mecánico", 75.00, "Accesorios", 30);
    }
}
