package org.UI.dialogs;

import org.modelo.Producto;
import org.modelo.Usuario;
import org.servicios.InventarioService;

import javax.swing.*;
import java.awt.*;

public class GestionarProductoDialog extends BaseDialog {
    private final InventarioService service;
    private final Producto producto;
    private final Usuario usuario;

    private JRadioButton rbProduccion;
    private JRadioButton rbDesechar;
    private JTextField txtCantidad;
    private JTextField txtPrecio;
    private JTextArea txtJustificacion;
    private JLabel lblInfoProducto;
    private JLabel lblTotal;
    private boolean exitoso = false;

    public GestionarProductoDialog(Frame parent, InventarioService service,
                                   Producto producto, Usuario usuario) {
        super(parent, "📦 Gestionar Stock - " + producto.getNombre(), 560, 520);
        this.service = service;
        this.producto = producto;
        this.usuario = usuario;
        construirUI();
        actualizarUI();
    }

    private void construirUI() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        int y = 0;

        form.add(FormUtils.titulo("📦 Gestión de Stock"), FormUtils.gbcFull(y++));

        lblInfoProducto = FormUtils.label("");
        form.add(lblInfoProducto, FormUtils.gbcFull(y++));

        form.add(new JSeparator(), FormUtils.gbcFull(y++));

        form.add(FormUtils.subtitulo("Tipo de movimiento:"), FormUtils.gbcFull(y++));

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        radioPanel.setBackground(Color.WHITE);
        rbProduccion = new JRadioButton("🏭 Registrar Producción", true);
        rbDesechar = new JRadioButton("🗑️ Desechar");
        rbProduccion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rbDesechar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbProduccion); bg.add(rbDesechar);
        rbProduccion.addActionListener(e -> actualizarUI());
        rbDesechar.addActionListener(e -> actualizarUI());
        radioPanel.add(rbProduccion);
        radioPanel.add(rbDesechar);
        form.add(radioPanel, FormUtils.gbcFull(y++));

        form.add(FormUtils.label("Cantidad:"), FormUtils.gbc(0, y));
        txtCantidad = FormUtils.campo("1");
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { actualizarTotal(); }
        });
        form.add(txtCantidad, FormUtils.gbc(1, y++));

        form.add(FormUtils.label("Precio unitario (S/.):"), FormUtils.gbc(0, y));
        txtPrecio = FormUtils.campo(String.format("%.2f", producto.getPrecioVenta()));
        txtPrecio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { actualizarTotal(); }
        });
        form.add(txtPrecio, FormUtils.gbc(1, y++));

        form.add(FormUtils.subtitulo("Valor total:"), FormUtils.gbc(0, y));
        lblTotal = new JLabel("S/. 0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(new Color(0, 102, 0));
        form.add(lblTotal, FormUtils.gbc(1, y++));

        form.add(FormUtils.label("Justificación:"), FormUtils.gbc(0, y));
        txtJustificacion = new JTextArea(3, 20);
        form.add(FormUtils.areaConScroll(txtJustificacion, 250, 70), FormUtils.gbc(1, y++));

        contentPanel.add(form, BorderLayout.CENTER);
        agregarBoton("✅ Aceptar", e -> procesar());
        agregarBoton("✖ Cancelar", e -> dispose());
    }

    private void actualizarUI() {
        String accion = rbProduccion.isSelected() ? "PRODUCCIÓN" : "DESECHO";
        lblInfoProducto.setText(String.format(
                "📦 %s | Stock: %d | Mín: %d | Precio: S/. %.2f | Acción: %s",
                producto.getNombre(), producto.getCantidad(), producto.getStockMinimo(),
                producto.getPrecioVenta(), accion));

        txtPrecio.setEnabled(rbProduccion.isSelected());
        txtJustificacion.setEnabled(rbDesechar.isSelected());
        actualizarTotal();
    }

    private void actualizarTotal() {
        try {
            double cant = Double.parseDouble(txtCantidad.getText().trim());
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            lblTotal.setText(String.format("S/. %.2f", cant * precio));
        } catch (NumberFormatException e) {
            lblTotal.setText("S/. 0.00");
        }
    }

    private void procesar() {
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "Cantidad debe ser > 0");
                return;
            }

            if (rbProduccion.isSelected()) {
                double nuevoPrecio = Double.parseDouble(txtPrecio.getText().trim());
                if (nuevoPrecio <= 0) {
                    JOptionPane.showMessageDialog(this, "Precio debe ser > 0");
                    return;
                }
                producto.setPrecioVenta(nuevoPrecio);
                service.actualizarProducto(producto);

                // ✅ CON usuario.getId()
                if (service.ingresarProducto(producto.getId(), cantidad,
                        "Producción registrada", usuario.getId())) {
                    JOptionPane.showMessageDialog(this,
                            "✅ Producción registrada: +" + cantidad + " unidades");
                    exitoso = true;
                    dispose();
                }
            } else {
                String just = txtJustificacion.getText().trim();
                if (just.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "La justificación es obligatoria para desecho",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (cantidad > producto.getCantidad()) {
                    JOptionPane.showMessageDialog(this,
                            "Stock insuficiente. Disponible: " + producto.getCantidad());
                    return;
                }

                // ✅ CON usuario.getId()
                if (service.mermaProducto(producto.getId(), cantidad, just, usuario.getId())) {
                    JOptionPane.showMessageDialog(this,
                            "✅ Desecho registrado: -" + cantidad + " unidades");
                    exitoso = true;
                    dispose();
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Verifique los valores numéricos");
        }
    }

    public boolean isExitoso() { return exitoso; }
}