package com.itson.sistema_de_comandas_restaurante;

import com.itson.sistema_de_comandas_restaurante.gui.Inicio;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Sistema_de_Comandas_Restaurante {
    
    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            new Inicio().setVisible(true);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            System.getLogger(Sistema_de_Comandas_Restaurante.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
    }
}
