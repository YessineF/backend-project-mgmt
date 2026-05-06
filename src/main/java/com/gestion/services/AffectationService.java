package com.gestion.services;

import com.gestion.dao.AffectationDAO;
import com.gestion.dao.EmployeDAO;
import com.gestion.dao.ProjetDAO;
import com.gestion.dto.AffectationDTO;
import com.gestion.models.Affectation;
import com.gestion.models.Employe;
import com.gestion.models.Projet;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AffectationService {

    private final AffectationDAO affectationDAO = new AffectationDAO();
    private final EmployeDAO     employeDAO     = new EmployeDAO();
    private final ProjetDAO      projetDAO      = new ProjetDAO();

    // ── Toutes les affectations ────────────────────────────────────
    public List<AffectationDTO> getAllAffectations() {
        return affectationDAO.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Affectations par projet ────────────────────────────────────
    public List<AffectationDTO> getByProjet(Long projetId) {
        return affectationDAO.findByProjetId(projetId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Affectations par employé ───────────────────────────────────
    public List<AffectationDTO> getByEmploye(Long employeId) {
        return affectationDAO.findByEmployeId(employeId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Créer une affectation ──────────────────────────────────────
    public AffectationDTO createAffectation(Long employeId, Long projetId,
                                            LocalDate dateDebut, LocalDate dateFin) {
        // Vérification doublon
        if (affectationDAO.exists(employeId, projetId)) {
            throw new IllegalArgumentException(
                    "Cet employé est déjà affecté à ce projet");
        }

        // Vérification existence employé et projet
        Employe employe = employeDAO.findById(employeId);
        Projet  projet  = projetDAO.findById(projetId);

        if (employe == null) throw new IllegalArgumentException("Employé introuvable");
        if (projet  == null) throw new IllegalArgumentException("Projet introuvable");

        // Validation des dates
        if (dateFin != null && dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException(
                    "La date de fin doit être après la date de début");
        }

        Affectation affectation = new Affectation();
        affectation.setEmploye(employe);
        affectation.setProjet(projet);
        affectation.setDateDebut(dateDebut);
        affectation.setDateFin(dateFin);

        return toDTO(affectationDAO.save(affectation));
    }

    // ── Supprimer une affectation ──────────────────────────────────
    public boolean deleteAffectation(Long id) {
        return affectationDAO.delete(id);
    }

    // ── Entity → DTO ──────────────────────────────────────────────
    private AffectationDTO toDTO(Affectation a) {
        return new AffectationDTO(
                a.getId(),
                a.getEmploye().getId(),
                a.getEmploye().getNom(),
                a.getEmploye().getPrenom(),
                a.getProjet().getId(),
                a.getProjet().getNom(),
                a.getDateDebut(),
                a.getDateFin()
        );
    }
}