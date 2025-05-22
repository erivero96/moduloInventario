package interfaz;

import org.example.InventarioDao;
import org.example.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ConsultarProductoUI extends JFrame {

    private JTextField txtCodigo, txtNombre;
    private JComboBox<String> cbCategoria, cbOrden;
    private JCheckBox chkSoloActivos, chkAscendente;
    private JTable tablaProductos;
    private InventarioDao inventarioDao;

    public ConsultarProductoUI(InventarioDao dao) {
        this.inventarioDao = dao;
        setTitle("Consultar Productos");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel superior: filtros
        JPanel panelFiltros = new JPanel(new GridLayout(3, 4, 5, 5));
        txtCodigo = new JTextField();
        txtNombre = new JTextField();
        cbCategoria = new JComboBox<>();
        cbOrden = new JComboBox<>(new String[]{"codigo", "nombre", "precio", "stock"});
        chkSoloActivos = new JCheckBox("Solo activos", true);
        chkAscendente = new JCheckBox("Ascendente", true);

        try {
            cbCategoria.addItem("Todas");
            for (String cat : inventarioDao.getCategoriasValidas()) {
                cbCategoria.addItem(cat);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando categorías: " + e.getMessage());
        }

        panelFiltros.add(new JLabel("Código:"));
        panelFiltros.add(txtCodigo);
        panelFiltros.add(new JLabel("Nombre:"));
        panelFiltros.add(txtNombre);
        panelFiltros.add(new JLabel("Categoría:"));
        panelFiltros.add(cbCategoria);
        panelFiltros.add(new JLabel("Ordenar por:"));
        panelFiltros.add(cbOrden);
        panelFiltros.add(chkSoloActivos);
        panelFiltros.add(chkAscendente);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarProductos());
        panelFiltros.add(btnBuscar);

        add(panelFiltros, BorderLayout.NORTH);

        // Tabla de resultados
        tablaProductos = new JTable();
        JScrollPane scroll = new JScrollPane(tablaProductos);
        add(scroll, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void buscarProductos() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String categoria = cbCategoria.getSelectedItem().toString();
        Integer categoriaId = categoria.equals("Todas") ? null : obtenerCategoriaId(categoria);
        boolean soloActivos = chkSoloActivos.isSelected();
        String orden = cbOrden.getSelectedItem().toString();
        boolean ascendente = chkAscendente.isSelected();

        try {
            List<Producto> productos = inventarioDao.consultarProductos(
                    codigo.isEmpty() ? null : codigo,
                    nombre.isEmpty() ? null : nombre,
                    categoriaId,
                    soloActivos,
                    orden,
                    ascendente
            );
            mostrarEnTabla(productos);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al consultar: " + ex.getMessage());
        }
    }

    private Integer obtenerCategoriaId(String nombreCategoria) {
        try {
            List<String> categorias = inventarioDao.getCategoriasValidas();
            return categorias.indexOf(nombreCategoria) + 1;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error obteniendo ID de categoría.");
            return null;
        }
    }

    private void mostrarEnTabla(List<Producto> productos) {
        String[] columnas = {"Código", "Nombre", "Descripción", "Precio", "Categoría", "Stock"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);

        for (Producto p : productos) {
            model.addRow(new Object[]{
                    p.getCodigo(),
                    p.getNombre(),
                    p.getDescripcion(),
                    p.getPrecio(),
                    p.getCategoria(),
                    p.getStock(),
            });
        }

        tablaProductos.setModel(model);
    }
}
