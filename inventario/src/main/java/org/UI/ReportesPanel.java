package org.UI;

import org.modelo.Usuario;
import org.UI.estilo.BotonEstilos;
import org.UI.dialogs.ReporteVisualizadorDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ReportesPanel extends JPanel {
    private final Usuario usuario;

    public ReportesPanel(Usuario usuario) {
        this.usuario = usuario;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelFiltros.setBackground(new Color(245, 248, 255));
        panelFiltros.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                "📋 Panel de Reportes",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)));

        JLabel lblInfo = new JLabel("Seleccione el tipo de reporte que desea gestionar:");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelFiltros.add(lblInfo);
        add(panelFiltros, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 20, 20));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JButton btnVentas = new JButton("📊 Reporte de Ventas");
        BotonEstilos.aplicarEstiloConTamaño(btnVentas, 250, 80);
        btnVentas.addActionListener(e -> abrir("Ventas"));
        panelBotones.add(btnVentas);

        JButton btnInv = new JButton("📦 Reporte de Inventario");
        BotonEstilos.aplicarEstiloConTamaño(btnInv, 250, 80);
        btnInv.addActionListener(e -> abrir("Inventario"));
        panelBotones.add(btnInv);

        JButton btnCost = new JButton("💰 Reporte de Costos");
        BotonEstilos.aplicarEstiloConTamaño(btnCost, 250, 80);
        btnCost.addActionListener(e -> abrir("Costos"));
        panelBotones.add(btnCost);

        JButton btnStock = new JButton("⚠️ Reporte de Stock Bajo");
        BotonEstilos.aplicarEstiloConTamaño(btnStock, 250, 80);
        btnStock.addActionListener(e -> abrir("Stock"));
        panelBotones.add(btnStock);

        add(panelBotones, BorderLayout.CENTER);
    }

    private void abrir(String categoria) {
        ReporteVisualizadorDialog d = new ReporteVisualizadorDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), categoria, usuario);
        d.setVisible(true);
    }

    public void actualizarReportes() {
        // Los reportes se cargan desde el diálogo
    }
}