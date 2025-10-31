package com.itson.sistema_de_comandas_restaurante.gui.controlador;

import com.itson.sistema_de_comandas_restaurante.dao.ClienteFrecuenteDAO;
import com.itson.sistema_de_comandas_restaurante.dao.ComandaDAO;
import com.itson.sistema_de_comandas_restaurante.entidad.Comanda;
import com.itson.sistema_de_comandas_restaurante.entidad.EstadoComanda;
import java.awt.BorderLayout;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class InicioControlador {

    private final ComandaDAO comandaDAO;
    private final ClienteFrecuenteDAO clienteDAO;
    private final JPanel panelIndicadores;
    private final DefaultTableModel modeloTabla;

    public InicioControlador(JPanel panelIndicadores, DefaultTableModel modeloTabla) {
        this.comandaDAO = new ComandaDAO();
        this.clienteDAO = new ClienteFrecuenteDAO();
        this.panelIndicadores = panelIndicadores;
        this.modeloTabla = modeloTabla;
    }

    public void inicializar() {
        actualizarIndicadores();
        actualizarTablaComandas();
    }

    public void actualizarIndicadores() {
        panelIndicadores.removeAll();

        List<Comanda> abiertas = comandaDAO.buscarPorEstado(EstadoComanda.ABIERTA);
        int mesasOcupadas = abiertas.size();
        int totalMesas = 10;

        double ventasDia = comandaDAO.buscarPorRangoFechas(LocalDate.now(), LocalDate.now())
                .stream()
                .filter(c -> c.getEstado() == EstadoComanda.ENTREGADA)
                .mapToDouble(c -> c.getTotal() != null ? c.getTotal() : 0.0)
                .sum();

        int clientesActivos = clienteDAO.listarTodos().size();

        agregarTarjeta("Mesas Ocupadas", mesasOcupadas + "/" + totalMesas);
        agregarTarjeta("Comandas Abiertas", String.valueOf(mesasOcupadas));
        agregarTarjeta("Ventas del Día", "$" + ventasDia);
        agregarTarjeta("Clientes Activos", String.valueOf(clientesActivos));

        panelIndicadores.revalidate();
        panelIndicadores.repaint();
    }

    private void agregarTarjeta(String titulo, String valor) {
        JPanel tarjeta = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel(titulo);
        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(lblValor, BorderLayout.CENTER);
        panelIndicadores.add(tarjeta);
    }

    public void actualizarTablaComandas() {
        modeloTabla.setRowCount(0);
        List<Comanda> recientes = comandaDAO.listarTodas();
        recientes.stream()
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
