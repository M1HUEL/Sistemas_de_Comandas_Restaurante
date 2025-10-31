package com.itson.sistema_de_comandas_restaurante.gui;

import com.itson.sistema_de_comandas_restaurante.gui.controlador.ComandasControlador;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ComandasPantalla extends JFrame {

    private JTable tablaComandas;
    private JTable tablaProductos;
    private DefaultTableModel modeloComandas;
    private DefaultTableModel modeloProductos;
    private JLabel lblTotal;
    private ComandasControlador controlador;

    public ComandasPantalla() {
        setTitle("Sistema de Comandas - Comandas");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());

        modeloComandas = new DefaultTableModel(
                new String[]{"ID", "Folio", "Mesa", "Cliente", "Estado", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaComandas = new JTable(modeloComandas);
        JScrollPane scrollComandas = new JScrollPane(tablaComandas);
        scrollComandas.setBorder(BorderFactory.createTitledBorder("Comandas"));
        mainPanel.add(scrollComandas, BorderLayout.NORTH);

        JPanel detallePanel = new JPanel(new BorderLayout());
        detallePanel.setBorder(BorderFactory.createTitledBorder("Detalle de Comanda"));

        modeloProductos = new DefaultTableModel(
                new String[]{"Producto", "Cantidad", "Precio Unitario", "Subtotal"}, 0);
        tablaProductos = new JTable(modeloProductos);
        JScrollPane scrollProductos = new JScrollPane(tablaProductos);
        detallePanel.add(scrollProductos, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Total: $0.0");
        JButton btnAgregarProducto = new JButton("Agregar Producto");
        JButton btnGuardar = new JButton("Guardar Comanda");
        JButton btnActualizarComanda = new JButton("Actualizar Comanda");
        JButton btnEliminarComanda = new JButton("Eliminar Comanda"); // Nuevo botón
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnMarcarEntregada = new JButton("Marcar Entregada");

        bottomPanel.add(lblTotal);
        bottomPanel.add(btnAgregarProducto);
        bottomPanel.add(btnGuardar);
        bottomPanel.add(btnActualizarComanda);
        bottomPanel.add(btnEliminarComanda);
        bottomPanel.add(btnCancelar);
        bottomPanel.add(btnMarcarEntregada);

        detallePanel.add(bottomPanel, BorderLayout.SOUTH);
        mainPanel.add(detallePanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        controlador = new ComandasControlador(tablaComandas, tablaProductos,
                modeloComandas, modeloProductos, lblTotal);
        controlador.inicializar();

        btnAgregarProducto.addActionListener(e -> controlador.agregarProducto());
        btnGuardar.addActionListener(e -> controlador.guardarComanda());
        btnActualizarComanda.addActionListener(e -> controlador.actualizarComanda());
        btnEliminarComanda.addActionListener(e -> controlador.eliminarComanda());
        btnCancelar.addActionListener(e -> controlador.cancelarComanda());
        btnMarcarEntregada.addActionListener(e -> controlador.marcarEntregada());

        setVisible(true);
    }
}
