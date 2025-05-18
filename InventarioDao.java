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
            e.printStackTrace();
            throw new RuntimeException("Error al llamar la función cambiar_estado_producto", e);
        }
    }

    public boolean obtenerEstadoProducto(int codigo) {
        String sql = "SELECT estado FROM productos WHERE codigo = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, codigo);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("estado");
            } else {
                throw new RuntimeException("Producto no encontrado");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener el estado del producto", e);
        }
    }
}
