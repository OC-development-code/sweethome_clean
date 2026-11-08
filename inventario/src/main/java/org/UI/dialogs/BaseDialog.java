package org.UI.dialogs;

import org.UI.estilo.BotonEstilos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Clase base para todos los diálogos del sistema.
 * Proporciona estructura común: título, panel central, panel de botones.
 * Los diálogos hijos solo implementan construirContenido() y definir botones.
 */
public abstract class BaseDialog extends JDialog {

    protected JPanel mainPanel;
    protected JPanel contentPanel;
    protected JPanel buttonsPanel;

    public BaseDialog(Frame parent, String titulo, int ancho, int alto) {
        super(parent, titulo, true);
        setSize(ancho, alto);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        initBase();
    }

    private void initBase() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonsPanel.setBackground(Color.WHITE);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /** Añade un botón con estilo al panel inferior. */
    protected JButton agregarBoton(String texto, java.awt.event.ActionListener accion) {
        JButton b = new JButton(texto);
        BotonEstilos.aplicarEstiloPersonalizado(b);
        b.addActionListener(accion);
        buttonsPanel.add(b);
        return b;
    }

    /** Crea un label con estilo uniforme. */
    protected JLabel label(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return l;
    }

    /** Crea un campo de texto con estilo uniforme. */
    protected JTextField campoTexto(String valorInicial) {
        JTextField f = new JTextField(valorInicial, 20);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return f;
    }

    /** Crea un text area con scroll uniforme. */
    protected JScrollPane areaTexto(JTextArea area) {
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(250, 80));
        return sp;
    }
}