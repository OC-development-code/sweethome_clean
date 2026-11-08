package org.UI;

import org.modelo.Producto;
import org.modelo.Usuario;
import org.servicios.InventarioService;
import org.UI.dialogs.NuevoProductoDialog;
import org.UI.dialogs.GestionarProductoDialog;
import org.UI.dialogs.EliminarProductoDialog;
import org.UI.estilo.BotonEstilos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductosPanel extends JPanel {
    private final InventarioService service;
    private final Usuario usuario;
    private JTable tablaProductos;
    private DefaultTableModel tableModel;
    private JTextField txtBuscar;
    private JComboBox<String> cmbFiltro;
    private JLabel lblTotalProductos;
    private JTextArea txtResumenCategorias;
    private JButton btnNuevoProducto;
    private JButton btnGestionarStock;
    private JButton btnEliminar;
    private int productoSeleccionadoId = -1;

    public ProductosPanel(Usuario usuario) {
        this.usuario = usuario;
        this.service = new InventarioService();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);
        initComponents();
        configurarPermisos();
        actualizarTabla();
    }

    private void initComponents() {
        // TOP
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(Color.WHITE);

        JLabel lblBuscar = new JLabel("🔍 Buscar:");
        lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        topPanel.add(lblBuscar);

        txtBuscar = new JTextField(20);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { buscarProductos(); }
        });
        topPanel.add(txtBuscar);

        JLabel lblFiltro = new JLabel("Filtrar por:");
        lblFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        topPanel.add(lblFiltro);

        cmbFiltro = new JComboBox<>(new String[]{"Código", "Nombre", "Categoría"});
        cmbFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbFiltro.addActionListener(e -> buscarProductos());
        topPanel.add(cmbFiltro);

        add(topPanel, BorderLayout.NORTH);

        // CENTER
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.72);
        splitPane.setBorder(new EmptyBorder(5, 0, 5, 0));

        String[] columnas = {"ID", "Nombre", "Categoría", "Cantidad", "Stock Mín.", "Precio", "Últ. Act."};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaProductos = new JTable(tableModel);
        tablaProductos.setRowHeight(28);
        tablaProductos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaProductos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tablaProductos.getSelectedRow();
                if (row >= 0) productoSeleccionadoId = (int) tableModel.getValueAt(row, 0);
            }
        });
        splitPane.setLeftComponent(new JScrollPane(tablaProductos));

        // Aside de resumen
        JPanel asidePanel = new JPanel(new BorderLayout(10, 10));
        asidePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        asidePanel.setBackground(new Color(245, 248, 255));

        JLabel lblResumen = new JLabel("📊 Resumen");
        lblResumen.setFont(new Font("Segoe UI", Font.BOLD, 14));
        asidePanel.add(lblResumen, BorderLayout.NORTH);

        lblTotalProductos = new JLabel("Total: 0");
        lblTotalProductos.setFont(new Font("Segoe UI", Font.BOLD, 12));
        asidePanel.add(lblTotalProductos, BorderLayout.CENTER);

        txtResumenCategorias = new JTextArea(10, 15);
        txtResumenCategorias.setEditable(false);
        txtResumenCategorias.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtResumenCategorias.setBackground(new Color(245, 248, 255));
        asidePanel.add(new JScrollPane(txtResumenCategorias), BorderLayout.SOUTH);

        splitPane.setRightComponent(asidePanel);
        add(splitPane, BorderLayout.CENTER);

        // BOTTOM
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                new EmptyBorder(5, 10, 5, 10)));

        btnNuevoProducto = new JButton("➕ Nuevo Producto");
        BotonEstilos.aplicarEstiloPersonalizado(btnNuevoProducto);
        btnNuevoProducto.addActionListener(e -> abrirNuevoProducto());
        bottomPanel.add(btnNuevoProducto);

        btnGestionarStock = new JButton("📦 Gestionar Stock");
        BotonEstilos.aplicarEstiloPersonalizado(btnGestionarStock);
        btnGestionarStock.addActionListener(e -> abrirGestionarProducto());
        bottomPanel.add(btnGestionarStock);

        btnEliminar = new JButton("🗑️ Eliminar");
        BotonEstilos.aplicarEstiloPersonalizado(btnEliminar);
        btnEliminar.addActionListener(e -> abrirEliminarProducto());
        bottomPanel.add(btnEliminar);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void configurarPermisos() {
        btnEliminar.setEnabled(usuario.puedeEliminar());
        if (!usuario.puedeEliminar())
            btnEliminar.setToolTipText("Solo admin o 'sweethome' pueden eliminar");
    }

    private void buscarProductos() {
        String texto = txtBuscar.getText().trim();
        String filtro = (String) cmbFiltro.getSelectedItem();
        List<Producto> lista;

        if (texto.isEmpty()) lista = service.listarProductos();
        else switch (filtro) {
            case "Código":
                try {
                    Producto p = service.obtenerProducto(Integer.parseInt(texto));
                    lista = p != null ? List.of(p) : List.of();
                } catch (NumberFormatException e) { lista = List.of(); }
                break;
            case "Categoría": lista = service.buscarProductosPorCategoria(texto); break;
            default: lista = service.buscarProductos(texto);
        }
        actualizarTablaConProductos(lista);
    }

    private void actualizarTablaConProductos(List<Producto> productos) {
        tableModel.setRowCount(0);
        for (Producto p : productos) {
            tableModel.addRow(new Object[]{
                    p.getId(), p.getNombre(), p.getCategoria(), p.getCantidad(),
                    p.getStockMinimo(), String.format("S/. %.2f", p.getPrecioVenta()),
                    p.getFechaActualizacion()
            });
        }
        actualizarResumen(productos);
    }

    private void actualizarResumen(List<Producto> productos) {
        lblTotalProductos.setText("Total: " + productos.size());
        Map<String, Integer> cats = new HashMap<>();
        for (Producto p : productos) {
            String cat = p.getCategoria() != null ? p.getCategoria() : "Sin categoría";
            cats.put(cat, cats.getOrDefault(cat, 0) + 1);
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> e : cats.entrySet())
            sb.append(String.format("• %-18s: %d\n", e.getKey(), e.getValue()));
        txtResumenCategorias.setText(sb.toString());
    }

    public void actualizarTabla() { buscarProductos(); }

    private void abrirNuevoProducto() {
        NuevoProductoDialog d = new NuevoProductoDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), service, usuario);
        d.setVisible(true);
        if (d.isExitoso()) actualizarTabla();
    }

    private void abrirGestionarProducto() {
        if (productoSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Producto p = service.obtenerProducto(productoSeleccionadoId);
        if (p == null) return;
        GestionarProductoDialog d = new GestionarProductoDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), service, p, usuario);
        d.setVisible(true);
        if (d.isExitoso()) actualizarTabla();
    }

    private void abrirEliminarProducto() {
        if (productoSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!usuario.puedeEliminar()) {
            JOptionPane.showMessageDialog(this,
                    "❌ Sin permisos", "Permiso denegado", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Producto p = service.obtenerProducto(productoSeleccionadoId);
        if (p == null) return;
        EliminarProductoDialog d = new EliminarProductoDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), service, p, usuario);
        d.setVisible(true);
        if (d.isExitoso()) {
            actualizarTabla();
            productoSeleccionadoId = -1;
        }
    }
}