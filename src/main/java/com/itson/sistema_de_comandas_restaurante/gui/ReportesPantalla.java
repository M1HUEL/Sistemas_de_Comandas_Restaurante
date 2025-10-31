package com.itson.sistema_de_comandas_restaurante.gui;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.itson.sistema_de_comandas_restaurante.gui.controlador.ReportesControlador;
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

public class ReportesPantalla extends JFrame {

    private JTable tablaReportes;
    private DefaultTableModel modeloTabla;
    private DatePicker datePickerInicio, datePickerFin;
    private ReportesControlador controlador;

    public ReportesPantalla() {
        setTitle("Reportes");
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Fecha Inicio:"));
        datePickerInicio = new DatePicker(new DatePickerSettings());
        topPanel.add(datePickerInicio);

        topPanel.add(new JLabel("Fecha Fin:"));
        datePickerFin = new DatePicker(new DatePickerSettings());
        topPanel.add(datePickerFin);

        JButton btnGenerar = new JButton("Generar Reporte");
        JButton btnExportar = new JButton("Exportar PDF");
        topPanel.add(btnGenerar);
        topPanel.add(btnExportar);

        add(topPanel, BorderLayout.NORTH);

        String[] columnas = {"Folio", "Mesa", "Cliente", "Estado", "Total"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaReportes = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaReportes);
        scroll.setBorder(BorderFactory.createTitledBorder("Resultados"));
        add(scroll, BorderLayout.CENTER);

        controlador = new ReportesControlador(tablaReportes, modeloTabla, datePickerInicio, datePickerFin);

        btnGenerar.addActionListener(e -> controlador.generarReporte());
        btnExportar.addActionListener(e -> controlador.exportarPDF());

        setVisible(true);
    }
}
