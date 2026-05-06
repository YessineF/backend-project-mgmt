package com.gestion.dao;

import com.gestion.models.Categorie;
import com.gestion.utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

public class CategorieDAO {

    // ── Lister toutes les catégories ──────────────────────────────
    public List<Categorie> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Categorie c ORDER BY c.nom", Categorie.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // ── Trouver par ID ─────────────────────────────────────────────
    public Categorie findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Categorie.class, id);
        } finally {
            em.close();
        }
    }

    // ── Trouver par nom ────────────────────────────────────────────
    public Categorie findByNom(String nom) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Categorie c WHERE c.nom = :nom", Categorie.class)
                    .setParameter("nom", nom)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    // ── Créer ──────────────────────────────────────────────────────
    public Categorie save(Categorie categorie) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(categorie);
            em.getTransaction().commit();
            return categorie;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur création catégorie : " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // ── Modifier ───────────────────────────────────────────────────
    public Categorie update(Categorie categorie) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Categorie updated = em.merge(categorie);
            em.getTransaction().commit();
            return updated;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur modification catégorie : " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // ── Supprimer ──────────────────────────────────────────────────
    public boolean delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Categorie cat = em.find(Categorie.class, id);
            if (cat == null) return false;

            em.getTransaction().begin();
            em.remove(cat);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur suppression catégorie : " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // ── Vérifier si le nom existe ──────────────────────────────────
    public boolean nomExists(String nom) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(c) FROM Categorie c WHERE c.nom = :nom", Long.class)
                    .setParameter("nom", nom)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}