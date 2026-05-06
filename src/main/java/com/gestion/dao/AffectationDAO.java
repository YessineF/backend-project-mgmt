package com.gestion.dao;

import com.gestion.models.Affectation;
import com.gestion.utils.JPAUtil;

import javax.persistence.EntityManager;
import java.util.List;

public class AffectationDAO {

    // ── Toutes les affectations ────────────────────────────────────
    public List<Affectation> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT a FROM Affectation a " +
                                    "JOIN FETCH a.employe " +
                                    "JOIN FETCH a.projet " +
                                    "ORDER BY a.dateDebut DESC", Affectation.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // ── Affectations d'un projet ───────────────────────────────────
    public List<Affectation> findByProjetId(Long projetId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT a FROM Affectation a " +
                                    "JOIN FETCH a.employe e " +
                                    "WHERE a.projet.id = :projetId " +
                                    "ORDER BY e.nom", Affectation.class)
                    .setParameter("projetId", projetId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // ── Affectations d'un employé ──────────────────────────────────
    public List<Affectation> findByEmployeId(Long employeId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT a FROM Affectation a " +
                                    "JOIN FETCH a.projet p " +
                                    "WHERE a.employe.id = :employeId " +
                                    "ORDER BY a.dateDebut DESC", Affectation.class)
                    .setParameter("employeId", employeId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // ── Compter les employés d'un projet ───────────────────────────
    public int countByProjetId(Long projetId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(a) FROM Affectation a WHERE a.projet.id = :projetId",
                            Long.class)
                    .setParameter("projetId", projetId)
                    .getSingleResult();
            return count.intValue();
        } finally {
            em.close();
        }
    }

    // ── Vérifier si une affectation existe déjà ───────────────────
    public boolean exists(Long employeId, Long projetId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(a) FROM Affectation a " +
                                    "WHERE a.employe.id = :eId AND a.projet.id = :pId",
                            Long.class)
                    .setParameter("eId", employeId)
                    .setParameter("pId", projetId)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    // ── Trouver par ID ─────────────────────────────────────────────
    public Affectation findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Affectation.class, id);
        } finally {
            em.close();
        }
    }

    // ── Créer une affectation ──────────────────────────────────────
    public Affectation save(Affectation affectation) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(affectation);
            em.getTransaction().commit();
            return affectation;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur création affectation : " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // ── Supprimer une affectation ──────────────────────────────────
    public boolean delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Affectation a = em.find(Affectation.class, id);
            if (a == null) return false;

            em.getTransaction().begin();
            em.remove(a);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur suppression affectation : " + e.getMessage());
        } finally {
            em.close();
        }
    }
}