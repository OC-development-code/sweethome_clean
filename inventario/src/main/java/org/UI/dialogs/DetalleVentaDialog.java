package org.UI.dialogs;

import org.modelo.DetalleFactura;
import org.modelo.Factura;
import org.modelo.Usuario;
import org.servicios.FacturaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DetalleVentaDialog extends BaseDialog {
    private final FacturaService facturaService;
    private final int facturaId;
    private final Usuario usuario;
    private boolean anulada = false;

    public DetalleVentaDialog(Frame parent, FacturaService service, int facturaId, Usuario usuario) {
        super(parent, "🧾 Detalle Factura #" + facturaId, 800, 550);
        this.facturaService = service;
        this.facturaId = facturaId;
        this.usuario = usuario;
        construirUI();
    }

    private void construirUI() {
        Factura f = facturaService.obtenerPorId(facturaId);
        if (f == null) {
            JOptionPane.showMessageDialog(this, "Factura no encontrada");
            dispose();
            return;
        }

        // Info
        JPanel info = new JPanel(new GridLayout(2, 2, 10, 5));
        info.setBackground(Color.WHITE);
        info.setBorder(BorderFactory.createTitledBorder("Información"));
        info.add(new JLabel("Cliente: " + f.getNombreCliente()));
        info.add(new JLabel("Fecha: " + f.getFechaEmision()));
        info.add(new JLabel("Estado: " + f.getEstado()));
        info.add(new JLabel("Total: S/. " + String.format("%.2f", f.getTotal())));
        contentPanel.add(info, BorderLayout.NORTH);

        // Tabla
        String[] cols = {"Producto", "Cantidad", "P. Unitario", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(model);
        tabla.setRowHeight(28);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        List<DetalleFactura> detalles = facturaService.obtenerDetalles(facturaId);
        double total = 0;
        for (DetalleFactura d : detalles) {
            model.addRow(new Object[]{
                    d.getNombreProducto(), d.getCantidad(),
                    String.format("S/. %.2f", d.getPrecioUnitario()),
                    String.format("S/. %.2f", d.getSubtotal())
            });
            total += d.getSubtotal();
        }
        contentPanel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JLabel lblTotal = new JLabel("Total: S/. " + String.format("%.2f", total));
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(new Color(0, 102, 0));
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        contentPanel.add(lblTotal, BorderLayout.SOUTH);

        // Botones
        boolean puedeAnular = ("ACTIVA".equals(f.getEstado())) && usuario.puedeEliminar();
        JButton btnAnular = agregarBoton("🚫 Anular Factura", e -> anular(f));
        btnAnular.setEnabled(puedeAnular);
        if (!puedeAnular) btnAnular.setToolTipText("No permitido");

        agregarBoton("✖ Cerrar", e -> dispose());
    }

    private void anular(Factura f) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        JTextArea just = new JTextArea(3, 20);
        just.setLineWrap(true);
        JPasswordField clave = new JPasswordField(20);

        JPanel p1 = new JPanel(new BorderLayout());
        p1.add(new JLabel("Justificación:"), BorderLayout.NORTH);
        p1.add(new JScrollPane(just), BorderLayout.CENTER);

        JPanel p2 = new JPanel(new BorderLayout());
        p2.add(new JLabel("Clave:"), BorderLayout.NORTH);
        p2.add(clave, BorderLayout.CENTER);

        panel.add(p1);
        panel.add(p2);

        int r = JOptionPane.showConfirmDialog(this, panel,
                "Anular factura #" + facturaId,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        String j = just.getText().trim();
        String c = new String(clave.getPassword());

        if (j.isEmpty() || c.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ambos campos son obligatorios");
            return;
        }
        if (!c.equals(usuario.getPass())) {
            JOptionPane.showMessageDialog(this, "❌ Clave incorrecta");
            return;
        }

        if (facturaService.anularFactura(facturaId, j, usuario.getId())) {
            JOptionPane.showMessageDialog(this, "✅ Factura anulada");
            anulada = true;
            dispose();
        }
    }

    public boolean isAnulada() { return anulada; }
}