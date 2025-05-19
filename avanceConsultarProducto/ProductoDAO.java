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

    // Cargar configuración desde db.properties
    static {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("db.properties"));
            URL = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");
        } catch (IOException e) {
            System.err.println("Error al cargar configuración de la base de datos: " + e.getMessage());
            // Valores por defecto en caso de error
            URL = "jdbc:postgresql://localhost:5432/inventario";
            USER = "postgres";
            PASSWORD = "nuevaclave123";
        }
    }

    /**
     * Consulta productos con filtros opcionales
     * @param codigo Código del producto (opcional)
     * @param nombre Nombre o parte del nombre (opcional)
     * @param categoriaId ID de categoría (opcional)
     * @param soloActivos Si true, solo devuelve productos con estado=true
     * @param orden Por qué campo ordenar ("nombre", "codigo", "precio", "stock")
     * @param ascendente Dirección del orden (true=ascendente, false=descendente)
     * @return Lista de productos que coinciden con los filtros
     */
    public static List<Producto> consultarProductos(String codigo, String nombre, Integer categoriaId, 
                                                  boolean soloActivos, String orden, boolean ascendente) {
        List<Producto> productos = new ArrayList<>();
        
        // Construir la consulta SQL dinámica
        StringBuilder sql = new StringBuilder("SELECT * FROM productos WHERE 1=1");
        
        // Agregar filtros según parámetros
        if (codigo != null && !codigo.isEmpty()) {
            sql.append(" AND codigo = ?");
        }
        if (nombre != null && !nombre.isEmpty()) {
            sql.append(" AND nombre ILIKE ?");
        }
        if (categoriaId != null) {
            sql.append(" AND categoria_id = ?");
        }
        if (soloActivos) {
            sql.append(" AND estado = TRUE");
        }
        
        // Agregar ordenamiento
        if (orden != null && !orden.isEmpty()) {
            String direccion = ascendente ? "ASC" : "DESC";
            sql.append(" ORDER BY ").append(orden).append(" ").append(direccion);
        }
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            // Establecer parámetros según los filtros
            if (codigo != null && !codigo.isEmpty()) {
                stmt.setInt(paramIndex++, Integer.parseInt(codigo));
            }
            if (nombre != null && !nombre.isEmpty()) {
                stmt.setString(paramIndex++, "%" + nombre + "%");
            }
            if (categoriaId != null) {
                stmt.setInt(paramIndex++, categoriaId);
            }
            
            ResultSet rs = stmt.executeQuery();
            
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
        } catch (SQLException e) {
            System.err.println("Error al consultar productos: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Formato de código inválido: " + e.getMessage());
        }
        
        return productos;
    }
}