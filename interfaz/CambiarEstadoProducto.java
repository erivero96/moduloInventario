package interfaz;

import org.example.InventarioDao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CambiarEstadoProducto extends JFrame {

    private final InventarioDao inventarioDao;

    private JTextField codigoField;
    private JCheckBox estadoCheckBox;

    public CambiarEstadoProducto(JFrame padre, InventarioDao inventarioDao) {
        this.inventarioDao = inventarioDao;

        setTitle("Cambiar Estado del Producto");
        setSize(350, 200);
        setLocationRelativeTo(padre);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        codigoField = new JTextField();
        estadoCheckBox = new JCheckBox("Activo");

        JButton btnCambiar = new JButton("Cambiar Estado");

        panel.add(new JLabel("Código del Producto:"));
        panel.add(codigoField);

        panel.add(new JLabel("Nuevo Estado:"));
        panel.add(estadoCheckBox);

        panel.add(new JLabel(""));
        panel.add(btnCambiar);

        btnCambiar.addActionListener(this::cambiarEstado);

        add(panel);
    }

    private void cambiarEstado(ActionEvent e) {
        try {
            int codigo = Integer.parseInt(codigoField.getText());
            boolean nuevoEstado = estadoCheckBox.isSelected();

            inventarioDao.cambiarEstadoProducto(codigo, nuevoEstado);
            JOptionPane.showMessageDialog(this, "Estado actualizado correctamente.");
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El código debe ser un número válido.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Error de validación: " + ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void abrir(JFrame padre, InventarioDao dao) {
        SwingUtilities.invokeLater(() -> {
            CambiarEstadoProducto ventana = new CambiarEstadoProducto(padre, dao);
            ventana.setVisible(true);
        });
    }
}
