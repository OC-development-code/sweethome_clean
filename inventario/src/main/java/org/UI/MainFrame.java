package org.UI;

import org.modelo.Usuario;
import org.UI.estilo.BotonEstilos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private final Usuario usuario;

    private ProductosPanel productosPanel;
    private RecursosPanel recursosPanel;
    private ReportesPanel reportesPanel;
    private VentasPanel ventasPanel;

    private JButton btnProductos;
    private JButton btnRecursos;
    private JButton btnReportes;
    private JButton btnVentas;
    private JButton btnAdmin;

    public MainFrame(Usuario usuario) {
        this.usuario = usuario;
        setTitle("SweetHome Clean - Sistema de Inventario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        initComponents();
        configurarPermisos();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 51, 102));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("SweetHome Clean");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        headerPanel.add(lblTitulo, BorderLayout.WEST);

        JLabel lblUsuario = new JLabel(
                "Bienvenido, " + usuario.getNomUsu() + " (" + usuario.getRol() + ")");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUsuario.setForeground(Color.WHITE);
        headerPanel.add(lblUsuario, BorderLayout.EAST);

        // NAV
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        navPanel.setBackground(new Color(240, 248, 255));

        btnProductos = crearBotonNav("📦 Productos");
        btnRecursos = crearBotonNav("🔧 Recursos");
        btnReportes = crearBotonNav("📊 Reportes");
        btnVentas = crearBotonNav("💰 Ventas");
        btnAdmin = crearBotonNav("🔐 Admin");
        JButton btnCerrarSesion = crearBotonNav("🚪 Cerrar Sesión");

        navPanel.add(btnProductos);
        navPanel.add(btnRecursos);
        navPanel.add(btnReportes);
        navPanel.add(btnVentas);
        navPanel.add(btnAdmin);
        navPanel.add(btnCerrarSesion);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(navPanel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // CONTENT
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        productosPanel = new ProductosPanel(usuario);
        recursosPanel = new RecursosPanel(usuario);
        reportesPanel = new ReportesPanel(usuario);
        ventasPanel = new VentasPanel(usuario);

        contentPanel.add(productosPanel, "productos");
        contentPanel.add(recursosPanel, "recursos");
        contentPanel.add(reportesPanel, "reportes");
        contentPanel.add(ventasPanel, "ventas");
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // EVENTOS
        btnProductos.addActionListener(e -> mostrarPanel("productos"));
        btnRecursos.addActionListener(e -> mostrarPanel("recursos"));
        btnReportes.addActionListener(e -> mostrarPanel("reportes"));
        btnVentas.addActionListener(e -> mostrarPanel("ventas"));
        btnAdmin.addActionListener(e -> mostrarPanelAdmin());
        btnCerrarSesion.addActionListener(e -> cerrarSesion());

        mostrarPanel("productos");
        add(mainPanel);
    }

    private JButton crearBotonNav(String texto) {
        JButton b = new JButton(texto);
        BotonEstilos.aplicarEstiloPersonalizado(b);
        return b;
    }

    private void configurarPermisos() {
        btnAdmin.setVisible(usuario.esAdmin());
    }

    private void mostrarPanel(String nombre) {
        cardLayout.show(contentPanel, nombre);
        switch (nombre) {
            case "productos": productosPanel.actualizarTabla(); break;
            case "recursos": recursosPanel.actualizarTabla(); break;
            case "reportes": reportesPanel.actualizarReportes(); break;
            case "ventas": ventasPanel.actualizarVentas(); break;
        }
    }

    private void mostrarPanelAdmin() {
        JOptionPane.showMessageDialog(this,
                "🔐 Panel de Administración\n\n" +
                        "• Gestionar usuarios\n" +
                        "• Respaldar base de datos\n" +
                        "• Configurar sistema\n\n" +
                        "(Próximamente)",
                "Administración", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Cerrar sesión?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }
}