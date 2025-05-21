import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import exceptions.*;

public class InventarioDAO {

    private Connection conn;

    public void actualizarStock(Producto producto, int cantidad, String motivo, String usuarioId, String movId) throws SQLException {
        int codigoProducto;

        try {
            codigoProducto = Integer.parseInt(producto.codigo);
        } catch (NumberFormatException e) {
            throw new CodigoProductoInvalidoException("El código del producto no es un número válido.");
        }

        if (codigoProducto <= 0) {
            throw new CodigoProductoInvalidoException();
        }
        if (cantidad == 0) {
            throw new CantidadInvalidaException();
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new MotivoInvalidoException();
        }
        if (usuarioId == null || usuarioId.length() != 8) {
            throw new UsuarioIdInvalidoException();
        }
        if (movId == null || movId.length() != 5) {
            throw new MovimientoIdInvalidoException();
        }

        String sql = "{ CALL registrar_movimiento_stock(?, ?, ?, ?, ?) }";
        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, movId);
            stmt.setInt(2, codigoProducto);
            stmt.setInt(3, cantidad);
            stmt.setString(4, motivo);
            stmt.setString(5, usuarioId);

            stmt.execute();
        }
    }
    public List<MovimientoStock> listarMovimientosPorProducto(Producto producto) throws SQLException {
        List<MovimientoStock> movimientos = new ArrayList<>();

        try (CallableStatement stmt = conn.prepareCall("{ call listar_movimientos_por_producto(?, ?) }")) {
            stmt.setInt(1, Integer.parseInt(producto.codigo));
            stmt.registerOutParameter(2, java.sql.Types.OTHER);

            stmt.execute();

            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                while (rs.next()) {
                    MovimientoStock mov = new MovimientoStock(
                            rs.getString("id"),
                            rs.getInt("codigo_producto"),
                            rs.getTimestamp("fecha").toLocalDateTime(),
                            rs.getInt("cantidad"),
                            rs.getString("motivo"),
                            rs.getString("usuario_id")
                    );
                    movimientos.add(mov);
                }
            }
        }

        return movimientos;
    }

    public List<MovimientoStock> listarMovimientosPorUsuario(Usuario usuario) throws SQLException {
        List<MovimientoStock> movimientos = new ArrayList<>();

        try (CallableStatement stmt = conn.prepareCall("{ call listar_movimientos_por_usuario(?, ?) }")) {
            stmt.setString(1, usuario.id);
            stmt.registerOutParameter(2, java.sql.Types.OTHER);

            stmt.execute();

            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                while (rs.next()) {
                    MovimientoStock mov = new MovimientoStock(
                            rs.getString("id"),
                            rs.getInt("codigo_producto"),
                            rs.getTimestamp("fecha").toLocalDateTime(),
                            rs.getInt("cantidad"),
                            rs.getString("motivo"),
                            rs.getString("usuario_id")
                    );
                    movimientos.add(mov);
                }
            }
        }

        return movimientos;
    }
}
