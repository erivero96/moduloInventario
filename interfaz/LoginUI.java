package interfaz;

import org.example.InventarioDao;
import org.example.LoginService;
import org.example.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginUI extends JFrame {

    private final JTextField correoField;
    private final JPasswordField contraseñaField;

    public LoginUI() {
        setTitle("Inicio de Sesión");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel de formulario
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Correo:"));
        correoField = new JTextField();
        formPanel.add(correoField);

        formPanel.add(new JLabel("Contraseña:"));
        contraseñaField = new JPasswordField();
        formPanel.add(contraseñaField);

        add(formPanel, BorderLayout.CENTER);

        // Botón de login
        JButton loginButton = new JButton("Iniciar Sesión");
        add(loginButton, BorderLayout.SOUTH);

        // Acción del botón
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarLogin();
            }
        });
    }

    private void realizarLogin() {
        String correo = correoField.getText();
        String contraseña = new String(contraseñaField.getPassword());

        try {
            InventarioDao dao = new InventarioDao(); // Ya tiene conexión interna
            LoginService loginService = new LoginService(dao.getConnection());

            Usuario usuario = loginService.login(correo, contraseña);
            if (usuario != null) {
                JOptionPane.showMessageDialog(this, "Bienvenido, " + usuario.getCorreo() + " (" + usuario.getRol() + ")");
                dispose(); // Cierra la ventana de login
                MenuPrincipal.abrir(usuario); // Abre el menú

                // Aquí puedes redirigir al menú principal o siguiente ventana
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas.");
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Error de validación: " + ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al intentar iniciar sesión: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginUI login = new LoginUI();
            login.setVisible(true);
        });
    }
}
