import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import exceptions.*;

public class InventarioDAO {

    private Connection conn;

    public InventarioDAO(Connection conn) {
        this.conn = conn;
    }
    //RF 02
    public void actualizarStock(int codigoProducto, int cantidad, String motivo, String usuarioId, String movId) throws SQLException {
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
            stmt.setInt(1, movId);
            stmt.setInt(2, codigoProducto);
            stmt.setString(3, cantidad);
            stmt.setString(4, motivo);
            stmt.setString(5, usuarioId);

            stmt.execute();
        }
    }
}
