package com.itson.sistema_de_comandas_restaurante;

import com.itson.sistema_de_comandas_restaurante.dao.*;
import com.itson.sistema_de_comandas_restaurante.entidad.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class InsercionDatosMasivos {

    public static void main(String[] args) {
        IngredienteDAO ingredienteDAO = new IngredienteDAO();
        ProductoDAO productoDAO = new ProductoDAO();
        ClienteFrecuenteDAO clienteDAO = new ClienteFrecuenteDAO();
        ComandaDAO comandaDAO = new ComandaDAO();

        Ingrediente tomate = new Ingrediente();
        tomate.setNombre("Tomate");
        tomate.setUnidadMedida("kg");
        tomate.setStock(50.0);

        Ingrediente queso = new Ingrediente();
        queso.setNombre("Queso");
        queso.setUnidadMedida("kg");
        queso.setStock(30.0);

        Ingrediente lechuga = new Ingrediente();
        lechuga.setNombre("Lechuga");
        lechuga.setUnidadMedida("kg");
        lechuga.setStock(20.0);

        Ingrediente pan = new Ingrediente();
        pan.setNombre("Pan");
        pan.setUnidadMedida("pieza");
        pan.setStock(100.0);

        Ingrediente carne = new Ingrediente();
        carne.setNombre("Carne");
        carne.setUnidadMedida("kg");
        carne.setStock(40.0);

        Ingrediente pollo = new Ingrediente();
        pollo.setNombre("Pollo");
        pollo.setUnidadMedida("kg");
        pollo.setStock(25.0);

        Ingrediente cebolla = new Ingrediente();
        cebolla.setNombre("Cebolla");
        cebolla.setUnidadMedida("kg");
        cebolla.setStock(15.0);

        ingredienteDAO.crear(tomate);
        ingredienteDAO.crear(queso);
        ingredienteDAO.crear(lechuga);
        ingredienteDAO.crear(pan);
        ingredienteDAO.crear(carne);
        ingredienteDAO.crear(pollo);
        ingredienteDAO.crear(cebolla);

        Producto hamburguesa = new Producto();
        hamburguesa.setNombre("Hamburguesa");
        hamburguesa.setPrecio(120.0);
        hamburguesa.setTipo("Comida");
        hamburguesa.setDisponible(true);
        hamburguesa.setIngredientes(new HashSet<>(Set.of(tomate, queso, pan, carne, cebolla)));
        productoDAO.crear(hamburguesa);

        Producto ensalada = new Producto();
        ensalada.setNombre("Ensalada");
        ensalada.setPrecio(80.0);
        ensalada.setTipo("Comida");
        ensalada.setDisponible(true);
        ensalada.setIngredientes(new HashSet<>(Set.of(lechuga, tomate, cebolla)));
        productoDAO.crear(ensalada);

        Producto polloFrito = new Producto();
        polloFrito.setNombre("Pollo Frito");
        polloFrito.setPrecio(150.0);
        polloFrito.setTipo("Comida");
        polloFrito.setDisponible(true);
        polloFrito.setIngredientes(new HashSet<>(Set.of(pollo, pan, tomate)));
        productoDAO.crear(polloFrito);

        Producto pizza = new Producto();
        pizza.setNombre("Pizza");
        pizza.setPrecio(200.0);
        pizza.setTipo("Comida");
        pizza.setDisponible(true);
        pizza.setIngredientes(new HashSet<>(Set.of(queso, tomate, cebolla)));
        productoDAO.crear(pizza);

        Producto refresco = new Producto();
        refresco.setNombre("Refresco");
        refresco.setPrecio(30.0);
        refresco.setTipo("Bebida");
        refresco.setDisponible(true);
        productoDAO.crear(refresco);

        Producto agua = new Producto();
        agua.setNombre("Agua");
        agua.setPrecio(20.0);
        agua.setTipo("Bebida");
        agua.setDisponible(true);
        productoDAO.crear(agua);

        ClienteFrecuente cliente1 = new ClienteFrecuente();
        cliente1.setNombreCompleto("Juan Manuel");
        cliente1.setTelefono("5551234567");
        cliente1.setCorreo("juan.manuel@gmail.com");
        cliente1.setFechaRegistro(LocalDate.now().minusMonths(2));
        cliente1.setVisitas(5);
        cliente1.setTotalGastado(500.0);
        cliente1.setPuntosFidelidad(50);

        ClienteFrecuente cliente2 = new ClienteFrecuente();
        cliente2.setNombreCompleto("Roberto Carlos");
        cliente2.setTelefono("5559876543");
        cliente2.setCorreo("roberto.carlos@gmail.com");
        cliente2.setFechaRegistro(LocalDate.now().minusMonths(1));
        cliente2.setVisitas(3);
        cliente2.setTotalGastado(300.0);
        cliente2.setPuntosFidelidad(30);

        ClienteFrecuente cliente3 = new ClienteFrecuente();
        cliente3.setNombreCompleto("Carlos Rivera");
        cliente3.setTelefono("5551122334");
        cliente3.setCorreo("carlos.rivera@gmail.com");
        cliente3.setFechaRegistro(LocalDate.now().minusWeeks(3));
        cliente3.setVisitas(2);
        cliente3.setTotalGastado(180.0);
        cliente3.setPuntosFidelidad(18);

        clienteDAO.crear(cliente1);
        clienteDAO.crear(cliente2);
        clienteDAO.crear(cliente3);

        crearComanda(comandaDAO, hamburguesa, cliente1, 5, EstadoComanda.ABIERTA, "Sin cebolla");
        crearComanda(comandaDAO, ensalada, cliente2, 3, EstadoComanda.ENTREGADA, "");
        crearComanda(comandaDAO, pizza, cliente3, 1, EstadoComanda.ABIERTA, "Extra queso");
        crearComanda(comandaDAO, polloFrito, cliente1, 2, EstadoComanda.ABIERTA, "Picante");
        crearComanda(comandaDAO, refresco, cliente2, 4, EstadoComanda.ENTREGADA, "");
        crearComanda(comandaDAO, agua, cliente3, 1, EstadoComanda.ABIERTA, "");

        System.out.println("Datos de prueba insertados correctamente.");
    }

    private static void crearComanda(ComandaDAO comandaDAO, Producto producto, ClienteFrecuente cliente,
            int mesa, EstadoComanda estado, String notas) {
        Comanda comanda = new Comanda();
        comanda.setFolio("COM-" + System.currentTimeMillis());
        comanda.setFechaHoraCreacion(LocalDateTime.now());
        comanda.setNumeroMesa(mesa);
        comanda.setEstado(estado);
        comanda.setTotal(producto.getPrecio());
        comanda.setClienteFrecuente(cliente);

        DetalleComanda detalle = new DetalleComanda();
        detalle.setComanda(comanda);
        detalle.setProducto(producto);
        detalle.setCantidad(1);
        detalle.setPrecioUnitario(producto.getPrecio());
        detalle.setImporteTotal(producto.getPrecio());
        detalle.setNotasEspeciales(notas);

        comanda.setDetallesComanda(new HashSet<>(Set.of(detalle)));

        comandaDAO.crear(comanda);
    }
}
