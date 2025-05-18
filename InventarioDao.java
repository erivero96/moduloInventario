package org.example;

import java.sql.*;

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
        String sql = "SELECT cambiar_estado_producto(?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, codigo);
            statement.setBoolean(2, estado);
            statement.executeQuery();
        } catch (SQLException e) {
            if (e.getMessage().contains("Producto con código")) {
                System.out.println("false");
                System.err.println("Producto no encontrado: código " + codigo );
            } else {
                System.err.println("Error al cambiar el estado del producto");
            }
        }
    }



    public boolean obtenerEstadoProducto(int codigo) {
        String sql = "CALL obtener_estado_producto(?, ?)";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, codigo); // IN: código del producto
            stmt.registerOutParameter(2, java.sql.Types.BOOLEAN); // OUT: estado_producto

            stmt.execute();
            return stmt.getBoolean(2);

        } catch (SQLException e) {
            String mensaje = e.getMessage();
            if (mensaje != null && mensaje.contains("Producto con código")) {
                System.err.println("Producto no encontrado: código " + codigo);
                return false; // o lanza una excepción personalizada
            }

            e.printStackTrace();
            throw new RuntimeException("Error al obtener el estado del producto", e);
        }
    }


    
}
