package com.gestion.models;

import javax.persistence.*;

@Entity
@Table(name = "categories")
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    // ── Constructeurs ──────────────────────────────────
    public Categorie() {}
    public Categorie(String nom) { this.nom = nom; }

    // ── Getters / Setters ──────────────────────────────
    public Long getId()           { return id; }
    public void setId(Long id)    { this.id = id; }
    public String getNom()        { return nom; }
    public void setNom(String nom){ this.nom = nom; }
}