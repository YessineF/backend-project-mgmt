package com.gestion.dto;

import java.math.BigDecimal;

public class ProjetInputDTO {

    private String     nom;
    private String     description;
    private BigDecimal budget;
    private String     statut;

    public ProjetInputDTO() {}

    // ── Getters / Setters ──────────────────────────────────────────
    public String     getNom()                   { return nom; }
    public void       setNom(String nom)         { this.nom = nom; }
    public String     getDescription()           { return description; }
    public void       setDescription(String d)   { this.description = d; }
    public BigDecimal getBudget()                { return budget; }
    public void       setBudget(BigDecimal b)    { this.budget = b; }
    public String     getStatut()                { return statut; }
    public void       setStatut(String s)        { this.statut = s; }
}