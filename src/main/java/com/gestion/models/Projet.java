package com.gestion.models;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "projets")
public class Projet {

    public enum Statut { EN_COURS, TERMINE, EN_ATTENTE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 15, scale = 2)
    private BigDecimal budget;

    @Enumerated(EnumType.STRING)
    private Statut statut = Statut.EN_ATTENTE;

    // ── Constructeurs ──────────────────────────────────
    public Projet() {}

    // ── Getters / Setters ──────────────────────────────
    public Long getId()                  { return id; }
    public void setId(Long id)           { this.id = id; }
    public String getNom()               { return nom; }
    public void setNom(String nom)       { this.nom = nom; }
    public String getDescription()       { return description; }
    public void setDescription(String d) { this.description = d; }
    public BigDecimal getBudget()        { return budget; }
    public void setBudget(BigDecimal b)  { this.budget = b; }
    public Statut getStatut()            { return statut; }
    public void setStatut(Statut s)      { this.statut = s; }
}