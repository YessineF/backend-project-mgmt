package com.gestion.services;

import com.gestion.dao.EmployeDAO;
import com.gestion.dto.EmployeDTO;
import com.gestion.dto.EmployeInputDTO;
import com.gestion.models.Categorie;
import com.gestion.models.Employe;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Employé — logique métier entre Servlet et DAO.
 * Gère le hashage des mots de passe et la conversion Entity ↔ DTO.
 */
public class EmployeService {

    private final EmployeDAO employeDAO = new EmployeDAO();

    // ── Authentification ───────────────────────────────────────────
    /**
     * Vérifie email + mot de passe.
     * @return l'employé si valide, null sinon
     */
    public Employe authenticate(String email, String password) {
        Employe employe = employeDAO.findByEmail(email);

        // Vérifie que l'employé existe et que le mot de passe correspond
        if (employe == null) return null;
        if (!BCrypt.checkpw(password, employe.getPassword())) return null;

        return employe;
    }

    // ── Lister tous les employés → liste de DTOs ──────────────────
    public List<EmployeDTO> getAllEmployes() {
        return employeDAO.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Trouver un employé par ID → DTO ───────────────────────────
    public EmployeDTO getEmployeById(Long id) {
        Employe employe = employeDAO.findById(id);
        return employe != null ? toDTO(employe) : null;
    }

    // ── Créer un employé ───────────────────────────────────────────
    public EmployeDTO createEmploye(Employe employe) {
        // Vérification email unique
        if (employeDAO.emailExists(employe.getEmail())) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }

        // Hash du mot de passe avant sauvegarde
        String hashed = BCrypt.hashpw(employe.getPassword(), BCrypt.gensalt(10));
        employe.setPassword(hashed);

        return toDTO(employeDAO.save(employe));
    }

    // ── Modifier un employé ────────────────────────────────────────
    public EmployeDTO updateEmploye(Long id, Employe updated) {
        Employe existing = employeDAO.findById(id);
        if (existing == null) return null;

        existing.setNom(updated.getNom());
        existing.setPrenom(updated.getPrenom());
        existing.setEmail(updated.getEmail());

        // Ne re-hashe que si un nouveau mot de passe est fourni
        if (updated.getPassword() != null && !updated.getPassword().isBlank()) {
            existing.setPassword(
                    BCrypt.hashpw(updated.getPassword(), BCrypt.gensalt(10))
            );
        }

        if (updated.getRole() != null) {
            existing.setRole(updated.getRole());
        }

        if (updated.getCategorie() != null) {
            existing.setCategorie(updated.getCategorie());
        }

        return toDTO(employeDAO.update(existing));
    }

    // ── Supprimer un employé ───────────────────────────────────────
    public boolean deleteEmploye(Long id) {
        return employeDAO.delete(id);
    }

    // ── Convertir Entity → DTO (supprime le password) ─────────────
    public EmployeDTO toDTO(Employe e) {
        Long   catId  = e.getCategorie() != null ? e.getCategorie().getId()  : null;
        String catNom = e.getCategorie() != null ? e.getCategorie().getNom() : null;

        return new EmployeDTO(
                e.getId(),
                e.getNom(),
                e.getPrenom(),
                e.getEmail(),
                e.getRole().name(),
                catId,
                catNom
        );
    }

    // ── Convertir DTO → Entity (utilisé pour les requêtes POST/PUT) ─
    public Employe toEntity(EmployeInputDTO input) {
        Employe employe = new Employe();
        employe.setNom(input.getNom());
        employe.setPrenom(input.getPrenom());
        employe.setEmail(input.getEmail());
        employe.setPassword(input.getPassword());

        if (input.getRole() != null) {
            employe.setRole(Employe.Role.valueOf(input.getRole()));
        }

        if (input.getCategorieId() != null) {
            Categorie cat = new Categorie();
            cat.setId(input.getCategorieId());
            employe.setCategorie(cat);
        }

        return employe;
    }
}