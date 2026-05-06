package com.gestion.dto;

/**
 * DTO d'entrée — reçu depuis Angular dans le body JSON.
 * Séparé du DTO de sortie pour mieux contrôler ce qu'on accepte.
 */
public class EmployeInputDTO {

    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String role;
    private Long   categorieId;

    // Constructeur vide requis par Gson
    public EmployeInputDTO() {}

    // ── Getters / Setters ──────────────────────────────
    public String getNom()                   { return nom; }
    public void   setNom(String nom)         { this.nom = nom; }
    public String getPrenom()                { return prenom; }
    public void   setPrenom(String prenom)   { this.prenom = prenom; }
    public String getEmail()                 { return email; }
    public void   setEmail(String email)     { this.email = email; }
    public String getPassword()              { return password; }
    public void   setPassword(String p)      { this.password = p; }
    public String getRole()                  { return role; }
    public void   setRole(String role)       { this.role = role; }
    public Long   getCategorieId()           { return categorieId; }
    public void   setCategorieId(Long id)    { this.categorieId = id; }
}