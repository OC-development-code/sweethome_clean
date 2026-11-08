package org.UI;

import org.modelo.Factura;
import org.modelo.Usuario;
import org.servicios.FacturaService;
import org.UI.dialogs.GestionarCompraDialog;
import org.UI.dialogs.DetalleVentaDialog;
import org.UI.estilo.BotonEstilos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentasPanel extends JPanel {
    private final FacturaService facturaService;
    private final Usuario usuario;
    private JTable tablaVentas;
    private DefaultTableModel tableModel;
    private JButton btnGestionarCompra;
    private JButton btnVerDetalle;
    private JTextField txtBuscar;
    private int facturaSeleccionadaId = -1;

    public VentasPanel(Usuario usuario) {
        this.usuario = usuario;
        this.facturaService = new FacturaService();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);
        initComponents();
        actualizarVentas();
    }

    private void initComponents() {
        // TOP
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(Color.WHITE);

        JLabel lblBuscar = new JLabel("🔍 Buscar cliente:");
        lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        topPanel.add(lblBuscar);

        txtBuscar = new JTextField(15);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.addActionListener(e -> buscar());
        topPanel.add(txtBuscar);

        JButton btnBuscar = new JButton("Buscar");
        BotonEstilos.aplicarEstiloConTamaño(btnBuscar, 100, 32);
        btnBuscar.addActionListener(e -> buscar());
        topPanel.add(btnBuscar);

        JButton btnLimpiar = new JButton("✖ Limpiar");
        BotonEstilos.aplicarEstiloConTamaño(btnLimpiar, 100, 32);
        btnLimpiar.addActionListener(e -> {
            txtBuscar.setText("");
            actualizarVentas();
        });
        topPanel.add(btnLimpiar);

        add(topPanel, BorderLayout.NORTH);

        // CENTER
        String[] columnas = {"ID", "Fecha", "Cliente", "Total", "Estado", "Usuario"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaVentas = new JTable(tableModel);
        tablaVentas.setRowHeight(28);
        tablaVentas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaVentas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaVentas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tablaVentas.getSelectedRow();
                if (row >= 0) {
                    facturaSeleccionadaId = (int) tableModel.getValueAt(row, 0);
                    btnVerDetalle.setEnabled(true);
                } else {
                    facturaSeleccionadaId = -1;
                    btnVerDetalle.setEnabled(false);
                }
            }
        });
        add(new JScrollPane(tablaVentas), BorderLayout.CENTER);

        // BOTTOM
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                new EmptyBorder(5, 10, 5, 10)));

        btnGestionarCompra = new JButton("🛒 Gestionar Compra");
        BotonEstilos.aplicarEstiloPersonalizado(btnGestionarCompra);
        btnGestionarCompra.addActionListener(e -> abrirGestionarCompra());
        bottomPanel.add(btnGestionarCompra);

        btnVerDetalle = new JButton("👁 Ver Detalle");
        BotonEstilos.aplicarEstiloPersonalizado(btnVerDetalle);
        btnVerDetalle.setEnabled(false);
        btnVerDetalle.addActionListener(e -> verDetalle());
        bottomPanel.add(btnVerDetalle);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void buscar() {
        String texto = txtBuscar.getText().trim();
        List<Factura> lista = texto.isEmpty()
                ? facturaService.obtenerTodas()
                : facturaService.buscarPorCliente(texto);
        actualizarTabla(lista);
    }

    private void actualizarTabla(List<Factura> facturas) {
        tableModel.setRowCount(0);
        org.DAO.UsuarioDAO usuarioDAO = new org.DAO.UsuarioDAO();
        for (Factura f : facturas) {
            String nombreUsu = "";
            if (f.getUsuarioId() != null) {
                org.modelo.Usuario u = usuarioDAO.obtenerPorId(f.getUsuarioId());
                if (u != null) nombreUsu = u.getNomUsu();
            }
            tableModel.addRow(new Object[]{
                    f.getId(), f.getFechaEmision(), f.getNombreCliente(),
                    String.format("S/. %.2f", f.getTotal()),
                    f.getEstado(), nombreUsu
            });
        }
    }

    public void actualizarVentas() { buscar(); }

    private void abrirGestionarCompra() {
        GestionarCompraDialog d = new GestionarCompraDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), facturaService, usuario);
        d.setVisible(true);
        if (d.isFacturaGenerada()) actualizarVentas();
    }

    private void verDetalle() {
        if (facturaSeleccionadaId == -1) return;
        DetalleVentaDialog d = new DetalleVentaDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), facturaService,
                facturaSeleccionadaId, usuario);
        d.setVisible(true);
        if (d.isAnulada()) actualizarVentas();
    }
}