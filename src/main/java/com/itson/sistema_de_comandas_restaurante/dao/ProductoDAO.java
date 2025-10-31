package com.itson.sistema_de_comandas_restaurante.dao;

import com.itson.sistema_de_comandas_restaurante.entidad.Producto;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

public class ProductoDAO {

    private final EntityManagerFactory emf;

    public ProductoDAO() {
        this.emf = Persistence.createEntityManagerFactory(
                "com.itson_Sistema_de_Comandas_Restaurante_jar_1.0-SNAPSHOTPU");
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void crear(Producto producto) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(producto);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new PersistenceException("Error al crear el producto", e);
        } finally {
            em.close();
        }
    }

    public Producto actualizar(Producto producto) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Producto actualizado = em.merge(producto);
            em.getTransaction().commit();
            return actualizado;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new PersistenceException("Error al actualizar el producto", e);
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Producto producto = em.find(Producto.class, id);
            if (producto != null) {
                em.remove(producto);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new PersistenceException("Error al eliminar el producto", e);
        } finally {
            em.close();
        }
    }

    public Producto buscarPorId(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Producto.class, id);
        } finally {
            em.close();
        }
    }

    public Producto buscarPorNombre(String nombre) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery(
                    "SELECT p FROM Producto p WHERE p.nombre = :nombre", Producto.class);
            query.setParameter("nombre", nombre);
            List<Producto> resultados = query.getResultList();
            return resultados.isEmpty() ? null : resultados.get(0);
        } finally {
            em.close();
        }
    }

    public List<Producto> obtenerTodos() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery("SELECT p FROM Producto p", Producto.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Producto> obtenerDisponibles() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery(
                    "SELECT p FROM Producto p WHERE p.disponible = true", Producto.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Producto> buscarPorTipo(String tipo) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery(
                    "SELECT p FROM Producto p WHERE p.tipo = :tipo", Producto.class);
            query.setParameter("tipo", tipo);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Producto> buscarPorTipoYDisponibilidad(String tipo, boolean disponible) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery(
                    "SELECT p FROM Producto p WHERE p.tipo = :tipo AND p.disponible = :disponible",
                    Producto.class);
            query.setParameter("tipo", tipo);
            query.setParameter("disponible", disponible);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Producto> buscarPorIngrediente(Long idIngrediente) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery(
                    "SELECT DISTINCT p FROM Producto p JOIN p.ingredientes i WHERE i.id = :idIngrediente",
                    Producto.class);
            query.setParameter("idIngrediente", idIngrediente);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
