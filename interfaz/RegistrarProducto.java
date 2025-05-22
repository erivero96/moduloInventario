package interfaz;

import org.example.InventarioDao;
import org.example.Producto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class RegistrarProducto extends JFrame {

    private final InventarioDao inventarioDao;
    private final JFrame padre;

    private JTextField nombreField;
    private JTextField descripcionField;
    private JTextField precioField;
    private JComboBox<String> categoriaComboBox;
    private JTextField stockField;

    public RegistrarProducto(JFrame padre, InventarioDao inventarioDao) throws SQLException {
        this.padre = padre;
        this.inventarioDao = inventarioDao;

        setTitle("Registrar Producto");
        setSize(400, 350);
        setLocationRelativeTo(padre);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() throws SQLException {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        nombreField = new JTextField();
        descripcionField = new JTextField();
        precioField = new JTextField();
        categoriaComboBox = new JComboBox<>();
        for (String categoria : inventarioDao.getCategoriasValidas()) {
            categoriaComboBox.addItem(categoria);
        }
        stockField = new JTextField();

        JButton btnRegistrar = new JButton("Registrar");

        panel.add(new JLabel("Nombre:"));
        panel.add(nombreField);

        panel.add(new JLabel("Descripción:"));
        panel.add(descripcionField);

        panel.add(new JLabel("Precio:"));
        panel.add(precioField);


        panel.add(new JLabel("Categoría:"));
        panel.add(categoriaComboBox);

        panel.add(new JLabel("Stock Inicial:"));
        panel.add(stockField);

        panel.add(new JLabel(""));
        panel.add(btnRegistrar);

        btnRegistrar.addActionListener(this::registrarProducto);

        add(panel);
    }

    private void registrarProducto(ActionEvent e) {
        try {
            String nombre = nombreField.getText();
            String descripcion = descripcionField.getText();
            double precio = Double.parseDouble(precioField.getText());
            String categoria = categoriaComboBox.getSelectedItem().toString();
            int stock = Integer.parseInt(stockField.getText());

            Producto producto = new Producto(0, nombre, descripcion, precio, 0, stock, 0, true);
            producto.setCategoria(categoria);

            inventarioDao.registerProduct(producto);
            JOptionPane.showMessageDialog(this, "Producto registrado correctamente.");
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Precio o stock inválidos.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar: " + ex.getMessage());
        }
    }
}
