package interfaz;

import org.example.InventarioDao;
import org.example.Producto;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ActualizarStockUI extends JFrame {

    private final InventarioDao inventarioDao;

    private JTextField txtCodigo;
    private JTextField txtCantidad;
    private JTextField txtMotivo;
    private JTextField txtUsuarioId;
    private JTextField txtMovimientoId;

    public ActualizarStockUI(InventarioDao dao) {
        this.inventarioDao = dao;
        setTitle("Actualizar Stock");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Formulario
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtCodigo = new JTextField();
        txtCantidad = new JTextField();
        txtMotivo = new JTextField();
        txtUsuarioId = new JTextField();
        txtMovimientoId = new JTextField();

        panel.add(new JLabel("Código del producto:"));
        panel.add(txtCodigo);
        panel.add(new JLabel("Cantidad (+/-):"));
        panel.add(txtCantidad);
        panel.add(new JLabel("Motivo:"));
        panel.add(txtMotivo);
        panel.add(new JLabel("ID de Usuario (8 dígitos):"));
        panel.add(txtUsuarioId);
        panel.add(new JLabel("ID de Movimiento (5 caracteres):"));
        panel.add(txtMovimientoId);

        JButton btnActualizar = new JButton("Actualizar Stock");
        btnActualizar.addActionListener(e -> actualizarStock());

        add(panel, BorderLayout.CENTER);
        add(btnActualizar, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void actualizarStock() {
        try {
            int codigo = Integer.parseInt(txtCodigo.getText().trim());
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            String motivo = txtMotivo.getText().trim();
            String usuarioId = txtUsuarioId.getText().trim();
            String movimientoId = txtMovimientoId.getText().trim();

            // Aquí podrías buscar el producto por código (si tienes un método así)
            Producto producto = new Producto();
            producto.setCodigo(codigo);
            producto.setNombre("ProductoSimulado");  // Obligatorio para validación
            producto.setDescripcion("Simulado");     // Obligatorio para validación

            inventarioDao.actualizarStock(producto, cantidad, motivo, usuarioId, movimientoId);
            JOptionPane.showMessageDialog(this, "Stock actualizado correctamente");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Código y cantidad deben ser números enteros");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
