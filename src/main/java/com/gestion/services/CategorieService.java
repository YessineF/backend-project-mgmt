package com.gestion.services;

import com.gestion.dao.CategorieDAO;
import com.gestion.models.Categorie;

import java.util.List;

public class CategorieService {

    private final CategorieDAO categorieDAO = new CategorieDAO();

    // ── Lister toutes les catégories ──────────────────────────────
    public List<Categorie> getAllCategories() {
        return categorieDAO.findAll();
    }

    // ── Trouver par ID ─────────────────────────────────────────────
    public Categorie getCategorieById(Long id) {
        return categorieDAO.findById(id);
    }

    // ── Créer une catégorie ────────────────────────────────────────
    public Categorie createCategorie(String nom) {
        if (categorieDAO.nomExists(nom)) {
            throw new IllegalArgumentException("Catégorie déjà existante : " + nom);
        }
        Categorie cat = new Categorie(nom);
        return categorieDAO.save(cat);
    }

    // ── Modifier une catégorie ─────────────────────────────────────
    public Categorie updateCategorie(Long id, String nouveauNom) {
        Categorie cat = categorieDAO.findById(id);
        if (cat == null) return null;

        cat.setNom(nouveauNom);
        return categorieDAO.update(cat);
    }

    // ── Supprimer une catégorie ────────────────────────────────────
    public boolean deleteCategorie(Long id) {
        return categorieDAO.delete(id);
    }
}