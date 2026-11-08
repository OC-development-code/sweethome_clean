package org.UI.dialogs;

import org.modelo.Reporte;
import org.modelo.Usuario;
import org.servicios.ReporteService;
import org.UI.estilo.BotonEstilos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ReporteVisualizadorDialog extends JDialog {
    private final String categoria;
    private final ReporteService service;
    private final Usuario usuario;

    private JComboBox<String> cmbPeriodo;
    private JList<Reporte> listaReportes;
    private DefaultListModel<Reporte> modelLista;
    private JTextArea txtPreview;
    private JButton btnVisualizar;
    private JButton btnEliminar;
    private Reporte seleccionado;

    public ReporteVisualizadorDialog(Frame parent, String categoria, Usuario usuario) {
        super(parent, "📊 Reportes - " + categoria, true);
        this.categoria = categoria;
        this.service = new ReporteService();
        this.usuario = usuario;
        initComponents();
        setSize(1024, 720);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        cargarReportes();
    }

    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(new EmptyBorder(15, 15, 15, 15));
        main.setBackground(Color.WHITE);

        // Filtros
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filtros.setBackground(new Color(245, 248, 255));
        filtros.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                "📋 Filtros",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)));

        filtros.add(new JLabel("📅 Período:"));
        cmbPeriodo = new JComboBox<>(new String[]{"Hoy", "Esta Semana", "Este Mes", "Este Año", "Personalizado"});
        cmbPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filtros.add(cmbPeriodo);

        JButton btnGenerar = new JButton("🔄 Generar Reporte");
        BotonEstilos.aplicarEstiloPersonalizado(btnGenerar);
        btnGenerar.addActionListener(e -> generar());
        filtros.add(btnGenerar);

        main.add(filtros, BorderLayout.NORTH);

        // Split lista + preview
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.35);

        JPanel panelLista = new JPanel(new BorderLayout(5, 5));
        panelLista.setBackground(Color.WHITE);
        panelLista.setBorder(BorderFactory.createTitledBorder(
                "📄 Reportes Guardados (" + categoria + ")"));

        modelLista = new DefaultListModel<>();
        listaReportes = new JList<>(modelLista);
        listaReportes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listaReportes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaReportes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionado = listaReportes.getSelectedValue();
                btnVisualizar.setEnabled(seleccionado != null);
                btnEliminar.setEnabled(seleccionado != null);
                actualizarPreview();
            }
        });
        listaReportes.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) visualizar();
            }
        });
        panelLista.add(new JScrollPane(listaReportes), BorderLayout.CENTER);
        split.setLeftComponent(panelLista);

        JPanel panelPrev = new JPanel(new BorderLayout(5, 5));
        panelPrev.setBackground(Color.WHITE);
        panelPrev.setBorder(BorderFactory.createTitledBorder("👁 Previsualización"));
        txtPreview = new JTextArea();
        txtPreview.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtPreview.setEditable(false);
        txtPreview.setBackground(new Color(252, 252, 252));
        txtPreview.setText("Seleccione un reporte para previsualizar");
        panelPrev.add(new JScrollPane(txtPreview), BorderLayout.CENTER);
        split.setRightComponent(panelPrev);

        main.add(split, BorderLayout.CENTER);

        // Botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        botones.setBackground(new Color(240, 248, 255));

        btnVisualizar = new JButton("👁 Visualizar");
        BotonEstilos.aplicarEstiloPersonalizado(btnVisualizar);
        btnVisualizar.setEnabled(false);
        btnVisualizar.addActionListener(e -> visualizar());
        botones.add(btnVisualizar);

        btnEliminar = new JButton("🗑️ Eliminar");
        BotonEstilos.aplicarEstiloPersonalizado(btnEliminar);
        btnEliminar.setEnabled(false);
        btnEliminar.addActionListener(e -> eliminar());
        botones.add(btnEliminar);

        JButton btnCerrar = new JButton("✖ Cerrar");
        BotonEstilos.aplicarEstiloPersonalizado(btnCerrar);
        btnCerrar.addActionListener(e -> dispose());
        botones.add(btnCerrar);

        main.add(botones, BorderLayout.SOUTH);
        add(main);
    }

    private void cargarReportes() {
        modelLista.clear();
        List<Reporte> reportes = service.obtenerReportesPorCategoria(categoria);
        for (Reporte r : reportes) modelLista.addElement(r);
        if (modelLista.isEmpty()) {
            btnVisualizar.setEnabled(false);
            btnEliminar.setEnabled(false);
        }
    }

    private void actualizarPreview() {
        if (seleccionado == null) {
            txtPreview.setText("Seleccione un reporte");
            return;
        }
        String[] lines = seleccionado.getContenido().split("\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(lines.length, 30); i++)
            sb.append(lines[i]).append("\n");
        if (lines.length > 30) sb.append("\n... (más contenido al visualizar)");
        txtPreview.setText(sb.toString());
        txtPreview.setCaretPosition(0);
    }

    private void generar() {
        String periodo = (String) cmbPeriodo.getSelectedItem();
        service.generarYGuardarReporte(categoria, periodo, usuario.getId());
        JOptionPane.showMessageDialog(this, "✅ Reporte generado y guardado");
        cargarReportes();
    }

    private void visualizar() {
        if (seleccionado == null) return;
        String periodo = extraerPeriodo(seleccionado.getContenido());
        ReporteDetalleDialog d = new ReporteDetalleDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                categoria, seleccionado.getContenido(), periodo);
        d.setVisible(true);
    }

    private String extraerPeriodo(String contenido) {
        for (String line : contenido.split("\n")) {
            if (line.contains("Período:"))
                return line.replace("Período:", "").trim();
        }
        return "General";
    }

    private void eliminar() {
        if (seleccionado == null) return;
        int c = JOptionPane.showConfirmDialog(this,
                "¿Eliminar reporte #" + seleccionado.getId() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        if (service.eliminarReporte(seleccionado.getId())) {
            cargarReportes();
        }
    }
}