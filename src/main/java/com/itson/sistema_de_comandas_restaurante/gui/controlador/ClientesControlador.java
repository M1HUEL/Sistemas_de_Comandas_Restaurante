package com.itson.sistema_de_comandas_restaurante.gui.controlador;

import com.itson.sistema_de_comandas_restaurante.dao.ClienteFrecuenteDAO;
import com.itson.sistema_de_comandas_restaurante.entidad.ClienteFrecuente;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextField;

public class ClientesControlador {

    private final ClienteFrecuenteDAO clienteDAO;
    private final JTable tablaClientes;
    private final DefaultTableModel modeloTabla;

    public ClientesControlador(JTable tablaClientes, DefaultTableModel modeloTabla) {
        this.clienteDAO = new ClienteFrecuenteDAO();
        this.tablaClientes = tablaClientes;
        this.modeloTabla = modeloTabla;
    }

    public void inicializar() {
        cargarClientes();
    }

    public void cargarClientes() {
        modeloTabla.setRowCount(0);
        List<ClienteFrecuente> clientes = clienteDAO.listarTodos();
        for (ClienteFrecuente c : clientes) {
            modeloTabla.addRow(new Object[]{
                c.getId(),
                c.getNombreCompleto(),
                c.getTelefono(),
                c.getCorreo(),
                c.getVisitas(),
                c.getTotalGastado(),
                c.getPuntosFidelidad()
            });
        }
    }

    public void agregarCliente() {
        JTextField nombreField = new JTextField();
        JTextField telefonoField = new JTextField();
        JTextField correoField = new JTextField();

        Object[] mensaje = {"Nombre:", nombreField, "Teléfono:", telefonoField, "Correo:", correoField};

        int option = JOptionPane.showConfirmDialog(null, mensaje, "Agregar Cliente", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String nombre = nombreField.getText().trim();
            String telefono = telefonoField.getText().trim();
            String correo = correoField.getText().trim();

            if (nombre.isEmpty() || telefono.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nombre y Teléfono son obligatorios.");
                return;
            }

            if (!telefono.matches("\\d{7,15}")) {
                JOptionPane.showMessageDialog(null, "Ingrese un teléfono válido (solo números, 7 a 15 dígitos).");
                return;
            }

            if (!correo.isEmpty() && !correo.matches("^\\S+@\\S+\\.\\S+$")) {
                JOptionPane.showMessageDialog(null, "Correo electrónico inválido.");
                return;
            }

            if (clienteDAO.existeTelefono(telefono)) {
                JOptionPane.showMessageDialog(null, "Ya existe un cliente con este teléfono.");
                return;
            }

            ClienteFrecuente cliente = new ClienteFrecuente();
            cliente.setNombreCompleto(nombre);
            cliente.setTelefono(telefono);
            cliente.setCorreo(correo);
            cliente.setVisitas(0);
            cliente.setTotalGastado(0.0);
            cliente.setPuntosFidelidad(0);

            clienteDAO.crear(cliente);
            cargarClientes();
        }
    }

    public void editarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(null, "Seleccione un cliente para editar.");
            return;
        }
        Long id = (Long) modeloTabla.getValueAt(fila, 0);
        ClienteFrecuente cliente = clienteDAO.buscarPorId(id);

        JTextField nombreField = new JTextField(cliente.getNombreCompleto());
        JTextField telefonoField = new JTextField(cliente.getTelefono());
        JTextField correoField = new JTextField(cliente.getCorreo());

        Object[] mensaje = {"Nombre:", nombreField, "Teléfono:", telefonoField, "Correo:", correoField};

        int option = JOptionPane.showConfirmDialog(null, mensaje, "Editar Cliente", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String nombre = nombreField.getText().trim();
            String telefono = telefonoField.getText().trim();
            String correo = correoField.getText().trim();

            if (nombre.isEmpty() || telefono.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nombre y Teléfono son obligatorios.");
                return;
            }

            if (!telefono.matches("\\d{7,15}")) {
                JOptionPane.showMessageDialog(null, "Ingrese un teléfono válido (solo números, 7 a 15 dígitos).");
                return;
            }

            if (!correo.isEmpty() && !correo.matches("^\\S+@\\S+\\.\\S+$")) {
                JOptionPane.showMessageDialog(null, "Correo electrónico inválido.");
                return;
            }

            if (clienteDAO.existeTelefonoExcluyendoId(telefono, id)) {
                JOptionPane.showMessageDialog(null, "Ya existe un cliente con este teléfono.");
                return;
            }

            cliente.setNombreCompleto(nombre);
            cliente.setTelefono(telefono);
            cliente.setCorreo(correo);

            clienteDAO.actualizar(cliente);
            cargarClientes();
        }
    }

    public void eliminarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(null, "Seleccione un cliente para eliminar.");
            return;
        }

        Long id = (Long) modeloTabla.getValueAt(fila, 0);

        if (clienteDAO.tieneComandasAsociadas(id)) {
            JOptionPane.showMessageDialog(null,
                    "No se puede eliminar el cliente porque tiene comandas asociadas.",
                    "Operación no permitida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null,
                "¿Desea eliminar este cliente?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            clienteDAO.eliminar(id);
            cargarClientes();
        }
    }
}
