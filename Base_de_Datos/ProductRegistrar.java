import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRegistrar {
    private static final String URL = "jdbc:postgresql://localhost/inventario";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void registerProduct(Producto producto) throws SQLException {
        validateNombre(producto);
        validateDescripcion(producto);
        validatePrecio(producto);
        validateStock(producto);
        validateCategoria(producto);

        String sql = "CALL registrar_producto_por_nombre(?, ?, ?, ?, ?);";

        try (Connection conn = getConnection();
                CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, producto.getNombre());
            cs.setString(2, producto.getDescripcion());
            cs.setDouble(3, producto.getPrecio());
            cs.setString(4, producto.getCategoria());
            cs.setInt(5, producto.getStock());

            cs.execute();
            System.out.println("Producto registrado: " + producto.getNombre());
        }
    }

    public void deleteProduct(String nombreProducto) throws SQLException {
        validateNombreProducto(nombreProducto);
        validateProductoExiste(nombreProducto);

        String sql = "CALL eliminar_producto_por_nombre(?);";

        try (Connection conn = getConnection();
                CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, nombreProducto);
            cs.execute();
            System.out.println("Producto eliminado: " + nombreProducto);
        }
    }

    // Métodos de validación
    private void validateNombre(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre no puede estar vacío");
        }
        if (producto.getNombre().length() > 100) {
            throw new IllegalArgumentException("Nombre máximo 100 caracteres");
        }
    }

    private void validateDescripcion(Producto producto) {
        if (producto.getDescripcion() == null || producto.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("Descripción no puede estar vacía");
        }
        if (producto.getDescripcion().length() > 500) {
            throw new IllegalArgumentException("Descripción máxima 500 caracteres");
        }
    }

    private void validatePrecio(Producto producto) {
        if (producto.getPrecio() <= 0) {
            throw new IllegalArgumentException("Precio debe ser > 0");
        }
        if (producto.getPrecio() > 1_000_000) {
            throw new IllegalArgumentException("Precio máximo 1,000,000");
        }
    }

    private void validateStock(Producto producto) {
        if (producto.getStock() < 0) {
            throw new IllegalArgumentException("Stock no puede ser negativo");
        }
        if (producto.getStock() > 100_000) {
            throw new IllegalArgumentException("Stock máximo 100,000 unidades");
        }
    }

    private void validateCategoria(Producto producto) throws SQLException {
        if (producto.getCategoria() == null || producto.getCategoria().trim().isEmpty()) {
            throw new IllegalArgumentException("Categoría no puede estar vacía");
        }

        if (!getCategoriasValidas().contains(producto.getCategoria())) {
            throw new IllegalArgumentException("Categoría no válida. Válidas: " + getCategoriasValidas());
        }
    }

    private List<String> getCategoriasValidas() throws SQLException {
        List<String> categorias = new ArrayList<>();
        String sql = "SELECT nombre FROM categorias";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categorias.add(rs.getString("nombre"));
            }
        }
        return categorias;
    }

    private void validateNombreProducto(String nombreProducto) {
        if (nombreProducto == null || nombreProducto.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre no puede estar vacío");
        }
        if (nombreProducto.length() > 100) {
            throw new IllegalArgumentException("Nombre máximo 100 caracteres");
        }
    }

    private void validateProductoExiste(String nombreProducto) throws SQLException {
        if (!productoExiste(nombreProducto)) {
            throw new IllegalArgumentException("Producto '" + nombreProducto + "' no existe");
        }
    }

    private boolean productoExiste(String nombreProducto) throws SQLException {
        String sql = "SELECT COUNT(*) FROM productos WHERE nombre = ?";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombreProducto);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
  