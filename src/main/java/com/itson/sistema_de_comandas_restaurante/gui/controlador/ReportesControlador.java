package com.itson.sistema_de_comandas_restaurante.gui.controlador;

import com.github.lgooddatepicker.components.DatePicker;
import com.itson.sistema_de_comandas_restaurante.dao.ComandaDAO;
import com.itson.sistema_de_comandas_restaurante.entidad.Comanda;
import java.awt.HeadlessException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class ReportesControlador {

    private final ComandaDAO comandaDAO;
    private final JTable tablaReportes;
    private final DefaultTableModel modeloTabla;
    private final DatePicker datePickerInicio;
    private final DatePicker datePickerFin;

    public ReportesControlador(JTable tablaReportes, DefaultTableModel modeloTabla,
            DatePicker datePickerInicio, DatePicker datePickerFin) {
        this.comandaDAO = new ComandaDAO();
        this.tablaReportes = tablaReportes;
        this.modeloTabla = modeloTabla;
        this.datePickerInicio = datePickerInicio;
        this.datePickerFin = datePickerFin;
    }

    public void generarReporte() {
        modeloTabla.setRowCount(0);
        try {
            LocalDate inicio = datePickerInicio.getDate();
            LocalDate fin = datePickerFin.getDate();

            if (inicio == null || fin == null) {
                JOptionPane.showMessageDialog(null, "Seleccione ambas fechas.");
                return;
            }

            List<Comanda> comandas = comandaDAO.buscarPorRangoFechas(inicio, fin);
            for (Comanda c : comandas) {
                modeloTabla.addRow(new Object[]{
                    c.getFolio(),
                    c.getNumeroMesa(),
                    c.getClienteFrecuente() != null ? c.getClienteFrecuente().getNombreCompleto() : "",
                    c.getEstado(),
                    c.getTotal()
                });
            }

        } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(null, "Error al generar reporte.");
        }
    }

    public void exportarPDF() {
        try {
            LocalDate inicio = datePickerInicio.getDate();
            LocalDate fin = datePickerFin.getDate();

            if (inicio == null || fin == null) {
                JOptionPane.showMessageDialog(null, "Seleccione ambas fechas.");
                return;
            }

            List<Comanda> comandas = comandaDAO.buscarPorRangoFechas(inicio, fin);

            if (comandas.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No hay datos para las fechas seleccionadas.");
                return;
            }

            List<Map<String, Object>> datosReporte = comandas.stream().map(c -> {
                Map<String, Object> fila = new HashMap<>();
                fila.put("folio", c.getFolio());
                fila.put("numeroMesa", c.getNumeroMesa());
                fila.put("cliente", c.getClienteFrecuente() != null ? c.getClienteFrecuente().getNombreCompleto() : "");
                fila.put("estado", c.getEstado().toString());
                fila.put("total", c.getTotal());
                return fila;
            }).toList();

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(datosReporte);

            InputStream reporteStream = getClass().getResourceAsStream("/reports/ReporteComandas.jrxml");
            if (reporteStream == null) {
                JOptionPane.showMessageDialog(null, "No se encontró el archivo de reporte.");
                return;
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reporteStream);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("fechaInicio", inicio.toString());
            parametros.put("fechaFin", fin.toString());

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

            String outputPath = "ReporteComandas.pdf";
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);

            JOptionPane.showMessageDialog(null, "Reporte generado correctamente en: " + outputPath);

        } catch (HeadlessException | JRException e) {
            JOptionPane.showMessageDialog(null, "Error al generar el reporte: " + e.getMessage());
        }
    }
}
