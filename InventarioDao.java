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
        try {
            if (codigo <= 0) {
                throw new IllegalArgumentException("El código del producto debe ser un número positivo.");
            }
            if (String.valueOf(codigo).length() > 4) {
                throw new IllegalArgumentException("El código del producto no debe tener más de 4 dígitos.");
            }
            if (!String.valueOf(codigo).matches("\\d+")) {
                
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
            if (estado != true && estado != false) {
                throw new IllegalArgumentException("El estado debe ser verdadero o falso.");
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println(e.getMessage());
            return;
        }

        String sql = "CALL cambiar_estado_producto(?, ?)";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, codigo);
            stmt.setBoolean(2, estado);
            stmt.execute();
        } catch (SQLException e) {
            if (e.getMessage().contains("Producto con código")) {
                System.out.println("false");
                System.err.println("\u001B[31mProducto no encontrado: código " + codigo + "\u001B[0m");
            } else {
                System.err.println("\u001B[31mError al cambiar el estado del producto\u001B[0m");
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
            System.err.println(e.getMessage());
            throw e;
        }
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
