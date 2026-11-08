package org.UI.estilo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BotonEstilos {

    private static final String FUENTE_PRINCIPAL = "Segoe UI";
    private static final int TAMANO_BASE = 14;
    private static final int TAMANO_AUMENTADO = (int) (TAMANO_BASE * 1.1);

    private static final Color COLOR_FONDO_NORMAL = new Color(240, 248, 255);
    private static final Color COLOR_TEXTO_NORMAL = new Color(0, 51, 102);
    private static final Color COLOR_BORDE_NORMAL = new Color(70, 130, 180);

    private static final Color COLOR_FONDO_PRESIONADO = new Color(20, 40, 80);
    private static final Color COLOR_TEXTO_PRESIONADO = new Color(173, 216, 230);
    private static final Color COLOR_BORDE_PRESIONADO = new Color(100, 149, 237);

    private static final Color COLOR_FONDO_HOVER = new Color(220, 240, 255);
    private static final Color COLOR_TEXTO_HOVER = new Color(0, 70, 140);

    public static void aplicarEstiloPersonalizado(JButton boton) {
        String fuente = obtenerFuenteDisponible();
        boton.setFont(new Font(fuente, Font.PLAIN, TAMANO_AUMENTADO));
        boton.setBackground(COLOR_FONDO_NORMAL);
        boton.setForeground(COLOR_TEXTO_NORMAL);
        boton.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_NORMAL, 2, true));
        boton.setMargin(new Insets(8, 16, 8, 16));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (!boton.getModel().isPressed() && boton.isEnabled()) {
                    boton.setBackground(COLOR_FONDO_HOVER);
                    boton.setForeground(COLOR_TEXTO_HOVER);
                    boton.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_NORMAL, 3, true));
                }
            }
            @Override public void mouseExited(MouseEvent e) {
                if (!boton.getModel().isPressed() && boton.isEnabled()) {
                    boton.setBackground(COLOR_FONDO_NORMAL);
                    boton.setForeground(COLOR_TEXTO_NORMAL);
                    boton.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_NORMAL, 2, true));
                }
            }
            @Override public void mousePressed(MouseEvent e) {
                if (boton.isEnabled()) {
                    boton.setBackground(COLOR_FONDO_PRESIONADO);
                    boton.setForeground(COLOR_TEXTO_PRESIONADO);
                    boton.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_PRESIONADO, 3, true));
                }
            }
            @Override public void mouseReleased(MouseEvent e) {
                if (!boton.isEnabled()) return;
                if (boton.contains(e.getPoint())) {
                    boton.setBackground(COLOR_FONDO_HOVER);
                    boton.setForeground(COLOR_TEXTO_HOVER);
                    boton.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_NORMAL, 3, true));
                } else {
                    boton.setBackground(COLOR_FONDO_NORMAL);
                    boton.setForeground(COLOR_TEXTO_NORMAL);
                    boton.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_NORMAL, 2, true));
                }
            }
        });
    }

    public static void aplicarEstiloConTamaño(JButton boton, int ancho, int alto) {
        aplicarEstiloPersonalizado(boton);
        boton.setPreferredSize(new Dimension(ancho, alto));
    }

    public static void aplicarEstiloCompleto(JButton boton, String texto, String tooltip) {
        aplicarEstiloPersonalizado(boton);
        if (texto != null && !texto.isEmpty()) boton.setText(texto);
        if (tooltip != null && !tooltip.isEmpty()) boton.setToolTipText(tooltip);
    }

    public static void estandarizarBotones(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JButton) {
                JButton b = (JButton) comp;
                b.setPreferredSize(new Dimension(150, 45));
                b.setMinimumSize(new Dimension(120, 40));
            }
        }
    }

    private static String obtenerFuenteDisponible() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fuentes = ge.getAvailableFontFamilyNames();
        String[] preferidas = {FUENTE_PRINCIPAL, "Roboto", "Arial", "Tahoma"};
        for (String pref : preferidas) {
            for (String disp : fuentes) {
                if (disp.equalsIgnoreCase(pref)) return pref;
            }
        }
        return "Dialog";
    }
}