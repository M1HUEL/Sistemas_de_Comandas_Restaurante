package com.itson.sistema_de_comandas_restaurante.gui.controlador;

import com.itson.sistema_de_comandas_restaurante.dao.DetalleComandaDAO;
import com.itson.sistema_de_comandas_restaurante.dao.ProductoDAO;
import com.itson.sistema_de_comandas_restaurante.entidad.Producto;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public final class ProductoControlador {

    private final ProductoDAO productoDAO;
    private DetalleComandaDAO detalleComandaDAO;
    private final JTable tablaProductos;
    private final DefaultTableModel modeloTabla;

    public ProductoControlador(JTable tablaProductos, DefaultTableModel modeloTabla) {
        this.productoDAO = new ProductoDAO();
        this.detalleComandaDAO = new DetalleComandaDAO();
        this.tablaProductos = tablaProductos;
        this.modeloTabla = modeloTabla;
        cargarProductos();
    }

    public void cargarProductos() {
        modeloTabla.setRowCount(0);
        List<Producto> productos = productoDAO.obtenerTodos();
        for (Producto p : productos) {
            modeloTabla.addRow(new Object[]{
                p.getId(),
                p.getNombre(),
                p.getPrecio(),
                p.getTipo(),
                p.getDisponible() ? "Sí" : "No"
            });
        }
    }

    public Producto mostrarFormulario(Producto p) {
        JTextField nombreField = new JTextField();
        JTextField precioField = new JTextField();
        JTextField tipoField = new JTextField();
        JCheckBox disponibleCheck = new JCheckBox("Disponible");

        if (p != null) {
            nombreField.setText(p.getNombre());
            precioField.setText(p.getPrecio().toString());
            tipoField.setText(p.getTipo());
            disponibleCheck.setSelected(p.getDisponible());
        }

        Object[] campos = {
            "Nombre:", nombreField,
            "Precio:", precioField,
            "Tipo:", tipoField,
            disponibleCheck
        };

        int opcion = JOptionPane.showConfirmDialog(null, campos,
                p == null ? "Agregar Producto" : "Editar Producto",
                JOptionPane.OK_CANCEL_OPTION);

        if (opcion == JOptionPane.OK_OPTION) {
            try {
                String nombre = nombreField.getText().trim();
                double precio = Double.parseDouble(precioField.getText().trim());
                String tipo = tipoField.getText().trim();
                boolean disponible = disponibleCheck.isSelected();

                if (p == null) {
                    p = new Producto();
                }

                p.setNombre(nombre);
                p.setPrecio(precio);
                p.setTipo(tipo);
                p.setDisponible(disponible);

                return p;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Precio inválido.");
            }
        }
        return null;
    }

    public void agregarProducto() {
        Producto nuevo = mostrarFormulario(null);
        if (nuevo != null) {
            productoDAO.crear(nuevo);
            cargarProductos();
        }
    }

    public void editarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(null, "Seleccione un producto para editar.");
            return;
        }
        Long id = (Long) modeloTabla.getValueAt(fila, 0);
        Producto p = productoDAO.buscarPorId(id);
        Producto editado = mostrarFormulario(p);
        if (editado != null) {
            productoDAO.actualizar(editado);
            cargarProductos();
        }
    }

    public void eliminarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(null, "Seleccione un producto para eliminar.");
            return;
        }

        Long id = ((Number) modeloTabla.getValueAt(fila, 0)).longValue();

        if (detalleComandaDAO.tieneDetallesConProducto(id)) {
            JOptionPane.showMessageDialog(null,
                    "No se puede eliminar el producto porque ya está registrado en alguna comanda.",
                    "Operación no permitida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null,
                "¿Está seguro de eliminar el producto?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            productoDAO.eliminar(id);
            cargarProductos();
            JOptionPane.showMessageDialog(null, "Producto eliminado correctamente.");
        }
    }
}
