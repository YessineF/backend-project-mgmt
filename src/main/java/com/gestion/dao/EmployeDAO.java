package com.gestion.dao;

import com.gestion.models.Employe;
import com.gestion.utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

/**
 * DAO Employé — toutes les opérations base de données.
 * Chaque méthode ouvre et ferme son propre EntityManager.
 */
public class EmployeDAO {

    // ── Trouver un employé par email ───────────────────────────────
    public Employe findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT e FROM Employe e WHERE e.email = :email", Employe.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;   // email inexistant → retourne null
        } finally {
            em.close();
        }
    }

    // ── Trouver un employé par ID ──────────────────────────────────
    public Employe findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Employe.class, id);
        } finally {
            em.close();
        }
    }

    // ── Lister tous les employés ───────────────────────────────────
    public List<Employe> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT e FROM Employe e ORDER BY e.nom", Employe.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // ── Créer un nouvel employé ────────────────────────────────────
    public Employe save(Employe employe) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(employe);
            em.getTransaction().commit();
            return employe;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur création employé : " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // ── Modifier un employé existant ───────────────────────────────
    public Employe update(Employe employe) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Employe updated = em.merge(employe);
            em.getTransaction().commit();
            return updated;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur modification employé : " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // ── Supprimer un employé ───────────────────────────────────────
    public boolean delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Employe employe = em.find(Employe.class, id);
            if (employe == null) return false;

            em.getTransaction().begin();
            em.remove(employe);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur suppression employé : " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // ── Vérifier si un email existe déjà ──────────────────────────
    public boolean emailExists(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(e) FROM Employe e WHERE e.email = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}