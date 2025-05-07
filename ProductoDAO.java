package moduloinventario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    private static final String URL = "jdbc:postgresql://localhost:5433/inventario";
    private static final String USER = "postgres";
    private static final String PASSWORD = "nuevaclave123";
    public boolean insertarProducto(Producto p) {
        String sql = "CALL registrar_producto(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, p.codigo);
            stmt.setString(2, p.nombre);
            stmt.setString(3, p.descripcion);
            stmt.setDouble(4, p.precio);
            stmt.setInt(5, p.categoria);
            stmt.setInt(6, p.stock);

            stmt.execute();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean actualizarStock(String codigo, int nuevoStock, String motivo, int usuarioId) {
        String sql = "UPDATE productos SET stock = ? WHERE codigo = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, nuevoStock);
            stmt.setString(2, codigo);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                registrarMovimiento(conn, codigo, nuevoStock, motivo, usuarioId);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private void registrarMovimiento(Connection conn, String codigo, int nuevoStock, String motivo, int usuarioId) throws SQLException {
        String sql = "INSERT INTO movimientos_stock (codigo_producto, fecha, cantidad, motivo, usuario_id) VALUES (?, NOW(), ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            stmt.setInt(2, nuevoStock);
            stmt.setString(3, motivo);
            stmt.setInt(4, usuarioId);
            stmt.executeUpdate();
        }
    }

    public List<Producto> buscarProductos(String filtro) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT p.codigo, p.nombre, p.descripcion, p.precio, p.stock, c.nombre as categoria " +
                     "FROM productos p JOIN categorias c ON p.categoria_id = c.id " +
                     "WHERE p.nombre ILIKE ? OR p.codigo ILIKE ? OR c.nombre ILIKE ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String wildcard = "%" + filtro + "%";
            stmt.setString(1, wildcard);
            stmt.setString(2, wildcard);
            stmt.setString(3, wildcard);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Producto p = new Producto(
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getDouble("precio"),
                    rs.getInt("categoria"),
                    rs.getInt("stock")
                );
                lista.add(p);
            }
        } catch (Exception e) {
        }
        return lista;
    }

    public List<Producto> listarProductos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT p.codigo, p.nombre, p.descripcion, p.precio, p.stock, c.nombre as categoria " +
                     "FROM productos p JOIN categorias c ON p.categoria_id = c.id";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Producto p = new Producto(
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getDouble("precio"),
                    rs.getInt("categoria"),
                    rs.getInt("stock")
                );
                lista.add(p);
            }
        } catch (Exception e) {
        }
        return lista;
    }

    public boolean darDeBajaProducto(String codigo) {
        String sql = "UPDATE productos SET stock = 0 WHERE codigo = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        ProductoDAO dao = new ProductoDAO();

        Producto producto = new Producto("P107", "Monitor", "Monitor 27 pulgadas", 850.00, 1, 25);

        boolean insertado = dao.insertarProducto(producto);
        System.out.println("Insertado: " + insertado);

        boolean actualizado = dao.actualizarStock("P100", 30, "Reposición", 1);
        System.out.println("Stock actualizado: " + actualizado);

        List<Producto> buscados = dao.buscarProductos("Monitor");
        for (Producto p : buscados) {
            System.out.println("Buscado: " + p);
        }

        List<Producto> todos = dao.listarProductos();
        for (Producto p : todos) {
            System.out.println("Listado: " + p);
        }

        boolean baja = dao.darDeBajaProducto("P100");
        System.out.println("Dado de baja: " + baja);
    }
}
