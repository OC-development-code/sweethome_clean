package org.UI.dialogs;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Utilidades para construir formularios con GridBagLayout de manera consistente.
 */
public final class FormUtils {

    private FormUtils() {}

    public static GridBagConstraints gbc(int x, int y) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = x;
        g.gridy = y;
        return g;
    }

    public static GridBagConstraints gbcFull(int y) {
        GridBagConstraints g = gbc(0, y);
        g.gridwidth = 2;
        return g;
    }

    public static JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 18));
        l.setForeground(new Color(0, 51, 102));
        return l;
    }

    public static JLabel subtitulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return l;
    }

    public static JLabel label(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return l;
    }

    public static JTextField campo(String valor) {
        JTextField f = new JTextField(valor, 20);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return f;
    }

    public static JComboBox<String> combo(String[] items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return c;
    }

    public static JScrollPane areaConScroll(JTextArea area, int ancho, int alto) {
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(ancho, alto));
        return sp;
    }

    public static JPanel panelConTitulo(String titulo, LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                titulo,
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)));
        return p;
    }

    public static void aplicarPadding(JPanel panel, int top, int left, int bottom, int right) {
        panel.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
    }
}