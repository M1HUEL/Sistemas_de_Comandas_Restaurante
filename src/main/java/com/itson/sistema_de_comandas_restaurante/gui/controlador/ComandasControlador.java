package com.itson.sistema_de_comandas_restaurante.gui.controlador;

import com.itson.sistema_de_comandas_restaurante.dao.ClienteFrecuenteDAO;
import com.itson.sistema_de_comandas_restaurante.dao.ComandaDAO;
import com.itson.sistema_de_comandas_restaurante.dao.ProductoDAO;
import com.itson.sistema_de_comandas_restaurante.entidad.ClienteFrecuente;
import com.itson.sistema_de_comandas_restaurante.entidad.Comanda;
import com.itson.sistema_de_comandas_restaurante.entidad.DetalleComanda;
import com.itson.sistema_de_comandas_restaurante.entidad.EstadoComanda;
import com.itson.sistema_de_comandas_restaurante.entidad.Producto;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ComandasControlador {

    private final ComandaDAO comandaDAO;
    private final ProductoDAO productoDAO;
    private final ClienteFrecuenteDAO clienteFrecuenteDAO;
    private final JTable tablaComandas;
    private final JTable tablaProductos;
    private final DefaultTableModel modeloComandas;
    private final DefaultTableModel modeloProductos;
    private final JLabel lblTotal;

    private Comanda comandaSeleccionada;

    public ComandasControlador(JTable tablaComandas, JTable tablaProductos,
            DefaultTableModel modeloComandas, DefaultTableModel modeloProductos,
            JLabel lblTotal) {
        this.comandaDAO = new ComandaDAO();
        this.productoDAO = new ProductoDAO();
        this.clienteFrecuenteDAO = new ClienteFrecuenteDAO();
        this.tablaComandas = tablaComandas;
        this.tablaProductos = tablaProductos;
        this.modeloComandas = modeloComandas;
        this.modeloProductos = modeloProductos;
        this.lblTotal = lblTotal;
    }

    public void inicializar() {
        cargarComandas();
        tablaComandas.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaComandas.getSelectedRow();
            if (fila >= 0) {
                Long idComanda = (Long) modeloComandas.getValueAt(fila, 0);
                comandaSeleccionada = comandaDAO.buscarPorId(idComanda);
                cargarDetalleComanda();
            }
        });
    }

    public void cargarComandas() {
        modeloComandas.setRowCount(0);
        List<Comanda> comandas = comandaDAO.listarTodas();
        for (Comanda c : comandas) {
            modeloComandas.addRow(new Object[]{
                c.getId(),
                c.getFolio(),
                c.getNumeroMesa(),
                c.getClienteFrecuente() != null ? c.getClienteFrecuente().getNombreCompleto() : "",
                c.getEstado(),
                c.getTotal()
            });
        }
    }

    public void cargarDetalleComanda() {
        modeloProductos.setRowCount(0);
        double total = 0;
        if (comandaSeleccionada != null && comandaSeleccionada.getDetallesComanda() != null) {
            for (DetalleComanda d : comandaSeleccionada.getDetallesComanda()) {
                double subtotal = d.getCantidad() * d.getPrecioUnitario();
                total += subtotal;
                modeloProductos.addRow(new Object[]{
                    d.getProducto().getNombre(),
                    d.getCantidad(),
                    d.getPrecioUnitario(),
                    subtotal
                });
            }
        }
        lblTotal.setText("Total: $" + total);
    }

    public void agregarProducto() {
        List<Producto> productos = productoDAO.obtenerDisponibles();
        if (productos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay productos disponibles.");
            return;
        }

        String[] nombres = productos.stream().map(Producto::getNombre).toArray(String[]::new);
        String seleccionado = (String) JOptionPane.showInputDialog(null, "Seleccione un producto",
                "Agregar Producto", JOptionPane.PLAIN_MESSAGE, null, nombres, nombres[0]);
        if (seleccionado != null) {
            Producto p = productoDAO.buscarPorNombre(seleccionado);
            String cantidadStr = JOptionPane.showInputDialog(null, "Cantidad:");
            try {
                int cantidad = Integer.parseInt(cantidadStr);
                double subtotal = cantidad * p.getPrecio();
                modeloProductos.addRow(new Object[]{
                    p.getNombre(),
                    cantidad,
                    p.getPrecio(),
                    subtotal
                });
                actualizarTotal();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Cantidad inválida.");
            }
        }
    }

    public void actualizarTotal() {
        double total = 0;
        for (int i = 0; i < modeloProductos.getRowCount(); i++) {
            Object valor = modeloProductos.getValueAt(i, 3);
            if (valor instanceof Number) {
                total += ((Number) valor).doubleValue();
            }
        }
        lblTotal.setText("Total: $" + total);
    }

    public String generarFolio() {
        int numeroAleatorio = (int) (Math.random() * 9000) + 1000;
        String fecha = java.time.LocalDate.now().toString().replace("-", "");
        return "COM-" + fecha + "-" + numeroAleatorio;
    }

    public void guardarComanda() {
        int MAX_MESAS = 10;

        long mesasOcupadas = comandaDAO.listarTodas().stream()
                .filter(c -> c.getEstado() != EstadoComanda.CANCELADA)
                .map(c -> c.getNumeroMesa())
                .distinct()
                .count();

        if (mesasOcupadas >= MAX_MESAS) {
            JOptionPane.showMessageDialog(null, "No se pueden agregar más comandas. Todas las mesas están ocupadas.");
            return;
        }

        if (modeloProductos.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Agregue al menos un producto a la comanda.");
            return;
        }

        String folio = generarFolio();

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 5, 5));

        JLabel lblMesa = new JLabel("Número de mesa:");
        JTextField txtMesa = new JTextField();
        panel.add(lblMesa);
        panel.add(txtMesa);

        JLabel lblCliente = new JLabel("Cliente frecuente (opcional):");
        List<ClienteFrecuente> clientes = clienteFrecuenteDAO.listarTodos();
        JComboBox<String> comboClientes;

        if (clientes.isEmpty()) {
            comboClientes = new JComboBox<>(new String[]{});
        } else {
            String[] nombres = clientes.stream()
                    .map(ClienteFrecuente::getNombreCompleto)
                    .toArray(String[]::new);
            comboClientes = new JComboBox<>(nombres);
            comboClientes.setSelectedIndex(-1);
        }

        panel.add(lblCliente);
        panel.add(comboClientes);

        int result = JOptionPane.showConfirmDialog(null, panel, "Datos de la Comanda",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int mesa = Integer.parseInt(txtMesa.getText().trim());

            boolean mesaOcupada = comandaDAO.listarTodas().stream()
                    .anyMatch(c -> c.getNumeroMesa() == mesa && c.getEstado() != EstadoComanda.CANCELADA);
            if (mesaOcupada) {
                JOptionPane.showMessageDialog(null, "La mesa " + mesa + " ya tiene una comanda activa.");
                return;
            }

            Comanda comanda = comandaSeleccionada != null ? comandaSeleccionada : new Comanda();
            comanda.setFolio(folio);
            comanda.setNumeroMesa(mesa);
            comanda.setFechaHoraCreacion(LocalDateTime.now());
            comanda.setEstado(EstadoComanda.ABIERTA);

            if (comboClientes.getSelectedIndex() >= 0) {
                ClienteFrecuente cliente = clientes.get(comboClientes.getSelectedIndex());

                boolean clienteOcupado = comandaDAO.listarTodas().stream()
                        .anyMatch(c -> c.getClienteFrecuente() != null
                        && c.getClienteFrecuente().getId().equals(cliente.getId())
                        && c.getEstado() != EstadoComanda.CANCELADA
                        && (comandaSeleccionada == null || !c.getId().equals(comandaSeleccionada.getId()))
                        );

                if (clienteOcupado) {
                    JOptionPane.showMessageDialog(null,
                            "El cliente seleccionado ya tiene una comanda activa.");
                    return;
                }

                comanda.setClienteFrecuente(cliente);
            } else {
                comanda.setClienteFrecuente(null);
            }

            double total = 0;
            Set<DetalleComanda> detalles = new HashSet<>();
            for (int i = 0; i < modeloProductos.getRowCount(); i++) {
                String nombreProducto = (String) modeloProductos.getValueAt(i, 0);
                int cantidad = (int) modeloProductos.getValueAt(i, 1);
                double precioUnitario = (double) modeloProductos.getValueAt(i, 2);
                Producto producto = productoDAO.buscarPorNombre(nombreProducto);

                DetalleComanda detalle = new DetalleComanda();
                detalle.setComanda(comanda);
                detalle.setProducto(producto);
                detalle.setCantidad(cantidad);
                detalle.setPrecioUnitario(precioUnitario);
                detalle.setImporteTotal(cantidad * precioUnitario);
                detalles.add(detalle);
                total += cantidad * precioUnitario;
            }

            comanda.setTotal(total);
            comanda.setDetallesComanda(detalles);

            if (comandaSeleccionada == null) {
                comandaDAO.crear(comanda);
                JOptionPane.showMessageDialog(null, "Comanda guardada correctamente.");
            } else {
                comandaDAO.actualizar(comanda);
                JOptionPane.showMessageDialog(null, "Comanda actualizada correctamente.");
            }

            cancelarComanda();
            cargarComandas();
            comandaSeleccionada = null;

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Número de mesa inválido.");
        }
    }

    public void marcarEntregada() {
        if (comandaSeleccionada == null) {
            JOptionPane.showMessageDialog(null, "Seleccione una comanda primero.");
            return;
        }
        comandaSeleccionada.setEstado(EstadoComanda.ENTREGADA);
        comandaDAO.actualizar(comandaSeleccionada);
        JOptionPane.showMessageDialog(null, "Comanda marcada como ENTREGADA.");
        cargarComandas();
    }

    public void cancelarComanda() {
        comandaSeleccionada = null;
        modeloProductos.setRowCount(0);
        lblTotal.setText("Total: $0.0");
    }

    public void actualizarComanda() {
        if (comandaSeleccionada == null) {
            JOptionPane.showMessageDialog(null, "Seleccione una comanda primero.");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        JLabel lblFolio = new JLabel("Folio:");
        JTextField txtFolio = new JTextField(comandaSeleccionada.getFolio());
        panel.add(lblFolio);
        panel.add(txtFolio);

        JLabel lblMesa = new JLabel("Número de mesa:");
        JTextField txtMesa = new JTextField(String.valueOf(comandaSeleccionada.getNumeroMesa()));
        panel.add(lblMesa);
        panel.add(txtMesa);

        JLabel lblCliente = new JLabel("Cliente frecuente (opcional):");
        List<ClienteFrecuente> clientes = clienteFrecuenteDAO.listarTodos();
        JComboBox<String> comboCliente;

        if (clientes.isEmpty()) {
            comboCliente = new JComboBox<>(new String[]{});
        } else {
            String[] nombresClientes = clientes.stream()
                    .map(ClienteFrecuente::getNombreCompleto)
                    .toArray(String[]::new);
            comboCliente = new JComboBox<>(nombresClientes);
            comboCliente.setSelectedIndex(-1); // Ningún cliente seleccionado
            if (comandaSeleccionada.getClienteFrecuente() != null) {
                comboCliente.setSelectedItem(comandaSeleccionada.getClienteFrecuente().getNombreCompleto());
            }
        }
        panel.add(lblCliente);
        panel.add(comboCliente);

        int resultado = JOptionPane.showConfirmDialog(
                null, panel, "Actualizar Comanda", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                String nuevoFolio = txtFolio.getText().trim();
                int nuevaMesa = Integer.parseInt(txtMesa.getText().trim());

                boolean mesaOcupada = comandaDAO.listarTodas().stream()
                        .anyMatch(c -> c.getNumeroMesa() == nuevaMesa
                        && c.getEstado() != EstadoComanda.CANCELADA
                        && !c.getId().equals(comandaSeleccionada.getId()));

                if (mesaOcupada) {
                    JOptionPane.showMessageDialog(null, "La mesa " + nuevaMesa + " ya tiene una comanda activa.");
                    return;
                }

                comandaSeleccionada.setFolio(nuevoFolio);
                comandaSeleccionada.setNumeroMesa(nuevaMesa);

                if (comboCliente.getSelectedIndex() >= 0) {
                    ClienteFrecuente cliente = clientes.get(comboCliente.getSelectedIndex());

                    boolean clienteOcupado = comandaDAO.listarTodas().stream()
                            .anyMatch(c -> c.getClienteFrecuente() != null
                            && c.getClienteFrecuente().getId().equals(cliente.getId())
                            && c.getEstado() != EstadoComanda.CANCELADA
                            && !c.getId().equals(comandaSeleccionada.getId()));

                    if (clienteOcupado) {
                        JOptionPane.showMessageDialog(null,
                                "El cliente seleccionado ya tiene una comanda activa.");
                        return;
                    }

                    comandaSeleccionada.setClienteFrecuente(cliente);
                } else {
                    comandaSeleccionada.setClienteFrecuente(null);
                }

                comandaDAO.actualizar(comandaSeleccionada);
                JOptionPane.showMessageDialog(null, "Comanda actualizada correctamente.");
                cargarComandas();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Número de mesa inválido.");
            }
        }
    }

    public void eliminarComanda() {
        if (comandaSeleccionada == null) {
            JOptionPane.showMessageDialog(null, "Seleccione una comanda primero.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                null,
                "¿Está seguro de que desea eliminar la comanda " + comandaSeleccionada.getFolio() + "?",
                "Eliminar Comanda",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            comandaDAO.eliminar(comandaSeleccionada.getId());
            JOptionPane.showMessageDialog(null, "Comanda eliminada correctamente.");
            cancelarComanda();
            cargarComandas();
        }
    }
}
