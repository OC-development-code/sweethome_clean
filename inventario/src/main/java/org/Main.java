package org;

import org.DAO.InicializadorBD;
import org.UI.LoginFrame;
import org.util.FileUtils;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Crear directorios para reportes
        try {
            FileUtils.crearDirectorios();
        } catch (Exception e) {
            System.err.println("⚠️ Error al crear directorios: " + e.getMessage());
        }

        // Inicializar BD
        InicializadorBD.inicializarBaseDatos();
        InicializadorBD.actualizarRelaciones();

        // Lanzar UI
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginFrame().setVisible(true);
        });
    }
}