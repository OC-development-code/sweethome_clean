package org.UI.dialogs;

import org.modelo.DetalleFactura;
import org.modelo.Factura;
import org.modelo.Producto;
import org.modelo.Usuario;
import org.servicios.FacturaService;
import org.servicios.InventarioService;
import org.UI.estilo.BotonEstilos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GestionarCompraDialog extends JDialog {

    private final FacturaService facturaService;
    private final InventarioService inventarioService;
    private final Usuario usuario;

    private JTextField txtBuscar;
    private JList<Producto> listaProductos;
    private DefaultListModel<Producto> modelProductos;
    private JButton btnIncluir;

    private DefaultListModel<String> modelCarrito;
    private JList<String> listaCarrito;
    private JLabel lblTotalCarrito;
    private final List<ItemCarrito> carrito = new ArrayList<>();

    private JButton btnGenerarFactura;
    private boolean facturaGenerada = false;

    private static class ItemCarrito {
        Producto producto;
        int cantidad;
        double subtotal;
        ItemCarrito(Producto p, int c) {
            this.producto = p;
            this.cantidad = c;
            this.subtotal = p.getPrecioVenta() * c;
        }
        @Override public String toString() {
            return String.format("%s x%d = S/. %.2f",
                    producto.getNombre(), cantidad, subtotal);
        }
    }

    public GestionarCompraDialog(Frame parent, FacturaService facturaService, Usuario usuario) {
        super(parent, "🛒 Gestionar Compra", true);
        this.facturaService = facturaService;
        this.inventarioService = new InventarioService();
        this.usuario = usuario;
        initComponents();
        setSize(1100, 700);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        cargarProductos();
    }

    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(new EmptyBorder(15, 15, 15, 15));
        main.setBackground(Color.WHITE);

        // ASIDE IZQUIERDO
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                "📦 Productos Disponibles",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)));

        JPanel search = new JPanel(new BorderLayout(5, 5));
        search.setBackground(Color.WHITE);
        search.setBorder(new EmptyBorder(5, 5, 5, 5));
        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });
        search.add(new JLabel("🔍 Buscar:"), BorderLayout.WEST);
        search.add(txtBuscar, BorderLayout.CENTER);
        leftPanel.add(search, BorderLayout.NORTH);

        modelProductos = new DefaultListModel<>();
        listaProductos = new JList<>(modelProductos);
        listaProductos.setCellRenderer(new ProductoRenderer());
        listaProductos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaProductos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                btnIncluir.setEnabled(listaProductos.getSelectedValue() != null);
        });
        listaProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && btnIncluir.isEnabled()) incluir();
            }
        });
        JScrollPane scrollP = new JScrollPane(listaProductos);
        scrollP.setPreferredSize(new Dimension(350, 0));
        leftPanel.add(scrollP, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.WHITE);
        btnIncluir = new JButton("➕ Incluir");
        BotonEstilos.aplicarEstiloPersonalizado(btnIncluir);
        btnIncluir.setEnabled(false);
        btnIncluir.addActionListener(e -> incluir());
        btnPanel.add(btnIncluir);
        leftPanel.add(btnPanel, BorderLayout.SOUTH);

        main.add(leftPanel, BorderLayout.WEST);

        // ÁREA DERECHA - Carrito
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                "🛒 Carrito de Compra",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)));

        modelCarrito = new DefaultListModel<>();
        listaCarrito = new JList<>(modelCarrito);
        listaCarrito.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rightPanel.add(new JScrollPane(listaCarrito), BorderLayout.CENTER);

        JPanel totalPanel = new JPanel(new BorderLayout(10, 10));
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        lblTotalCarrito = new JLabel("Total: S/. 0.00");
        lblTotalCarrito.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalCarrito.setForeground(new Color(0, 102, 0));
        totalPanel.add(lblTotalCarrito, BorderLayout.WEST);

        btnGenerarFactura = new JButton("🧾 Generar Factura");
        BotonEstilos.aplicarEstiloPersonalizado(btnGenerarFactura);
        btnGenerarFactura.setEnabled(false);
        btnGenerarFactura.addActionListener(e -> generarFactura());
        totalPanel.add(btnGenerarFactura, BorderLayout.EAST);
        rightPanel.add(totalPanel, BorderLayout.SOUTH);

        main.add(rightPanel, BorderLayout.CENTER);
        add(main);
    }

    private void cargarProductos() {
        modelProductos.clear();
        List<Producto> productos = inventarioService.listarProductos();
        // Ordenar por categoría y nombre
        productos.sort((a, b) -> {
            String ca = a.getCategoria() != null ? a.getCategoria() : "";
            String cb = b.getCategoria() != null ? b.getCategoria() : "";
            int c = ca.compareToIgnoreCase(cb);
            return c != 0 ? c : a.getNombre().compareToIgnoreCase(b.getNombre());
        });
        for (Producto p : productos) modelProductos.addElement(p);
    }

    private void filtrar() {
        String texto = txtBuscar.getText().toLowerCase().trim();
        List<Producto> todos = inventarioService.listarProductos();
        modelProductos.clear();
        List<Producto> filtrados = texto.isEmpty()
                ? todos
                : todos.stream().filter(p -> p.getNombre().toLowerCase().contains(texto))
                  .collect(Collectors.toList());
        filtrados.sort((a, b) -> a.getNombre().compareToIgnoreCase(b.getNombre()));
        for (Producto p : filtrados) modelProductos.addElement(p);
    }

    private void incluir() {
        Producto p = listaProductos.getSelectedValue();
        if (p == null) return;

        if (p.getCantidad() <= 0) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ Sin stock disponible para " + p.getNombre(),
                    "Stock agotado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this,
                "Cantidad de \"" + p.getNombre() + "\" (Stock: " + p.getCantidad() + "):",
                "Cantidad", JOptionPane.QUESTION_MESSAGE);
        if (input == null) return;

        try {
            int cant = Integer.parseInt(input.trim());
            if (cant <= 0) {
                JOptionPane.showMessageDialog(this, "Cantidad debe ser > 0");
                return;
            }
            if (cant > p.getCantidad()) {
                JOptionPane.showMessageDialog(this,
                        "Stock insuficiente. Disponible: " + p.getCantidad());
                return;
            }

            carrito.add(new ItemCarrito(p, cant));
            modelCarrito.addElement(carrito.get(carrito.size() - 1).toString());
            actualizarTotal();
            btnGenerarFactura.setEnabled(!carrito.isEmpty());

            listaProductos.clearSelection();
            btnIncluir.setEnabled(false);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Número inválido");
        }
    }

    private void actualizarTotal() {
        double total = carrito.stream().mapToDouble(i -> i.subtotal).sum();
        lblTotalCarrito.setText(String.format("Total: S/. %.2f", total));
    }

    private void generarFactura() {
        if (carrito.isEmpty()) return;

        String cliente = JOptionPane.showInputDialog(this,
                "Nombre del cliente:", "Cliente General");
        if (cliente == null || cliente.trim().isEmpty()) cliente = "Cliente General";

        Factura factura = new Factura(cliente, usuario.getId());
        List<DetalleFactura> detalles = new ArrayList<>();
        for (ItemCarrito item : carrito) {
            detalles.add(new DetalleFactura(0, item.producto.getId(),
                    item.producto.getNombre(), item.cantidad,
                    item.producto.getPrecioVenta()));
        }

        Factura creada = facturaService.crearFactura(factura, detalles, usuario.getId());
        if (creada == null) {
            JOptionPane.showMessageDialog(this, "❌ Error al crear factura");
            return;
        }

        FacturaPreviewDialog preview = new FacturaPreviewDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), creada, detalles);
        preview.setVisible(true);

        if (preview.isImpreso()) {
            facturaGenerada = true;
            dispose();
        }
    }

    public boolean isFacturaGenerada() { return facturaGenerada; }

    /** Renderer con colores según stock */
    private static class ProductoRenderer extends JLabel implements ListCellRenderer<Producto> {
        @Override
        public Component getListCellRendererComponent(JList<? extends Producto> list,
                                                      Producto value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value == null) return this;
            String cat = value.getCategoria() != null ? "[" + value.getCategoria() + "] " : "";
            setText(cat + value.getNombre() + "  (Stock: " + value.getCantidad() + ")");
            setFont(new Font("Segoe UI", Font.PLAIN, 14));

            if (value.getCantidad() <= 0) {
                setForeground(Color.RED);
            } else if (value.getCantidad() <= value.getStockMinimo()) {
                setForeground(new Color(255, 140, 0)); // naranja
            } else {
                setForeground(Color.BLACK);
            }

            if (isSelected) {
                setBackground(new Color(70, 130, 180));
                setForeground(Color.WHITE);
                setOpaque(true);
            } else {
                setOpaque(false);
            }
            return this;
        }
    }
}