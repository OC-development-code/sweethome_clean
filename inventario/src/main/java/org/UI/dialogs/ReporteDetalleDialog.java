package org.UI.dialogs;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.UI.estilo.BotonEstilos;
import org.util.FileUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Color;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.FileOutputStream;
import java.nio.file.Path;

public class ReporteDetalleDialog extends JDialog {
    private final String categoria;
    private final String contenido;
    private final String periodo;
    private JTextArea txtContenido;

    public ReporteDetalleDialog(Frame parent, String categoria, String contenido, String periodo) {
        super(parent, "📄 " + categoria + " - Reporte", true);
        this.categoria = categoria;
        this.contenido = contenido;
        this.periodo = periodo;
        initComponents();
        setSize(950, 750);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(new EmptyBorder(15, 15, 15, 15));
        main.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("📄 " + categoria + " | Período: " + periodo);
        lblTitulo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        lblTitulo.setForeground(new Color(0, 51, 102));
        lblTitulo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                new EmptyBorder(10, 15, 10, 15)));
        lblTitulo.setBackground(new Color(240, 248, 255));
        lblTitulo.setOpaque(true);
        main.add(lblTitulo, BorderLayout.NORTH);

        txtContenido = new JTextArea(contenido);
        txtContenido.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));
        txtContenido.setEditable(false);
        txtContenido.setBackground(new Color(255, 255, 240));
        txtContenido.setMargin(new Insets(20, 20, 20, 20));
        txtContenido.setCaretPosition(0);
        main.add(new JScrollPane(txtContenido), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        botones.setBackground(new Color(240, 248, 255));

        JButton btnExcel = new JButton("📋 Exportar a Excel");
        BotonEstilos.aplicarEstiloPersonalizado(btnExcel);
        btnExcel.addActionListener(e -> exportarExcel());
        botones.add(btnExcel);

        JButton btnPDF = new JButton("📄 Exportar a PDF");
        BotonEstilos.aplicarEstiloPersonalizado(btnPDF);
        btnPDF.addActionListener(e -> exportarPDF());
        botones.add(btnPDF);

        JButton btnImprimir = new JButton("🖨 Imprimir");
        BotonEstilos.aplicarEstiloPersonalizado(btnImprimir);
        btnImprimir.addActionListener(e -> imprimir());
        botones.add(btnImprimir);

        JButton btnCerrar = new JButton("✖ Cerrar");
        BotonEstilos.aplicarEstiloPersonalizado(btnCerrar);
        btnCerrar.addActionListener(e -> dispose());
        botones.add(btnCerrar);

        main.add(botones, BorderLayout.SOUTH);
        add(main);
    }

    private void exportarExcel() {
        try {
            Path dir = FileUtils.getExcelPath(categoria);
            FileUtils.asegurarDirectorio(dir);
            String nombre = FileUtils.generarNombreArchivo(categoria, ".xlsx");
            Path archivo = dir.resolve(nombre);

            try (Workbook wb = new XSSFWorkbook()) {
                Sheet sheet = wb.createSheet(categoria);
                CellStyle headerStyle = wb.createCellStyle();
                org.apache.poi.ss.usermodel.Font hf = wb.createFont();
                hf.setBold(true);
                hf.setFontHeightInPoints((short) 12);
                headerStyle.setFont(hf);
                headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                String[] lines = contenido.split("\n");
                int rowNum = 0;
                for (String line : lines) {
                    if (line.trim().isEmpty() || line.startsWith("=") || line.startsWith("-")) continue;
                    String[] parts = line.trim().split("\\s{2,}");
                    Row row = sheet.createRow(rowNum++);
                    boolean esHeader = (line.contains("ID") && line.contains("Nombre"))
                            || line.contains("Total") || line.contains("Cliente");
                    for (int i = 0; i < parts.length; i++) {
                        Cell cell = row.createCell(i);
                        try {
                            cell.setCellValue(Double.parseDouble(parts[i].replace("S/.", "").trim()));
                        } catch (NumberFormatException e) {
                            cell.setCellValue(parts[i].trim());
                        }
                        if (esHeader) cell.setCellStyle(headerStyle);
                    }
                }
                for (int i = 0; i < 6; i++) sheet.autoSizeColumn(i);
                try (FileOutputStream out = new FileOutputStream(archivo.toFile())) {
                    wb.write(out);
                }
            }

            JOptionPane.showMessageDialog(this,
                    "✅ Exportado a Excel:\n" + archivo.toAbsolutePath());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void exportarPDF() {
        try {
            Path dir = FileUtils.getPdfPath(categoria);
            FileUtils.asegurarDirectorio(dir);
            String nombre = FileUtils.generarNombreArchivo(categoria, ".pdf");
            Path archivo = dir.resolve(nombre);

            Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(doc, new FileOutputStream(archivo.toFile()));
            doc.open();

            // ✅ Usar com.itextpdf.text.Font con nombre completo
            BaseFont bf = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1252, BaseFont.EMBEDDED);
            com.itextpdf.text.Font textFont = new com.itextpdf.text.Font(bf, 10);
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(bf, 14, com.itextpdf.text.Font.BOLD);

            Paragraph titulo = new Paragraph(
                    "SWEETHOME CLEAN - REPORTE DE " + categoria.toUpperCase(), titleFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);
            doc.add(new Paragraph("Período: " + periodo, textFont));
            doc.add(new Paragraph(" "));

            for (String line : contenido.split("\n")) {
                doc.add(new Paragraph(line, textFont));
            }
            doc.close();

            JOptionPane.showMessageDialog(this,
                    "✅ Exportado a PDF:\n" + archivo.toAbsolutePath());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + e.getMessage());
        }
    }

    private void imprimir() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            // ✅ Usar java.awt.Font con nombre completo
            g2d.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 10));
            FontMetrics fm = g2d.getFontMetrics();
            String[] lines = contenido.split("\n");
            int y = 0;
            int lh = fm.getHeight();
            for (String line : lines) {
                if (y > pageFormat.getImageableHeight()) break;
                g2d.drawString(line, 0, y + fm.getAscent());
                y += lh;
            }
            return Printable.PAGE_EXISTS;
        });
        if (job.printDialog()) {
            try {
                job.print();
                JOptionPane.showMessageDialog(this, "✅ Enviado a imprimir");
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, "❌ " + e.getMessage());
            }
        }
    }
}