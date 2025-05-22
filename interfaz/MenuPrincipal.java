package interfaz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

import org.example.InventarioDao;
import org.example.Usuario;

public class MenuPrincipal extends JFrame {

    private final Usuario usuario;
    private final InventarioDao inventarioDao;

    public MenuPrincipal(Usuario usuario) {
        this.usuario = usuario;
        this.inventarioDao = new InventarioDao();

        setTitle("Menú Principal - Usuario: " + usuario.getCorreo());
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnRegistrarProducto = new JButton("Registrar Producto");
        JButton btnActualizarStock = new JButton("Actualizar Stock");
        JButton btnConsultarProductos = new JButton("Consultar Productos");
        JButton btnCambiarEstado = new JButton("Cambiar Estado Producto");
        JButton btnSalir = new JButton("Salir");

        btnRegistrarProducto.addActionListener(e -> {
            try {
                RegistrarProducto registrarProducto = new RegistrarProducto(this, inventarioDao);
                registrarProducto.setVisible(true);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al abrir la ventana de registro de producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        btnActualizarStock.addActionListener(e -> {
            ActualizarStockUI actualizarStockUI = new ActualizarStockUI(inventarioDao);
            actualizarStockUI.setVisible(true);
        });

        btnConsultarProductos.addActionListener(e -> {
            ConsultarProductoUI consultarProductoUI = new ConsultarProductoUI(inventarioDao);
            consultarProductoUI.setVisible(true);
        });

        btnCambiarEstado.addActionListener(e -> {
            CambiarEstadoProducto.abrir(this, inventarioDao);
        });

        btnSalir.addActionListener((ActionEvent e) -> System.exit(0));

        panel.add(btnRegistrarProducto);
        panel.add(btnActualizarStock);
        panel.add(btnConsultarProductos);
        panel.add(btnCambiarEstado);
        panel.add(btnSalir);

        add(panel);
    }

    public static void abrir(Usuario usuario) {
        SwingUtilities.invokeLater(() -> {
            MenuPrincipal menu = new MenuPrincipal(usuario);
            menu.setVisible(true);
        });
    }
}
