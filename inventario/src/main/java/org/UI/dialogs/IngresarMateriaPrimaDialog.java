package org.UI.dialogs;

import org.modelo.MateriaPrima;
import org.modelo.Usuario;
import org.servicios.InventarioService;

import javax.swing.*;
import java.awt.*;

public class IngresarMateriaPrimaDialog extends BaseDialog {
    private final InventarioService service;
    private final MateriaPrima materia;
    private final Usuario usuario;

    private JRadioButton rbIngresar;
    private JRadioButton rbDesechar;
    private JTextField txtCantidad;
    private JTextField txtCosto;
    private JTextArea txtJustificacion;
    private JLabel lblInfo;
    private JLabel lblTotal;
    private boolean exitoso = false;

    public IngresarMateriaPrimaDialog(Frame parent, InventarioService service,
                                      MateriaPrima materia, Usuario usuario) {
        super(parent, "📥 Gestionar - " + materia.getNombre(), 560, 520);
        this.service = service;
        this.materia = materia;
        this.usuario = usuario;
        construirUI();
        actualizarUI();
    }

    private void construirUI() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        int y = 0;

        form.add(FormUtils.titulo("📦 Gestión de Stock"), FormUtils.gbcFull(y++));

        lblInfo = FormUtils.label("");
        form.add(lblInfo, FormUtils.gbcFull(y++));
        form.add(new JSeparator(), FormUtils.gbcFull(y++));

        form.add(FormUtils.subtitulo("Tipo de movimiento:"), FormUtils.gbcFull(y++));

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        radioPanel.setBackground(Color.WHITE);
        rbIngresar = new JRadioButton("📥 Ingresar (Compra)", true);
        rbDesechar = new JRadioButton("🗑️ Desechar");
        rbIngresar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rbDesechar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbIngresar); bg.add(rbDesechar);
        rbIngresar.addActionListener(e -> actualizarUI());
        rbDesechar.addActionListener(e -> actualizarUI());
        radioPanel.add(rbIngresar);
        radioPanel.add(rbDesechar);
        form.add(radioPanel, FormUtils.gbcFull(y++));

        form.add(FormUtils.label("Cantidad:"), FormUtils.gbc(0, y));
        txtCantidad = FormUtils.campo("1");
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { actualizarTotal(); }
        });
        form.add(txtCantidad, FormUtils.gbc(1, y++));

        form.add(FormUtils.label("Costo unitario (S/.):"), FormUtils.gbc(0, y));
        txtCosto = FormUtils.campo(String.format("%.2f", materia.getCostoUnitario()));
        txtCosto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { actualizarTotal(); }
        });
        form.add(txtCosto, FormUtils.gbc(1, y++));

        form.add(FormUtils.subtitulo("💰 Total:"), FormUtils.gbc(0, y));
        lblTotal = new JLabel("S/. 0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(new Color(0, 102, 0));
        form.add(lblTotal, FormUtils.gbc(1, y++));

        form.add(FormUtils.label("Justificación:"), FormUtils.gbc(0, y));
        txtJustificacion = new JTextArea(3, 20);
        form.add(FormUtils.areaConScroll(txtJustificacion, 250, 70), FormUtils.gbc(1, y));

        contentPanel.add(form, BorderLayout.CENTER);
        agregarBoton("✅ Aceptar", e -> procesar());
        agregarBoton("✖ Cancelar", e -> dispose());
    }

    private void actualizarUI() {
        String accion = rbIngresar.isSelected() ? "INGRESO" : "DESECHO";
        lblInfo.setText(String.format(
                "📦 %s | Stock: %d | Costo: S/. %.2f | Acción: %s",
                materia.getNombre(), materia.getCantidad(),
                materia.getCostoUnitario(), accion));
        txtCosto.setEnabled(rbIngresar.isSelected());
        actualizarTotal();
    }

    private void actualizarTotal() {
        try {
            double c = Double.parseDouble(txtCantidad.getText().trim());
            double p = Double.parseDouble(txtCosto.getText().trim());
            lblTotal.setText(String.format("S/. %.2f", c * p));
        } catch (NumberFormatException e) {
            lblTotal.setText("S/. 0.00");
        }
    }

    private void procesar() {
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            String just = txtJustificacion.getText().trim();

            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "Cantidad debe ser > 0");
                return;
            }

            if (rbIngresar.isSelected()) {
                double nuevoCosto = Double.parseDouble(txtCosto.getText().trim());
                if (nuevoCosto <= 0) {
                    JOptionPane.showMessageDialog(this, "Costo debe ser > 0");
                    return;
                }
                materia.setCostoUnitario(nuevoCosto);
                service.actualizarMateriaPrima(materia);

                if (service.comprarMateriaPrima(materia.getId(), cantidad,
                        just.isEmpty() ? "Compra" : just, usuario.getId())) {
                    JOptionPane.showMessageDialog(this, "✅ Compra registrada");
                    exitoso = true;
                    dispose();
                }
            } else {
                if (just.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Justificación obligatoria");
                    return;
                }
                if (cantidad > materia.getCantidad()) {
                    JOptionPane.showMessageDialog(this, "Stock insuficiente");
                    return;
                }
                if (service.mermaMateriaPrima(materia.getId(), cantidad, just, usuario.getId())) {
                    JOptionPane.showMessageDialog(this, "✅ Desecho registrado");
                    exitoso = true;
                    dispose();
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Verifique los valores numéricos");
        }
    }

    public boolean isExitoso() { return exitoso; }
}