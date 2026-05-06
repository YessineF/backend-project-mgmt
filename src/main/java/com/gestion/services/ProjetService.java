package com.gestion.services;

import com.gestion.dao.AffectationDAO;
import com.gestion.dao.ProjetDAO;
import com.gestion.dto.ProjetDTO;
import com.gestion.dto.ProjetInputDTO;
import com.gestion.models.Projet;

import java.util.List;
import java.util.stream.Collectors;

public class ProjetService {

    private final ProjetDAO       projetDAO       = new ProjetDAO();
    private final AffectationDAO  affectationDAO  = new AffectationDAO();

    // ── Lister tous les projets → DTOs ────────────────────────────
    public List<ProjetDTO> getAllProjets() {
        return projetDAO.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Projets d'un employé ───────────────────────────────────────
    public List<ProjetDTO> getProjetsByEmploye(Long employeId) {
        return projetDAO.findByEmployeId(employeId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Trouver par ID → DTO ───────────────────────────────────────
    public ProjetDTO getProjetById(Long id) {
        Projet projet = projetDAO.findById(id);
        return projet != null ? toDTO(projet) : null;
    }

    // ── Créer un projet ────────────────────────────────────────────
    public ProjetDTO createProjet(ProjetInputDTO input) {
        Projet projet = toEntity(input);
        return toDTO(projetDAO.save(projet));
    }

    // ── Modifier un projet ─────────────────────────────────────────
    public ProjetDTO updateProjet(Long id, ProjetInputDTO input) {
        Projet existing = projetDAO.findById(id);
        if (existing == null) return null;

        existing.setNom(input.getNom());
        existing.setDescription(input.getDescription());
        existing.setBudget(input.getBudget());

        if (input.getStatut() != null) {
            existing.setStatut(Projet.Statut.valueOf(input.getStatut()));
        }

        return toDTO(projetDAO.update(existing));
    }

    // ── Supprimer un projet ────────────────────────────────────────
    public boolean deleteProjet(Long id) {
        return projetDAO.delete(id);
    }

    // ── Convertir Entity → DTO ─────────────────────────────────────
    public ProjetDTO toDTO(Projet p) {
        // Compte le nombre d'employés affectés à ce projet
        int nbEmployes = affectationDAO.countByProjetId(p.getId());

        return new ProjetDTO(
                p.getId(),
                p.getNom(),
                p.getDescription(),
                p.getBudget(),
                p.getStatut().name(),
                nbEmployes
        );
    }

    // ── Convertir InputDTO → Entity ────────────────────────────────
    private Projet toEntity(ProjetInputDTO input) {
        Projet projet = new Projet();
        projet.setNom(input.getNom());
        projet.setDescription(input.getDescription());
        projet.setBudget(input.getBudget());

        if (input.getStatut() != null) {
            projet.setStatut(Projet.Statut.valueOf(input.getStatut()));
        }

        return projet;
    }
}