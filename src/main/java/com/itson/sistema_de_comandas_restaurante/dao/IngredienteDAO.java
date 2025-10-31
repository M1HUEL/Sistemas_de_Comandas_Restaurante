package com.itson.sistema_de_comandas_restaurante.dao;

import com.itson.sistema_de_comandas_restaurante.entidad.Ingrediente;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class IngredienteDAO {

    private final EntityManagerFactory emf;

    public IngredienteDAO() {
        this.emf = Persistence.createEntityManagerFactory(
                "com.itson_Sistema_de_Comandas_Restaurante_jar_1.0-SNAPSHOTPU");
    }

    public Ingrediente crear(Ingrediente ingrediente) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(ingrediente);
            tx.commit();
            return ingrediente;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al crear el ingrediente: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Ingrediente actualizar(Ingrediente ingrediente) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Ingrediente actualizado = em.merge(ingrediente);
            tx.commit();
            return actualizado;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al actualizar el ingrediente: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Ingrediente ingrediente = em.find(Ingrediente.class, id);
            if (ingrediente != null) {
                em.remove(ingrediente);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al eliminar el ingrediente: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Ingrediente buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Ingrediente.class, id);
        } finally {
            em.close();
        }
    }

    public List<Ingrediente> listarTodos() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Ingrediente> query = em.createQuery(
                    "SELECT i FROM Ingrediente i", Ingrediente.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Ingrediente> buscarPorNombre(String nombre) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Ingrediente> query = em.createQuery(
                    "SELECT i FROM Ingrediente i WHERE i.nombre LIKE :nombre", Ingrediente.class);
            query.setParameter("nombre", "%" + nombre + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public boolean estaAsociadoAProductos(Long idIngrediente) {
        EntityManager em = emf.createEntityManager();
        try {
            Long count = em.createQuery(
                    "SELECT COUNT(p) FROM Producto p JOIN p.ingredientes i WHERE i.id = :id", Long.class)
                    .setParameter("id", idIngrediente)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}
