package com.itson.sistema_de_comandas_restaurante.dao;

import com.itson.sistema_de_comandas_restaurante.entidad.DetalleComanda;
import com.itson.sistema_de_comandas_restaurante.entidad.Comanda;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

public class DetalleComandaDAO {

    private final EntityManagerFactory emf;

    public DetalleComandaDAO() {
        this.emf = Persistence.createEntityManagerFactory("com.itson_Sistema_de_Comandas_Restaurante_jar_1.0-SNAPSHOTPU");
    }

    public DetalleComanda crear(DetalleComanda detalle) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(detalle);
            tx.commit();
            return detalle;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al crear el detalle de comanda: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public DetalleComanda actualizar(DetalleComanda detalle) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            DetalleComanda actualizado = em.merge(detalle);
            tx.commit();
            return actualizado;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al actualizar el detalle de comanda: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            DetalleComanda detalle = em.find(DetalleComanda.class, id);
            if (detalle != null) {
                em.remove(detalle);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al eliminar el detalle de comanda: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public DetalleComanda buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(DetalleComanda.class, id);
        } finally {
            em.close();
        }
    }

    public List<DetalleComanda> listarTodos() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<DetalleComanda> query = em.createQuery(
                    "SELECT d FROM DetalleComanda d", DetalleComanda.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<DetalleComanda> buscarPorComanda(Comanda comanda) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<DetalleComanda> query = em.createQuery(
                    "SELECT d FROM DetalleComanda d WHERE d.comanda = :comanda", DetalleComanda.class);
            query.setParameter("comanda", comanda);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public boolean tieneDetallesConProducto(Long idProducto) {
        EntityManager em = emf.createEntityManager();
        try {
            Long count = em.createQuery(
                    "SELECT COUNT(dc) FROM DetalleComanda dc WHERE dc.producto.id = :id", Long.class)
                    .setParameter("id", idProducto)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}
