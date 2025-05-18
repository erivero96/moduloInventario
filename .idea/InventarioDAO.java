import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class InventarioDAO {

    private Connection conn;

    public InventarioDAO(Connection conn) {
        this.conn = conn;
    }
    //RF 02
    public void actualizarStock(int codigoProducto, int cantidad, String motivo, String usuarioId, String movId) throws SQLException {
        if (codigoProducto <= 0) {
            throw new IllegalArgumentException("El código del producto debe ser un entero positivo.");
        }
        if (cantidad == 0) {
            throw new IllegalArgumentException("La cantidad no puede ser cero.");
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo no puede ser vacío.");
        }
        if (usuarioId == null || usuarioId.length() != 8) {
            throw new IllegalArgumentException("El ID del usuario debe tener exactamente 8 caracteres.");
        }
        if (movId == null || movId.length() != 5) {
            throw new IllegalArgumentException("El ID del movimiento debe tener exactamente 5 caracteres.");
        }

        String sql = "{ CALL actualizar_stock(?, ?, ?, ?, ?) }";
        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, codigoProducto);
            stmt.setInt(2, cantidad);
            stmt.setString(3, motivo);
            stmt.setString(4, usuarioId);
            stmt.setString(5, movId);

            stmt.execute();
        }
    }
}
