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
        cargarConfiguracionBD();
    }

    private static void cargarConfiguracionBD() {
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
        validarParametrosConsulta(codigo, nombre, categoriaId, orden);
        
        List<Producto> productos = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             CallableStatement stmt = conn.prepareCall("{ call consultar_productos(?, ?, ?, ?, ?, ?) }")) {

            configurarParametrosConsulta(stmt, codigo, nombre, categoriaId, soloActivos, orden, ascendente);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProductoDesdeResultSet(rs));
                }
            }
        } catch (SQLException e) {
            manejarErrorSQL("Error al consultar productos", e);
        }
        return productos;
    }

    private static void validarParametrosConsulta(String codigo, String nombre, Integer categoriaId, String orden) {
        validarCodigoProducto(codigo);
        validarNombreProducto(nombre);
        validarCategoriaId(categoriaId);
        validarCampoOrden(orden);
    }

    private static void validarCodigoProducto(String codigo) {
        if (codigo != null && !codigo.matches("\\d+")) {
            throw new IllegalArgumentException("El código debe ser un número válido");
        }
    }

    private static void validarNombreProducto(String nombre) {
        if (nombre != null && nombre.length() > 150) {
            throw new IllegalArgumentException("El nombre no puede exceder los 150 caracteres");
        }
    }

    private static void validarCategoriaId(Integer categoriaId) {
        if (categoriaId != null && categoriaId <= 0) {
            throw new IllegalArgumentException("El ID de categoría debe ser un número positivo");
        }
    }

    private static void validarCampoOrden(String orden) {
        if (orden != null && !orden.matches("codigo|nombre|precio|stock")) {
            throw new IllegalArgumentException("Campo de orden inválido. Use: codigo, nombre, precio o stock");
        }
    }

    private static void configurarParametrosConsulta(CallableStatement stmt, String codigo, String nombre, 
                                                    Integer categoriaId, boolean soloActivos, String orden, 
                                                    boolean ascendente) throws SQLException {
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
    }

    private static Producto mapearProductoDesdeResultSet(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("codigo"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getDouble("precio"),
                rs.getInt("categoria_id"),
                rs.getInt("stock"),
                rs.getInt("stock_minimo"),
                rs.getBoolean("estado")
        );
    }

    private static void manejarErrorSQL(String mensaje, SQLException e) {
        System.err.println(mensaje + ": " + e.getMessage());
        e.printStackTrace();
    }
}