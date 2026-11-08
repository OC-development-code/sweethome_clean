package org.UI.dialogs;

import org.modelo.DetalleFactura;
import org.modelo.Factura;
import org.UI.estilo.BotonEstilos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;

public class FacturaPreviewDialog extends BaseDialog {
    private final Factura factura;
    private final List<DetalleFactura> detalles;
    private JTextArea txtFactura;
    private boolean impreso = false;

    public FacturaPreviewDialog(Frame parent, Factura factura, List<DetalleFactura> detalles) {
        super(parent, "🧾 Vista Previa - Factura #" + factura.getId(), 650, 750);
        this.factura = factura;
        this.detalles = detalles;
        construirUI();
        cargarFactura();
    }

    private void construirUI() {
        JLabel lblTitulo = new JLabel("🧾 Factura #" + factura.getId(), SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(0, 51, 102));
        contentPanel.add(lblTitulo, BorderLayout.NORTH);

        txtFactura = new JTextArea();
        txtFactura.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtFactura.setEditable(false);
        txtFactura.setBackground(new Color(255, 255, 240));
        txtFactura.setMargin(new Insets(20, 20, 20, 20));
        JScrollPane scroll = new JScrollPane(txtFactura);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 1));
        contentPanel.add(scroll, BorderLayout.CENTER);

        agregarBoton("🖨 Imprimir", e -> imprimir());
        agregarBoton("✖ Cancelar", e -> dispose());
    }

    private void cargarFactura() {
        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        sb.append("              SWEETHOME CLEAN\n");
        sb.append("              FACTURA #").append(factura.getId()).append("\n");
        sb.append("==================================================\n");
        sb.append("Fecha:   ").append(factura.getFechaEmision()).append("\n");
        sb.append("Cliente: ").append(factura.getNombreCliente()).append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("%-5s %-22s %-9s %-10s\n",
                "Cant", "Producto", "P.Unit", "Subtotal"));
        sb.append("--------------------------------------------------\n");

        double total = 0;
        for (DetalleFactura d : detalles) {
            String nombre = d.getNombreProducto();
            if (nombre.length() > 22) nombre = nombre.substring(0, 19) + "...";
            sb.append(String.format("%-5d %-22s S/%-7.2f S/%9.2f\n",
                    d.getCantidad(), nombre, d.getPrecioUnitario(), d.getSubtotal()));
            total += d.getSubtotal();
        }

        sb.append("--------------------------------------------------\n");
        sb.append(String.format("TOTAL A PAGAR: S/. %.2f\n", total));
        sb.append("==================================================\n");
        sb.append("Gracias por su compra.\n");
        sb.append("==================================================");

        txtFactura.setText(sb.toString());
        txtFactura.setCaretPosition(0);
    }

    private void imprimir() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));
            FontMetrics fm = g2d.getFontMetrics();
            String[] lines = txtFactura.getText().split("\n");
            int y = 0;
            int lh = fm.getHeight();
            for (String line : lines) {
                g2d.drawString(line, 0, y + fm.getAscent());
                y += lh;
            }
            return Printable.PAGE_EXISTS;
        });

        if (job.printDialog()) {
            try {
                job.print();
                impreso = true;
                JOptionPane.showMessageDialog(this, "✅ Factura impresa");
                dispose();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, "❌ " + e.getMessage());
            }
        }
    }

    public boolean isImpreso() { return impreso; }
}