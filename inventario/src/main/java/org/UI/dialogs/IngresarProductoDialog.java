package org.UI.dialogs;

import org.modelo.Producto;
import org.modelo.Usuario;
import org.servicios.InventarioService;
import org.UI.estilo.BotonEstilos; // ← NUEVA IMPORTACIÓN

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class IngresarProductoDialog extends JDialog {
    private InventarioService service;
    private Producto producto;
    private JTextField txtCantidad;
    private JTextField txtJustificacion;
    private JLabel lblInfoProducto;
    private JButton btnIngresar;
    private JButton btnCancelar;
    private boolean exitoso = false;
    private Usuario usuario;

    public IngresarProductoDialog(Frame parent, InventarioService service, Producto producto, Usuario usuario) {
        super(parent, "Ingresar Producto - " + producto.getNombre(), true);
        this.service = service;
        this.producto = producto;
        this.usuario = usuario;
        initComponents();
        setSize(500, 400);
        setLocationRelativeTo(parent);
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

        // Información del producto
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        lblInfoProducto = new JLabel();
        lblInfoProducto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        actualizarInfoProducto();
        formPanel.add(lblInfoProducto, gbc);
        row++;
        gbc.gridwidth = 1;

        // Separador
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(new JSeparator(), gbc);
        row++;
        gbc.gridwidth = 1;

        // Cantidad a ingresar
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblCantidad = new JLabel("Cantidad a ingresar:");
        lblCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblCantidad, gbc);

        gbc.gridx = 1;
        txtCantidad = new JTextField("1", 20);
        txtCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtCantidad, gbc);
        row++;

        // Justificación
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblJustificacion = new JLabel("Justificación:");
        lblJustificacion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblJustificacion, gbc);

        gbc.gridx = 1;
        txtJustificacion = new JTextField(20);
        txtJustificacion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtJustificacion, gbc);
        row++;

        // Botones CON ESTILOS
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        btnIngresar = new JButton("✅ Ingresar Producto");
        BotonEstilos.aplicarEstiloPersonalizado(btnIngresar);
        btnIngresar.addActionListener(e -> ingresarProducto());
        buttonPanel.add(btnIngresar);

        btnCancelar = new JButton("❌ Cancelar");
        BotonEstilos.aplicarEstiloPersonalizado(btnCancelar);
        btnCancelar.addActionListener(e -> dispose());
        buttonPanel.add(btnCancelar);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void actualizarInfoProducto() {
        if (producto != null) {
            lblInfoProducto.setText(String.format(
                    "📦 %s\nStock actual: %d | Stock mínimo: %d",
                    producto.getNombre(),
                    producto.getCantidad(),
                    producto.getStockMinimo()
            ));
        }
    }

    private void ingresarProducto() {
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            String justificacion = txtJustificacion.getText().trim();

            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this,
                        "La cantidad debe ser mayor a cero",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (justificacion.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "La justificación es obligatoria",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (service.ingresarProducto(producto.getId(), cantidad, "Producción registrada", usuario.getId())) {
                JOptionPane.showMessageDialog(this,
                        "✅ Producto ingresado exitosamente\n" +
                                "Stock actualizado: " + (producto.getCantidad() + cantidad),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                exitoso = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al ingresar el producto",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Cantidad inválida. Ingrese un número",
                    "Error de formato",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isExitoso() {
        return exitoso;
    }
}