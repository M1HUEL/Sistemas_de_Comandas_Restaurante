package com.itson.sistema_de_comandas_restaurante.dao;

import com.itson.sistema_de_comandas_restaurante.entidad.ClienteFrecuente;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class ClienteFrecuenteDAO {

    private final EntityManagerFactory emf;

    public ClienteFrecuenteDAO() {
        this.emf = Persistence.createEntityManagerFactory("com.itson_Sistema_de_Comandas_Restaurante_jar_1.0-SNAPSHOTPU");
    }

    public ClienteFrecuente crear(ClienteFrecuente cliente) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(cliente);
            tx.commit();
            return cliente;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al crear el cliente frecuente: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public ClienteFrecuente actualizar(ClienteFrecuente cliente) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            ClienteFrecuente actualizado = em.merge(cliente);
            tx.commit();
            return actualizado;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al actualizar el cliente frecuente: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            ClienteFrecuente cliente = em.find(ClienteFrecuente.class, id);
            if (cliente != null) {
                em.remove(cliente);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al eliminar el cliente frecuente: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public ClienteFrecuente buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(ClienteFrecuente.class, id);
        } finally {
            em.close();
        }
    }

    public List<ClienteFrecuente> listarTodos() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<ClienteFrecuente> query = em.createQuery(
                    "SELECT c FROM ClienteFrecuente c", ClienteFrecuente.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<ClienteFrecuente> buscarPorCriterio(String criterio) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<ClienteFrecuente> query = em.createQuery(
                    "SELECT c FROM ClienteFrecuente c WHERE "
                    + "LOWER(c.nombreCompleto) LIKE :criterio OR "
                    + "c.telefono LIKE :criterio OR "
                    + "LOWER(c.correo) LIKE :criterio", ClienteFrecuente.class);
            query.setParameter("criterio", "%" + criterio.toLowerCase() + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public boolean tieneComandasAsociadas(Long idCliente) {
        EntityManager em = emf.createEntityManager();
        try {
            Long count = em.createQuery(
                    "SELECT COUNT(c) FROM Comanda c WHERE c.clienteFrecuente.id = :id", Long.class)
                    .setParameter("id", idCliente)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();

        }
    }

    public boolean existeTelefono(String telefono) {
        EntityManager em = emf.createEntityManager();
        return em.createQuery("SELECT COUNT(c) FROM ClienteFrecuente c WHERE c.telefono = :tel", Long.class)
                .setParameter("tel", telefono)
                .getSingleResult() > 0;
    }

    public boolean existeTelefonoExcluyendoId(String telefono, Long id) {
        EntityManager em = emf.createEntityManager();
        return em.createQuery("SELECT COUNT(c) FROM ClienteFrecuente c WHERE c.telefono = :tel AND c.id <> :id", Long.class)
                .setParameter("tel", telefono)
                .setParameter("id", id)
                .getSingleResult() > 0;
    }
}
