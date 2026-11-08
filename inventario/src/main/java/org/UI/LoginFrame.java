package org.UI;

import org.DAO.UsuarioDAO;
import org.modelo.Usuario;
import org.UI.estilo.BotonEstilos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnLogin;
    private JLabel lblError;
    private UsuarioDAO usuarioDAO;

    public LoginFrame() {
        usuarioDAO = new UsuarioDAO();
        setTitle("SweetHome Clean - Inicio de Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(new Color(240, 248, 255));

        JLabel lblTitulo = new JLabel("SweetHome Clean", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(0, 51, 102));
        mainPanel.add(lblTitulo, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblUsuario, gbc);

        gbc.gridx = 1;
        txtUsuario = new JTextField(15);
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsuario.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) txtContrasena.requestFocus();
            }
        });
        formPanel.add(txtUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblContrasena, gbc);

        gbc.gridx = 1;
        txtContrasena = new JPasswordField(15);
        txtContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtContrasena.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) btnLogin.doClick();
            }
        });
        formPanel.add(txtContrasena, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        btnLogin = new JButton("Iniciar Sesión");
        BotonEstilos.aplicarEstiloPersonalizado(btnLogin);
        btnLogin.addActionListener(e -> validarCredenciales());
        formPanel.add(btnLogin, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        lblError = new JLabel("", SwingConstants.CENTER);
        lblError.setForeground(Color.RED);
        lblError.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblError.setVisible(false);
        formPanel.add(lblError, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JLabel lblVersion = new JLabel("Versión 1.0", SwingConstants.CENTER);
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblVersion.setForeground(Color.GRAY);
        mainPanel.add(lblVersion, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void validarCredenciales() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Complete todos los campos");
            return;
        }

        Usuario valido = usuarioDAO.validarCredenciales(usuario, contrasena);
        if (valido != null) {
            lblError.setVisible(false);
            new MainFrame(valido).setVisible(true);
            dispose();
        } else {
            mostrarError("Usuario o contraseña incorrectos");
            txtUsuario.setBackground(new Color(255, 200, 200));
            txtContrasena.setBackground(new Color(255, 200, 200));
            txtUsuario.selectAll();
            txtContrasena.setText("");
            txtContrasena.requestFocus();
        }
    }

    private void mostrarError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
        Timer timer = new Timer(3000, e -> lblError.setVisible(false));
        timer.setRepeats(false);
        timer.start();
    }
}