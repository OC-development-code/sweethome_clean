package org.UI.dialogs;

import org.modelo.MateriaPrima;
import org.modelo.Usuario;
import org.servicios.InventarioService;

import javax.swing.*;
import java.awt.*;

public class EliminarMateriaPrimaDialog extends BaseDialog {
    private final InventarioService service;
    private final MateriaPrima materia;
    private final Usuario usuario;

    private JTextArea txtJustificacion;
    private JPasswordField txtClave;
    private boolean exitoso = false;

    public EliminarMateriaPrimaDialog(Frame parent, InventarioService service,
                                      MateriaPrima materia, Usuario usuario) {
        super(parent, "🗑️ Eliminar Materia Prima", 500, 450);
        this.service = service;
        this.materia = materia;
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
                        "📦 %s | Stock: %d | Costo: S/. %.2f",
                        materia.getNombre(), materia.getCantidad(), materia.getCostoUnitario())),
                FormUtils.gbcFull(y++));

        form.add(new JSeparator(), FormUtils.gbcFull(y++));

        form.add(FormUtils.label("Justificación:"), FormUtils.gbc(0, y));
        txtJustificacion = new JTextArea(4, 20);
        form.add(FormUtils.areaConScroll(txtJustificacion, 250, 80), FormUtils.gbc(1, y++));

        JLabel warn = FormUtils.label("🔒 Requiere clave de administrador");
        warn.setForeground(new Color(204, 0, 0));
        form.add(warn, FormUtils.gbcFull(y++));

        form.add(FormUtils.label("Clave:"), FormUtils.gbc(0, y));
        txtClave = new JPasswordField(20);
        txtClave.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        form.add(txtClave, FormUtils.gbc(1, y));

        contentPanel.add(form, BorderLayout.CENTER);
        agregarBoton("🗑️ Eliminar", e -> eliminar());
        agregarBoton("✖ Cancelar", e -> dispose());
    }

    private void eliminar() {
        String just = txtJustificacion.getText().trim();
        String clave = new String(txtClave.getPassword());

        if (just.isEmpty() || clave.isEmpty()) {
            txtJustificacion.setBackground(just.isEmpty() ? new Color(255, 200, 200) : Color.WHITE);
            txtClave.setBackground(clave.isEmpty() ? new Color(255, 200, 200) : Color.WHITE);
            JOptionPane.showMessageDialog(this, "Ambos campos son obligatorios");
            return;
        }

        if (!clave.equals(usuario.getPass())) {
            JOptionPane.showMessageDialog(this, "❌ Clave incorrecta");
            txtClave.setText("");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar definitivamente?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (service.eliminarMateriaPrima(materia.getId())) {
            JOptionPane.showMessageDialog(this, "✅ Eliminado exitosamente");
            exitoso = true;
            dispose();
        }
    }

    public boolean isExitoso() { return exitoso; }
}