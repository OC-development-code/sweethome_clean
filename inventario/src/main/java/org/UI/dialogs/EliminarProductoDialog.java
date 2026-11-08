package org.UI.dialogs;

import org.modelo.Producto;
import org.modelo.Usuario;
import org.servicios.InventarioService;

import javax.swing.*;
import java.awt.*;

public class EliminarProductoDialog extends BaseDialog {
    private final InventarioService service;
    private final Producto producto;
    private final Usuario usuario;

    private JTextArea txtJustificacion;
    private boolean exitoso = false;

    public EliminarProductoDialog(Frame parent, InventarioService service,
                                  Producto producto, Usuario usuario) {
        super(parent, "🗑️ Eliminar Producto", 500, 400);
        this.service = service;
        this.producto = producto;
        this.usuario = usuario;
        construirUI();
    }

    private void construirUI() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        int y = 0;

        JLabel titulo = FormUtils.titulo("⚠️ ELIMINAR REGISTRO");
        titulo.setForeground(new Color(204, 0, 0));
        form.add(titulo, FormUtils.gbcFull(y++));

        form.add(FormUtils.label(String.format(
                        "📦 %s | Stock: %d | Precio: S/. %.2f",
                        producto.getNombre(), producto.getCantidad(), producto.getPrecioVenta())),
                FormUtils.gbcFull(y++));

        form.add(new JSeparator(), FormUtils.gbcFull(y++));

        form.add(FormUtils.label("Justificación (obligatoria):"), FormUtils.gbc(0, y));
        txtJustificacion = new JTextArea(4, 20);
        form.add(FormUtils.areaConScroll(txtJustificacion, 250, 80), FormUtils.gbc(1, y++));

        JLabel warn = FormUtils.label("🚨 Esta acción NO se puede deshacer");
        warn.setForeground(new Color(204, 0, 0));
        form.add(warn, FormUtils.gbcFull(y));

        contentPanel.add(form, BorderLayout.CENTER);
        agregarBoton("🗑️ Eliminar", e -> eliminar());
        agregarBoton("✖ Cancelar", e -> dispose());
    }

    private void eliminar() {
        String just = txtJustificacion.getText().trim();
        if (just.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe proporcionar una justificación",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar este producto?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (service.eliminarProducto(producto.getId())) {
            JOptionPane.showMessageDialog(this, "✅ Producto eliminado");
            exitoso = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Error al eliminar");
        }
    }

    public boolean isExitoso() { return exitoso; }
}