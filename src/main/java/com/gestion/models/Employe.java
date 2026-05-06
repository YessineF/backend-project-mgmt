package com.gestion.models;

import javax.persistence.*;

@Entity
@Table(name = "employes")
public class Employe {

    public enum Role { ADMIN, EMPLOYE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;   // BCrypt hash

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.EMPLOYE;

    // Relation ManyToOne → Categorie
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    // ── Constructeurs ──────────────────────────────────
    public Employe() {}

    // ── Getters / Setters ──────────────────────────────
    public Long getId()                      { return id; }
    public void setId(Long id)               { this.id = id; }
    public String getNom()                   { return nom; }
    public void setNom(String nom)           { this.nom = nom; }
    public String getPrenom()                { return prenom; }
    public void setPrenom(String prenom)     { this.prenom = prenom; }
    public String getEmail()                 { return email; }
    public void setEmail(String email)       { this.email = email; }
    public String getPassword()              { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole()                    { return role; }
    public void setRole(Role role)           { this.role = role; }
    public Categorie getCategorie()          { return categorie; }
    public void setCategorie(Categorie c)    { this.categorie = c; }
}