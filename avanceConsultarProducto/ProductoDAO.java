package moduloinventario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class ProductoDAO {
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("db.properties"));
            URL = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");
        } catch (IOException e) {
            System.err.println("Error al cargar configuración de la base de datos: " + e.getMessage());
            URL = "jdbc:postgresql://localhost:5432/inventario";
            USER = "postgres";
            PASSWORD = "nuevaclave123";
        }
    }

    public static List<Producto> consultarProductos(String codigo, String nombre, Integer categoriaId,
                                                    boolean soloActivos, String orden, boolean ascendente) {
        List<Producto> productos = new ArrayList<>();

        // Validar parámetros
        if (codigo != null && !codigo.matches("\\d+")) {
            throw new IllegalArgumentException("El código debe ser un número válido");
        }

        if (orden != null && !orden.matches("codigo|nombre|precio|stock")) {
            throw new IllegalArgumentException("Campo de orden inválido. Use: codigo, nombre, precio o stock");
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             CallableStatement stmt = conn.prepareCall("{ call consultar_productos(?, ?, ?, ?, ?, ?) }")) {

            // Configurar parámetros de entrada
            stmt.setString(1, codigo);
            stmt.setString(2, nombre);
            if (categoriaId != null) {
                stmt.setInt(3, categoriaId);
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setBoolean(4, soloActivos);
            stmt.setString(5, orden);
            stmt.setBoolean(6, ascendente);

            // Ejecutar y obtener resultados
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Producto producto = new Producto(
                            rs.getInt("codigo"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getDouble("precio"),
                            rs.getInt("categoria_id"),
                            rs.getInt("stock"),
                            rs.getInt("stock_minimo"),
                            rs.getBoolean("estado")
                    );
                    productos.add(producto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar productos: " + e.getMessage());
            e.printStackTrace();
        }
        return productos;
    }
}