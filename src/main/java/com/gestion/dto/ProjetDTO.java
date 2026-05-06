package com.gestion.dto;

import java.math.BigDecimal;

/**
 * DTO Projet — réponse envoyée au frontend Angular.
 */
public class ProjetDTO {

    private Long       id;
    private String     nom;
    private String     description;
    private BigDecimal budget;
    private String     statut;
    private int        nombreEmployes; // nombre d'employés affectés

    public ProjetDTO() {}

    public ProjetDTO(Long id, String nom, String description,
                     BigDecimal budget, String statut, int nombreEmployes) {
        this.id             = id;
        this.nom            = nom;
        this.description    = description;
        this.budget         = budget;
        this.statut         = statut;
        this.nombreEmployes = nombreEmployes;
    }

    // ── Getters / Setters ──────────────────────────────────────────
    public Long       getId()                        { return id; }
    public void       setId(Long id)                 { this.id = id; }
    public String     getNom()                       { return nom; }
    public void       setNom(String nom)             { this.nom = nom; }
    public String     getDescription()               { return description; }
    public void       setDescription(String d)       { this.description = d; }
    public BigDecimal getBudget()                    { return budget; }
    public void       setBudget(BigDecimal b)        { this.budget = b; }
    public String     getStatut()                    { return statut; }
    public void       setStatut(String s)            { this.statut = s; }
    public int        getNombreEmployes()             { return nombreEmployes; }
    public void       setNombreEmployes(int n)       { this.nombreEmployes = n; }
}