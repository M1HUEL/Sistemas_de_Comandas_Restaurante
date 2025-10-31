package com.itson.sistema_de_comandas_restaurante.gui;

import com.itson.sistema_de_comandas_restaurante.gui.controlador.ClientesControlador;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ClientesPantalla extends JFrame {

    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private ClientesControlador controlador;

    public ClientesPantalla() {
        setTitle("Clientes Frecuentes");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String[] columnas = {"ID", "Nombre", "Teléfono", "Correo", "Visitas", "Total Gastado", "Puntos"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaClientes = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaClientes);
        scroll.setBorder(BorderFactory.createTitledBorder("Clientes Frecuentes"));
        add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        btnPanel.add(btnAgregar);
        btnPanel.add(btnEditar);
        btnPanel.add(btnEliminar);
        add(btnPanel, BorderLayout.NORTH);

        controlador = new ClientesControlador(tablaClientes, modeloTabla);
        controlador.inicializar();

        btnAgregar.addActionListener(e -> controlador.agregarCliente());
        btnEditar.addActionListener(e -> controlador.editarCliente());
        btnEliminar.addActionListener(e -> controlador.eliminarCliente());

        setVisible(true);
    }
}
