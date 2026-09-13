/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package cr.ac.ucenfotec.soft2.app;

import cr.ac.ucenfotec.soft2.ui.LoginFrame;
import cr.ac.ucenfotec.soft2.ui.UITheme;

/**
 *
 * @author Carlos Carballo Villalobos
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Aplica el look-and-feel moderno (FlatLaf) y los ajustes de UX
        // globales antes de crear cualquier ventana.
        UITheme.setup();

        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
