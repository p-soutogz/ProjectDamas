/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Damas;

/**
 *
 * @author pablo
 */
public class Main {

    /**
     * @param args the command line arguments
     */
        public static void main(String[] args) {
    /* Configurar el estilo de la ventana (opcional) */
    try {
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                javax.swing.UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
    } catch (Exception ex) {
        System.err.println("No se pudo establecer el look and feel.");
    }

    /* Crear e iniciar la GUI */
    java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
            // Crear e inicializar la GUI
            DamasGUI gui = new DamasGUI();
            gui.actualizarStatusLabel();
            gui.actualizarLabels();
            gui.setVisible(true);
        }
    });
    
}
}
