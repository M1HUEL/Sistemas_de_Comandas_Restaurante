package com.itson.sistema_de_comandas_restaurante.gui.controlador;

import com.itson.sistema_de_comandas_restaurante.dao.ComandaDAO;
import com.itson.sistema_de_comandas_restaurante.entidad.Comanda;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class InicioControlador {

    private final ComandaDAO comandaDAO;
    private final DefaultTableModel modeloTabla;

    public InicioControlador(DefaultTableModel modeloTabla) {
        this.comandaDAO = new ComandaDAO();
        this.modeloTabla = modeloTabla;
    }

    public void inicializar() {
        actualizarTablaComandas();
    }

    public void actualizarTablaComandas() {
        modeloTabla.setRowCount(0);

        List<Comanda> recientes = comandaDAO.listarTodas();
        recientes.stream()
                .filter(c -> c.getFechaHoraCreacion() != null)
                .sorted((c1, c2) -> c2.getFechaHoraCreacion().compareTo(c1.getFechaHoraCreacion()))
                .limit(10)
                .forEach(c -> modeloTabla.addRow(new Object[]{
            c.getFolio(),
            c.getNumeroMesa(),
            c.getClienteFrecuente() != null ? c.getClienteFrecuente().getNombreCompleto() : "",
            c.getEstado(),
            c.getTotal()
        }));
    }
}
