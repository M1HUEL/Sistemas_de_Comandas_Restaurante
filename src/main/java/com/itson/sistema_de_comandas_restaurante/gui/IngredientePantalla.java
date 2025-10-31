package com.itson.sistema_de_comandas_restaurante.gui;

import com.itson.sistema_de_comandas_restaurante.gui.controlador.IngredienteControlador;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class IngredientePantalla extends JFrame {

    private JTable tablaIngredientes;
    private DefaultTableModel modeloTabla;
    private IngredienteControlador controlador;

    public IngredientePantalla() {
        setTitle("Ingredientes");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Unidad", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaIngredientes = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaIngredientes);
        scroll.setBorder(BorderFactory.createTitledBorder("Ingredientes"));
        add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        btnPanel.add(btnAgregar);
        btnPanel.add(btnEditar);
        btnPanel.add(btnEliminar);
        add(btnPanel, BorderLayout.NORTH);

        controlador = new IngredienteControlador(tablaIngredientes, modeloTabla, btnPanel);
        controlador.inicializar();

        btnAgregar.addActionListener(e -> controlador.agregarIngrediente());
        btnEditar.addActionListener(e -> controlador.editarIngrediente());
        btnEliminar.addActionListener(e -> controlador.eliminarIngrediente());

        setVisible(true);
    }
}
