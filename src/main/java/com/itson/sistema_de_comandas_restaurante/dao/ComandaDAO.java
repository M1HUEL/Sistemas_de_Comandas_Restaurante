package com.itson.sistema_de_comandas_restaurante.dao;

import com.itson.sistema_de_comandas_restaurante.entidad.Comanda;
import com.itson.sistema_de_comandas_restaurante.entidad.EstadoComanda;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class ComandaDAO {

    private final EntityManagerFactory emf;

    public ComandaDAO() {
        this.emf = Persistence.createEntityManagerFactory("com.itson_Sistema_de_Comandas_Restaurante_jar_1.0-SNAPSHOTPU");
    }

    public Comanda crear(Comanda comanda) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(comanda);
            tx.commit();
            return comanda;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al crear la comanda: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Comanda actualizar(Comanda comanda) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Comanda comandaActualizada = em.merge(comanda);
            tx.commit();
            return comandaActualizada;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al actualizar la comanda: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Comanda comanda = em.find(Comanda.class, id);
            if (comanda != null) {
                em.remove(comanda);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al eliminar la comanda: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Comanda buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Comanda.class, id);
        } finally {
            em.close();
        }
    }

    public List<Comanda> listarTodas() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Comanda> query = em.createQuery("SELECT c FROM Comanda c", Comanda.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Comanda> buscarPorEstado(EstadoComanda estado) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Comanda> query = em.createQuery(
                    "SELECT c FROM Comanda c WHERE c.estado = :estado", Comanda.class);
            query.setParameter("estado", estado);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Comanda buscarPorFolio(String folio) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Comanda> query = em.createQuery(
                    "SELECT c FROM Comanda c WHERE c.folio = :folio", Comanda.class);
            query.setParameter("folio", folio);
            List<Comanda> resultados = query.getResultList();
            return resultados.isEmpty() ? null : resultados.get(0);
        } finally {
            em.close();
        }
    }

    public List<Comanda> buscarPorRangoFechas(LocalDate inicio, LocalDate fin) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Comanda> query = em.createQuery(
                    "SELECT c FROM Comanda c WHERE c.fechaHoraCreacion BETWEEN :inicio AND :fin", Comanda.class);
            query.setParameter("inicio", inicio.atStartOfDay());
            query.setParameter("fin", fin.atTime(23, 59, 59));
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public long contarComandasHoy() {
        EntityManager em = emf.createEntityManager();
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime inicio = today.atStartOfDay();
            LocalDateTime fin = today.atTime(LocalTime.MAX);

            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(c) FROM Comanda c WHERE c.fecha BETWEEN :inicio AND :fin", Long.class);
            query.setParameter("inicio", inicio);
            query.setParameter("fin", fin);

            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}
