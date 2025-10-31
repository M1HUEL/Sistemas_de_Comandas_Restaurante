package com.itson.sistema_de_comandas_restaurante.gui.controlador;

import com.itson.sistema_de_comandas_restaurante.dao.IngredienteDAO;
import com.itson.sistema_de_comandas_restaurante.entidad.Ingrediente;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextField;

public class IngredienteControlador {

    private final IngredienteDAO ingredienteDAO;
    private final DefaultTableModel modeloTabla;
    private final JPanel panel;
    private final JTable tabla;

    public IngredienteControlador(JTable tabla, DefaultTableModel modeloTabla, JPanel panel) {
        this.ingredienteDAO = new IngredienteDAO();
        this.modeloTabla = modeloTabla;
        this.tabla = tabla;
        this.panel = panel;
    }

    public void inicializar() {
        cargarIngredientes();
    }

    public void cargarIngredientes() {
        modeloTabla.setRowCount(0);
        List<Ingrediente> ingredientes = ingredienteDAO.listarTodos();
        for (Ingrediente i : ingredientes) {
            modeloTabla.addRow(new Object[]{
                i.getId(),
                i.getNombre(),
                i.getUnidadMedida(),
                i.getStock()
            });
        }
    }

    public void agregarIngrediente() {
        Ingrediente nuevo = mostrarFormulario(null);
        if (nuevo != null) {
            ingredienteDAO.crear(nuevo);
            cargarIngredientes();
        }
    }

    public void editarIngrediente() {
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            Long id = (Long) modeloTabla.getValueAt(fila, 0);
            Ingrediente i = ingredienteDAO.buscarPorId(id);
            Ingrediente editado = mostrarFormulario(i);
            if (editado != null) {
                ingredienteDAO.actualizar(editado);
                cargarIngredientes();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione un ingrediente para editar.");
        }
    }

    public void eliminarIngrediente() {
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            Long id = (Long) modeloTabla.getValueAt(fila, 0);

            if (ingredienteDAO.estaAsociadoAProductos(id)) {
                JOptionPane.showMessageDialog(null,
                        "No se puede eliminar el ingrediente porque está asociado a uno o más productos.",
                        "Operación no permitida",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(null,
                    "¿Está seguro de eliminar el ingrediente?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                ingredienteDAO.eliminar(id);
                cargarIngredientes();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione un ingrediente para eliminar.");
        }
    }

    private Ingrediente mostrarFormulario(Ingrediente i) {
        JTextField nombreField = new JTextField();
        JTextField unidadField = new JTextField();
        JTextField stockField = new JTextField();

        if (i != null) {
            nombreField.setText(i.getNombre());
            unidadField.setText(i.getUnidadMedida());
            stockField.setText(i.getStock().toString());
        }

        Object[] campos = {
            "Nombre:", nombreField,
            "Unidad de Medida:", unidadField,
            "Stock:", stockField
        };

        int opcion = JOptionPane.showConfirmDialog(null, campos,
                i == null ? "Agregar Ingrediente" : "Editar Ingrediente",
                JOptionPane.OK_CANCEL_OPTION);

        if (opcion == JOptionPane.OK_OPTION) {
            try {
                String nombre = nombreField.getText().trim();
                String unidad = unidadField.getText().trim();
                double stock = Double.parseDouble(stockField.getText().trim());

                if (i == null) {
                    i = new Ingrediente();
                }

                i.setNombre(nombre);
                i.setUnidadMedida(unidad);
                i.setStock(stock);

                return i;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Stock inválido.");
            }
        }
        return null;
    }

}
