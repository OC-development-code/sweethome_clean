package org.UI;

import org.modelo.MateriaPrima;
import org.modelo.Usuario;
import org.servicios.InventarioService;
import org.UI.dialogs.NuevaMateriaPrimaDialog;
import org.UI.dialogs.IngresarMateriaPrimaDialog;
import org.UI.dialogs.EliminarMateriaPrimaDialog;
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

public class RecursosPanel extends JPanel {
    private final InventarioService service;
    private final Usuario usuario;
    private JTable tablaMaterias;
    private DefaultTableModel tableModel;
    private JTextField txtBuscar;
    private JComboBox<String> cmbFiltro;
    private JLabel lblTotalMaterias;
    private JTextArea txtResumenCategorias;
    private JButton btnNuevo;
    private JButton btnIngresar;
    private JButton btnEliminar;
    private int materiaSeleccionadaId = -1;

    public RecursosPanel(Usuario usuario) {
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
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(Color.WHITE);

        JLabel lblBuscar = new JLabel("🔍 Buscar:");
        lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        topPanel.add(lblBuscar);

        txtBuscar = new JTextField(20);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { buscarMaterias(); }
        });
        topPanel.add(txtBuscar);

        JLabel lblFiltro = new JLabel("Filtrar por:");
        lblFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        topPanel.add(lblFiltro);

        cmbFiltro = new JComboBox<>(new String[]{"Código", "Nombre", "Categoría"});
        cmbFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbFiltro.addActionListener(e -> buscarMaterias());
        topPanel.add(cmbFiltro);
        add(topPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.72);

        String[] columnas = {"ID", "Nombre", "Categoría", "Cantidad", "Stock Mín.", "Costo Unit.", "Últ. Act."};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaMaterias = new JTable(tableModel);
        tablaMaterias.setRowHeight(28);
        tablaMaterias.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaMaterias.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaMaterias.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tablaMaterias.getSelectedRow();
                if (row >= 0) materiaSeleccionadaId = (int) tableModel.getValueAt(row, 0);
            }
        });
        splitPane.setLeftComponent(new JScrollPane(tablaMaterias));

        JPanel asidePanel = new JPanel(new BorderLayout(10, 10));
        asidePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        asidePanel.setBackground(new Color(245, 248, 255));

        JLabel lblResumen = new JLabel("📊 Resumen");
        lblResumen.setFont(new Font("Segoe UI", Font.BOLD, 14));
        asidePanel.add(lblResumen, BorderLayout.NORTH);

        lblTotalMaterias = new JLabel("Total: 0");
        lblTotalMaterias.setFont(new Font("Segoe UI", Font.BOLD, 12));
        asidePanel.add(lblTotalMaterias, BorderLayout.CENTER);

        txtResumenCategorias = new JTextArea(10, 15);
        txtResumenCategorias.setEditable(false);
        txtResumenCategorias.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtResumenCategorias.setBackground(new Color(245, 248, 255));
        asidePanel.add(new JScrollPane(txtResumenCategorias), BorderLayout.SOUTH);

        splitPane.setRightComponent(asidePanel);
        add(splitPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                new EmptyBorder(5, 10, 5, 10)));

        btnNuevo = new JButton("➕ Nueva Materia Prima");
        BotonEstilos.aplicarEstiloPersonalizado(btnNuevo);
        btnNuevo.addActionListener(e -> abrirNuevo());
        bottomPanel.add(btnNuevo);

        btnIngresar = new JButton("📥 Gestionar Stock");
        BotonEstilos.aplicarEstiloPersonalizado(btnIngresar);
        btnIngresar.addActionListener(e -> abrirGestionar());
        bottomPanel.add(btnIngresar);

        btnEliminar = new JButton("🗑️ Eliminar");
        BotonEstilos.aplicarEstiloPersonalizado(btnEliminar);
        btnEliminar.addActionListener(e -> abrirEliminar());
        bottomPanel.add(btnEliminar);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void configurarPermisos() {
        btnEliminar.setEnabled(usuario.puedeEliminar());
        if (!usuario.puedeEliminar())
            btnEliminar.setToolTipText("Solo admin o 'sweethome' pueden eliminar");
    }

    private void buscarMaterias() {
        String texto = txtBuscar.getText().trim();
        String filtro = (String) cmbFiltro.getSelectedItem();
        List<MateriaPrima> lista;

        if (texto.isEmpty()) lista = service.listarMateriasPrimas();
        else switch (filtro) {
            case "Código":
                try {
                    MateriaPrima m = service.obtenerMateriaPrima(Integer.parseInt(texto));
                    lista = m != null ? List.of(m) : List.of();
                } catch (NumberFormatException e) { lista = List.of(); }
                break;
            case "Categoría": lista = service.buscarMateriasPorCategoria(texto); break;
            default: lista = service.buscarMateriasPrimas(texto);
        }
        actualizarTablaConMaterias(lista);
    }

    private void actualizarTablaConMaterias(List<MateriaPrima> materias) {
        tableModel.setRowCount(0);
        for (MateriaPrima m : materias) {
            tableModel.addRow(new Object[]{
                    m.getId(), m.getNombre(), m.getCategoria(), m.getCantidad(),
                    m.getStockMinimo(), String.format("S/. %.2f", m.getCostoUnitario()),
                    m.getFechaActualizacion()
            });
        }
        actualizarResumen(materias);
    }

    private void actualizarResumen(List<MateriaPrima> materias) {
        lblTotalMaterias.setText("Total: " + materias.size());
        Map<String, Integer> cats = new HashMap<>();
        int stockBajo = 0;
        for (MateriaPrima m : materias) {
            String cat = m.getCategoria() != null ? m.getCategoria() : "Sin categoría";
            cats.put(cat, cats.getOrDefault(cat, 0) + 1);
            if (m.getCantidad() <= m.getStockMinimo()) stockBajo++;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("⚠️ Stock bajo: ").append(stockBajo).append("\n");
        sb.append("─".repeat(20)).append("\n");
        for (Map.Entry<String, Integer> e : cats.entrySet())
            sb.append(String.format("• %-18s: %d\n", e.getKey(), e.getValue()));
        txtResumenCategorias.setText(sb.toString());
    }

    public void actualizarTabla() { buscarMaterias(); }

    private void abrirNuevo() {
        NuevaMateriaPrimaDialog d = new NuevaMateriaPrimaDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), service, usuario);
        d.setVisible(true);
        if (d.isExitoso()) actualizarTabla();
    }

    private void abrirGestionar() {
        if (materiaSeleccionadaId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una materia prima",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        MateriaPrima m = service.obtenerMateriaPrima(materiaSeleccionadaId);
        if (m == null) return;
        IngresarMateriaPrimaDialog d = new IngresarMateriaPrimaDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), service, m, usuario);
        d.setVisible(true);
        if (d.isExitoso()) actualizarTabla();
    }

    private void abrirEliminar() {
        if (materiaSeleccionadaId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una materia prima",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!usuario.puedeEliminar()) {
            JOptionPane.showMessageDialog(this,
                    "❌ Sin permisos", "Permiso denegado", JOptionPane.ERROR_MESSAGE);
            return;
        }
        MateriaPrima m = service.obtenerMateriaPrima(materiaSeleccionadaId);
        if (m == null) return;
        EliminarMateriaPrimaDialog d = new EliminarMateriaPrimaDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), service, m, usuario);
        d.setVisible(true);
        if (d.isExitoso()) {
            actualizarTabla();
            materiaSeleccionadaId = -1;
        }
    }
}