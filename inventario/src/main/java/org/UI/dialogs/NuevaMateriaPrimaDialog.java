package org.UI.dialogs;

import org.modelo.MateriaPrima;
import org.modelo.Usuario;
import org.servicios.InventarioService;
import org.UI.estilo.BotonEstilos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class NuevaMateriaPrimaDialog extends JDialog {
    private InventarioService service;
    private Usuario usuario;

    private JTextField txtNombre;
    private JTextField txtCategoria;
    private JTextField txtCantidad;
    private JTextField txtStockMinimo;
    private JTextField txtCostoUnitario;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private boolean exitoso = false;

    public NuevaMateriaPrimaDialog(Frame parent, InventarioService service, Usuario usuario) {
        super(parent, "➕ Nueva Materia Prima", true);
        this.service = service;
        this.usuario = usuario;
        initComponents();
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Título
        JLabel lblTitulo = new JLabel("🧪 Nueva Materia Prima");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(0, 51, 102));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(lblTitulo, gbc);
        row++;
        gbc.gridwidth = 1;

        // Nombre
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblNombre, gbc);

        gbc.gridx = 1;
        txtNombre = new JTextField(20);
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtNombre, gbc);
        row++;

        // Categoría
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblCategoria = new JLabel("Categoría:");
        lblCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblCategoria, gbc);

        gbc.gridx = 1;
        txtCategoria = new JTextField(20);
        txtCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtCategoria, gbc);
        row++;

        // Cantidad inicial
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblCantidad = new JLabel("Cantidad inicial:");
        lblCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblCantidad, gbc);

        gbc.gridx = 1;
        txtCantidad = new JTextField("0", 20);
        txtCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtCantidad, gbc);
        row++;

        // Stock mínimo
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblStock = new JLabel("Stock mínimo:");
        lblStock.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblStock, gbc);

        gbc.gridx = 1;
        txtStockMinimo = new JTextField("5", 20);
        txtStockMinimo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtStockMinimo, gbc);
        row++;

        // Costo unitario
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblCosto = new JLabel("Costo unitario (S/.):");
        lblCosto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblCosto, gbc);

        gbc.gridx = 1;
        txtCostoUnitario = new JTextField("0.00", 20);
        txtCostoUnitario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtCostoUnitario, gbc);
        row++;

        // Separador
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(new JSeparator(), gbc);
        row++;
        gbc.gridwidth = 1;

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        btnGuardar = new JButton("💾 Guardar");
        BotonEstilos.aplicarEstiloPersonalizado(btnGuardar);
        btnGuardar.addActionListener(e -> guardarMateriaPrima());
        buttonPanel.add(btnGuardar);

        btnCancelar = new JButton("✖ Cancelar");
        BotonEstilos.aplicarEstiloPersonalizado(btnCancelar);
        btnCancelar.addActionListener(e -> dispose());
        buttonPanel.add(btnCancelar);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void guardarMateriaPrima() {
        try {
            String nombre = txtNombre.getText().trim();
            String categoria = txtCategoria.getText().trim();
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            int stockMinimo = Integer.parseInt(txtStockMinimo.getText().trim());
            double costoUnitario = Double.parseDouble(txtCostoUnitario.getText().trim());

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (cantidad < 0) {
                JOptionPane.showMessageDialog(this, "La cantidad no puede ser negativa", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (costoUnitario < 0) {
                JOptionPane.showMessageDialog(this, "El costo unitario no puede ser negativo", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            MateriaPrima materia = new MateriaPrima(nombre, categoria, cantidad, stockMinimo, costoUnitario);

            MateriaPrima m = null;
            if (service.crearMateriaPrima(m, usuario.getId())) {
                JOptionPane.showMessageDialog(this,
                        "✅ Materia prima creada exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                exitoso = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "❌ Error al crear la materia prima",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, verifique los campos numéricos",
                    "Error de formato",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isExitoso() {
        return exitoso;
    }
}