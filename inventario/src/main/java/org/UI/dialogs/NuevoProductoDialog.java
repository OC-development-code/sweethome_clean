package org.UI.dialogs;

import org.modelo.Producto;
import org.modelo.Usuario;
import org.servicios.InventarioService;

import javax.swing.*;
import java.awt.*;

public class NuevoProductoDialog extends BaseDialog {
    private final InventarioService service;
    private final Usuario usuario;

    private JTextField txtNombre;
    private JTextField txtCategoria;
    private JTextField txtCantidad;
    private JTextField txtStockMinimo;
    private JTextField txtPrecio;
    private boolean exitoso = false;

    public NuevoProductoDialog(Frame parent, InventarioService service, Usuario usuario) {
        super(parent, "➕ Nuevo Producto", 500, 450);
        this.service = service;
        this.usuario = usuario;
        construirUI();
    }

    private void construirUI() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);

        int y = 0;
        form.add(FormUtils.titulo("📦 Datos del Producto"), FormUtils.gbcFull(y++));

        form.add(FormUtils.label("Nombre:"), FormUtils.gbc(0, y));
        txtNombre = FormUtils.campo("");
        form.add(txtNombre, FormUtils.gbc(1, y++));

        form.add(FormUtils.label("Categoría:"), FormUtils.gbc(0, y));
        txtCategoria = FormUtils.campo("");
        form.add(txtCategoria, FormUtils.gbc(1, y++));

        form.add(FormUtils.label("Cantidad inicial:"), FormUtils.gbc(0, y));
        txtCantidad = FormUtils.campo("0");
        form.add(txtCantidad, FormUtils.gbc(1, y++));

        form.add(FormUtils.label("Stock mínimo:"), FormUtils.gbc(0, y));
        txtStockMinimo = FormUtils.campo("5");
        form.add(txtStockMinimo, FormUtils.gbc(1, y++));

        form.add(FormUtils.label("Precio de venta (S/.):"), FormUtils.gbc(0, y));
        txtPrecio = FormUtils.campo("0.00");
        form.add(txtPrecio, FormUtils.gbc(1, y++));

        contentPanel.add(form, BorderLayout.CENTER);
        agregarBoton("💾 Guardar", e -> guardar());
        agregarBoton("✖ Cancelar", e -> dispose());
    }

    private void guardar() {
        try {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Producto p = new Producto(
                    nombre,
                    txtCategoria.getText().trim(),
                    Integer.parseInt(txtCantidad.getText().trim()),
                    Integer.parseInt(txtStockMinimo.getText().trim()),
                    Double.parseDouble(txtPrecio.getText().trim()));

            if (service.crearProducto(p, usuario.getId())) {
                JOptionPane.showMessageDialog(this,
                        "✅ Producto creado exitosamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                exitoso = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "❌ Error al crear el producto",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Verifique los campos numéricos",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isExitoso() { return exitoso; }
}