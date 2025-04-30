package moduloinventario;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.CallableStatement;

public class registrarProducto {

    private static final String URL = "jdbc:postgresql://localhost:5433/inventario";
    private static final String USER = "postgres";
    private static final String PASSWORD = "nuevaclave123"; // Cambia esto
    public static boolean registrarNuevoProducto(String codigo, String nombre, String descripcion, double precio, Integer categoria, Integer stockInicial) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "CALL registrar_producto(?, ?, ?, ?, ?, ?)";
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.setString(1, codigo);
            stmt.setString(2, nombre);
            stmt.setString(3, descripcion);
            stmt.setDouble(4, precio);
            stmt.setInt(5, categoria);
            stmt.setInt(6, stockInicial);
            stmt.execute();
            System.out.println("Producto registrado exitosamente en la base de datos.");
            return true;
        } catch (Exception e) {
            System.out.println("Error al registrar el producto: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        String codigo = "P001";
        String nombre = "Laptop";
        String descripcion = "Laptop de alta gama";
        double precio = 1500.00;
        Integer categoria = 1;
        Integer stockInicial = 10;

        boolean resultado = registrarNuevoProducto(codigo, nombre, descripcion, precio, categoria, stockInicial);
        if (resultado) {
            System.out.println("El producto se registró correctamente.");
        } else {
            System.out.println("Hubo un error al registrar el producto.");
        }
    }
}
