package org.example;

import java.sql.*;

public class LoginService {
    private final Connection connection;

    public LoginService(Connection connection) {
        this.connection = connection;
    }

    public Usuario login(String correo, String contraseña) throws SQLException {
        // Validar que el correo y la contraseña no contengan caracteres no permitidos
        if (correo == null || contraseña == null || 
            !correo.matches("^[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$") || 
            !contraseña.matches("^[\\w.@+-]+$")) {
            throw new IllegalArgumentException("El correo o la contraseña contienen caracteres no permitidos.");
        }

        // Validar que el correo no tenga más de un arroba y que el arroba esté en una posición válida
        int atIndex = correo.indexOf('@');
        if (atIndex == -1 || atIndex != correo.lastIndexOf('@') || atIndex == 0 || atIndex == correo.length() - 1) {
            throw new IllegalArgumentException("El correo no tiene un formato válido.");
        }


        String sql = "CALL login_usuario(?, ?, ?, ?)";

        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, correo);
            stmt.setString(2, contraseña);

            // Parámetros de salida
            stmt.registerOutParameter(3, Types.CHAR);      // p_id
            stmt.registerOutParameter(4, Types.VARCHAR);   // p_rol

            stmt.execute();

            String id = stmt.getString(3);
            String rol = stmt.getString(4);

            if (id != null && rol != null) {
                // Retorna un objeto Usuario si el login fue exitoso
                return new Usuario(id.trim(), correo, rol);
            } else {
                // Login fallido
                return null;
            }
        }
    }
}
