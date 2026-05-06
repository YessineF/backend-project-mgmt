package com.gestion.dao;

import com.gestion.models.Projet;
import com.gestion.utils.JPAUtil;

import javax.persistence.EntityManager;
import java.util.List;

public class ProjetDAO {

    // ── Lister tous les projets ────────────────────────────────────
    public List<Projet> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Projet p ORDER BY p.nom", Projet.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // ── Trouver par ID ─────────────────────────────────────────────
    public Projet findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Projet.class, id);
        } finally {
            em.close();
        }
    }

    // ── Créer ──────────────────────────────────────────────────────
    public Projet save(Projet projet) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(projet);
            em.getTransaction().commit();
            return projet;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur création projet : " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // ── Modifier ───────────────────────────────────────────────────
    public Projet update(Projet projet) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Projet updated = em.merge(projet);
            em.getTransaction().commit();
            return updated;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur modification projet : " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // ── Supprimer ──────────────────────────────────────────────────
    public boolean delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Projet projet = em.find(Projet.class, id);
            if (projet == null) return false;

            em.getTransaction().begin();
            em.remove(projet);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur suppression projet : " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // ── Projets d'un employé spécifique ───────────────────────────
    public List<Projet> findByEmployeId(Long employeId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT a.projet FROM Affectation a " +
                                    "WHERE a.employe.id = :employeId " +
                                    "ORDER BY a.dateDebut DESC", Projet.class)
                    .setParameter("employeId", employeId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}