package com.gestion.dto;

/**
 * DTO Employé — ce qu'on envoie au frontend.
 * On n'expose JAMAIS le mot de passe hashé dans la réponse JSON.
 */
public class EmployeDTO {

    private Long   id;
    private String nom;
    private String prenom;
    private String email;
    private String role;
    private Long   categorieId;
    private String categorieNom;

    // Constructeur vide (requis par Gson)
    public EmployeDTO() {}

    // Constructeur complet
    public EmployeDTO(Long id, String nom, String prenom,
                      String email, String role,
                      Long categorieId, String categorieNom) {
        this.id           = id;
        this.nom          = nom;
        this.prenom       = prenom;
        this.email        = email;
        this.role         = role;
        this.categorieId  = categorieId;
        this.categorieNom = categorieNom;
    }

    // ── Getters / Setters ──────────────────────────────
    public Long   getId()                      { return id; }
    public void   setId(Long id)               { this.id = id; }
    public String getNom()                     { return nom; }
    public void   setNom(String nom)           { this.nom = nom; }
    public String getPrenom()                  { return prenom; }
    public void   setPrenom(String prenom)     { this.prenom = prenom; }
    public String getEmail()                   { return email; }
    public void   setEmail(String email)       { this.email = email; }
    public String getRole()                    { return role; }
    public void   setRole(String role)         { this.role = role; }
    public Long   getCategorieId()             { return categorieId; }
    public void   setCategorieId(Long id)      { this.categorieId = id; }
    public String getCategorieNom()            { return categorieNom; }
    public void   setCategorieNom(String nom)  { this.categorieNom = nom; }
}