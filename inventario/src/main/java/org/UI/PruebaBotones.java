package org.UI;

import org.UI.estilo.BotonEstilos;

import javax.swing.*;
import java.awt.*;

public class PruebaBotones extends JFrame {
    public PruebaBotones() {
        setTitle("Prueba de Estilos - Botones");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(245, 248, 255));

        String[] textos = {"💾 Guardar", "✖ Cancelar", "📊 Reporte",
                "🗑 Eliminar", "🖨 Imprimir", "❓ Ayuda"};

        for (String t : textos) {
            JButton b = new JButton(t);
            BotonEstilos.aplicarEstiloPersonalizado(b);
            panel.add(b);
        }

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new PruebaBotones().setVisible(true);
        });
    }
}