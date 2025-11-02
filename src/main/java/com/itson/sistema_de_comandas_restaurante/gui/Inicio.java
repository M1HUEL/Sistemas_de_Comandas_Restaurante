package com.itson.sistema_de_comandas_restaurante.gui;

import com.itson.sistema_de_comandas_restaurante.gui.controlador.InicioControlador;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Inicio extends JFrame {

    private JTable tablaComandas;
    private DefaultTableModel modeloTabla;
    private InicioControlador controlador;

    public Inicio() {
        setTitle("Sistema de Comandas - Inicio");
        setSize(1440, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        modeloTabla = new DefaultTableModel(
                new String[]{"Folio", "Mesa", "Cliente", "Estado", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaComandas = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaComandas);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Comandas Recientes"));

        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        JPanel sideMenu = new JPanel(new GridLayout(7, 1, 5, 5));
        sideMenu.setPreferredSize(new Dimension(200, 0));

        JButton btnInicio = new JButton("Inicio");
        JButton btnComandas = new JButton("Comandas");
        JButton btnProductos = new JButton("Productos");
        JButton btnIngredientes = new JButton("Ingredientes");
        JButton btnClientes = new JButton("Clientes Frecuentes");
        JButton btnReportes = new JButton("Reportes");
        JButton btnSalir = new JButton("Salir");

        sideMenu.add(btnInicio);
        sideMenu.add(btnComandas);
        sideMenu.add(btnProductos);
        sideMenu.add(btnIngredientes);
        sideMenu.add(btnClientes);
        sideMenu.add(btnReportes);
        sideMenu.add(btnSalir);

        add(sideMenu, BorderLayout.WEST);
        add(panelPrincipal, BorderLayout.CENTER);

        controlador = new InicioControlador(modeloTabla);
        controlador.inicializar();

        btnInicio.addActionListener(e -> this.toFront());
        btnComandas.addActionListener(e -> new ComandasPantalla());
        btnProductos.addActionListener(e -> new ProductoPantalla());
        btnIngredientes.addActionListener(e -> new IngredientePantalla());
        btnClientes.addActionListener(e -> new ClientesPantalla());
        btnReportes.addActionListener(e -> new ReportesPantalla());
        btnSalir.addActionListener(e -> System.exit(0));

        setVisible(true);
    }
}
