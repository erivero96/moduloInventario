package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import exceptions.CantidadInvalidaException;
import exceptions.CodigoProductoInvalidoException;
import exceptions.MotivoInvalidoException;
import exceptions.MovimientoIdInvalidoException;
import exceptions.UsuarioIdInvalidoException;

public class InventarioDao {

    private static final String URL = "jdbc:postgresql://localhost:5433/inventario";
    private static final String USER = "postgres";
    private static final String PASSWORD = "nuevaclave123";
    private final Connection connection;

    public InventarioDao() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al conectar con la base de datos", e);
        }
    }

    public void cambiarEstadoProducto(int codigo, boolean estado) {
        try {
            if (codigo <= 0) {
                throw new IllegalArgumentException("El código del producto debe ser un número positivo.");
            }
            if (String.valueOf(codigo).length() > 4) {
                throw new IllegalArgumentException("El código del producto no debe tener más de 4 dígitos.");
            }
            if (!String.valueOf(codigo).matches("\\d+")) {
                throw new IllegalArgumentException("El código del producto debe contener solo números.");
            }
            boolean estadoActual = obtenerEstadoProducto(codigo);
            if (estadoActual == estado) {
                throw new IllegalArgumentException("El estado ya es el mismo. No se realizaron cambios.");
            }
            if (!String.valueOf(codigo).trim().equals(String.valueOf(codigo))) {
                throw new IllegalArgumentException("El código contiene espacios no permitidos.");
            }
            if (codigo > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("El código excede el valor permitido.");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        }

        String sql = "CALL cambiar_estado_producto(?, ?)";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, codigo);
            stmt.setBoolean(2, estado);
            stmt.execute();
        } catch (SQLException e) {
            if (e.getMessage().contains("Producto con código")) {
                throw new IllegalArgumentException("Producto no encontrado: código " + codigo, e);
            } else {
                throw new RuntimeException("Error al cambiar el estado del producto", e);
            }
        }
    }

    public boolean obtenerEstadoProducto(int codigo) {
        try {
            if (codigo <= 0) {
                throw new IllegalArgumentException("El código del producto debe ser un número positivo.");
            }
            if (String.valueOf(codigo).length() > 4) {
                throw new IllegalArgumentException("El código del producto no debe tener más de 4 dígitos.");
            }
            if (!String.valueOf(codigo).matches("\\d+")) {
                throw new IllegalArgumentException("El código del producto debe contener solo números.");
            }
            if (!String.valueOf(codigo).trim().equals(String.valueOf(codigo))) {
                throw new IllegalArgumentException("El código contiene espacios no permitidos.");
            }
            if (codigo > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("El código excede el valor permitido.");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        }

        String sql = "CALL obtener_estado_producto(?, ?)";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, codigo); // IN: código del producto
            stmt.registerOutParameter(2, java.sql.Types.BOOLEAN); // OUT: estado_producto

            stmt.execute();
            return stmt.getBoolean(2);
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("Producto con código")) {
                throw new IllegalArgumentException("Producto no encontrado: código " + codigo, e);
            }
            throw new RuntimeException("Error al obtener el estado del producto", e);
        }
    }

    public void actualizarStock(Producto producto, int cantidad, String motivo, String usuarioId, String movId) throws SQLException {
        int codigoProducto;
    
        try {
            codigoProducto = producto.getCodigo();
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

        if (producto.getNombre().matches(".*[\"'\\p{So}].*")) {
            throw new IllegalArgumentException("El nombre no debe contener comillas ni emojis.");
        }
        if (producto.getDescripcion().matches(".*[\"'\\p{So}].*")) {
            throw new IllegalArgumentException("La descripción no debe contener comillas ni emojis.");
        }
        if (motivo.matches(".*[\"'\\p{So}].*")) {
            throw new MotivoInvalidoException("El motivo no debe contener comillas ni emojis.");
        }
        if (usuarioId.matches(".*[\"'\\p{So}].*")) {
            throw new UsuarioIdInvalidoException("El ID de usuario no debe contener comillas ni emojis.");
        }
        if (movId.matches(".*[\"'\\p{So}].*")) {
            throw new MovimientoIdInvalidoException("El ID de movimiento no debe contener comillas ni emojis.");
        }

    
        String sql = "CALL registrar_movimiento_stock(?, ?, ?, ?, ?)";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, movId);
            stmt.setInt(2, codigoProducto);
            stmt.setInt(3, cantidad);
            stmt.setString(4, motivo);
            stmt.setString(5, usuarioId);
    
            stmt.execute();
        }
    }
    

    public List<MovimientoStock> listarMovimientosPorProducto(Producto producto) throws SQLException {
        validateNombre(producto);
        validateDescripcion(producto);
        validatePrecio(producto);
        validateStock(producto);
        validateCategoria(producto);
        

        if (producto.getNombre().matches(".*[\"'\\p{So}].*")) {
            throw new IllegalArgumentException("El nombre no debe contener comillas ni emojis.");
        }
        if (producto.getDescripcion().matches(".*[\"'\\p{So}].*")) {
            throw new IllegalArgumentException("La descripción no debe contener comillas ni emojis.");
        }

        List<MovimientoStock> movimientos = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall("CALL listar_movimientos_por_producto(?, ?)")) {
            stmt.setInt(1, producto.getCodigo());
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
        if (usuario == null || usuario.id == null || usuario.id.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario o su ID no pueden estar vacíos");
        }
        if (usuario.id.length() != 8) {
            throw new IllegalArgumentException("El ID del usuario debe tener exactamente 8 caracteres");
        }
        if (usuario.id.matches(".*[\"'\\p{So}].*")) {
            throw new IllegalArgumentException("El ID del usuario no debe contener comillas ni emojis");
        }


        List<MovimientoStock> movimientos = new ArrayList<>();
    
        try (CallableStatement stmt = connection.prepareCall("CALL listar_movimientos_por_usuario(?, ?)")) {
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

    public void registerProduct(Producto producto) throws SQLException {
        validateNombre(producto);
        validateDescripcion(producto);
        validatePrecio(producto);
        validateStock(producto);
        validateCategoria(producto);

        if (producto.getNombre().matches(".*[\"'\\p{So}].*")) {
            throw new IllegalArgumentException("El nombre no debe contener comillas ni emojis. \uD83E\uDD23");
        }
        if (producto.getDescripcion().matches(".*[\"'\\p{So}].*")) {
            throw new IllegalArgumentException("La descripción no debe contener comillas ni emojis. \uD83E\uDD23");
        }
    
        String sql = "CALL registrar_producto_por_nombre(?, ?, ?, ?, ?);";
    
        try (CallableStatement cs = connection.prepareCall(sql)) {
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
        if (nombreProducto.matches(".*[\"'\\p{So}].*")) {
            throw new IllegalArgumentException("El nombre del producto no debe contener comillas ni emojis.");
        }
    
        String sql = "CALL eliminar_producto_por_nombre(?);";
    
        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setString(1, nombreProducto);
            cs.execute();
            System.out.println("Producto eliminado: " + nombreProducto);
        }
    }
    public List<String> getCategoriasValidas() throws SQLException {
        List<String> categorias = new ArrayList<>();
        String sql = "SELECT nombre FROM categorias";
    
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
    
            while (rs.next()) {
                categorias.add(rs.getString("nombre"));
            }
        }
        return categorias;
    }

    private boolean productoExiste(String nombreProducto) throws SQLException {
        if (nombreProducto.matches(".*[\"'\\p{So}].*")) {
            throw new IllegalArgumentException("El nombre del producto no debe contener comillas ni emojis.");
        }
        String sql = "SELECT COUNT(*) FROM productos WHERE nombre = ?";
    
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nombreProducto);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
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

    public List<Producto> consultarProductos(String codigo, String nombre, Integer categoriaId,
                                         boolean soloActivos, String orden, boolean ascendente) {
        validarParametrosConsulta(codigo, nombre, categoriaId, orden);

        if ((codigo != null && codigo.matches(".*[\"'\\p{So}].*")) ||
            (nombre != null && nombre.matches(".*[\"'\\p{So}].*")) ||
            (orden != null && orden.matches(".*[\"'\\p{So}].*"))) {
            throw new IllegalArgumentException("Los parámetros no deben contener comillas ni emojis.");
        }
        
        List<Producto> productos = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall("{ call consultar_productos(?, ?, ?, ?, ?, ?) }")) {
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

    private void validarParametrosConsulta(String codigo, String nombre, Integer categoriaId, String orden) {
        validarCodigoProducto(codigo);
        validarNombreProducto(nombre);
        validarCategoriaId(categoriaId);
        validarCampoOrden(orden);
    }
    
    private void validarCodigoProducto(String codigo) {
        if (codigo != null && !codigo.matches("\\d+")) {
            throw new IllegalArgumentException("El código debe ser un número válido");
        }
    }
    
    private void validarNombreProducto(String nombre) {
        if (nombre != null && nombre.length() > 150) {
            throw new IllegalArgumentException("El nombre no puede exceder los 150 caracteres");
        }
    }
    
    private void validarCategoriaId(Integer categoriaId) {
        if (categoriaId != null && categoriaId <= 0) {
            throw new IllegalArgumentException("El ID de categoría debe ser un número positivo");
        }
    }
    
    private void validarCampoOrden(String orden) {
        if (orden != null && !orden.matches("codigo|nombre|precio|stock")) {
            throw new IllegalArgumentException("Campo de orden inválido. Use: codigo, nombre, precio o stock");
        }
    }
    
    private void configurarParametrosConsulta(CallableStatement stmt, String codigo, String nombre, 
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
    
    private Producto mapearProductoDesdeResultSet(ResultSet rs) throws SQLException {
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
    
    private void manejarErrorSQL(String mensaje, SQLException e) {
        System.err.println(mensaje + ": " + e.getMessage());
        e.printStackTrace();
    }

    public Connection getConnection() {
        return connection;
    }

}

