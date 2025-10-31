package com.itson.sistema_de_comandas_restaurante.gui;

import com.itson.sistema_de_comandas_restaurante.gui.controlador.ProductoControlador;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ProductoPantalla extends JFrame {

    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private ProductoControlador controlador;

    public ProductoPantalla() {
        setTitle("Productos");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String[] columnas = {"ID", "Nombre", "Precio", "Tipo", "Disponible"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProductos = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaProductos);
        scroll.setBorder(BorderFactory.createTitledBorder("Productos"));
        add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        btnPanel.add(btnAgregar);
        btnPanel.add(btnEditar);
        btnPanel.add(btnEliminar);
        add(btnPanel, BorderLayout.NORTH);

        controlador = new ProductoControlador(tablaProductos, modeloTabla);

        btnAgregar.addActionListener(e -> controlador.agregarProducto());
        btnEditar.addActionListener(e -> controlador.editarProducto());
        btnEliminar.addActionListener(e -> controlador.eliminarProducto());

        setVisible(true);
    }
}
