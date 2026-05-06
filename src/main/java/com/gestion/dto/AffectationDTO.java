package com.gestion.dto;

import java.time.LocalDate;

/**
 * DTO Affectation — réponse complète avec infos employé + projet.
 */
public class AffectationDTO {

    private Long      id;
    private Long      employeId;
    private String    employeNom;
    private String    employePrenom;
    private Long      projetId;
    private String    projetNom;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    public AffectationDTO() {}

    public AffectationDTO(Long id,
                          Long employeId, String employeNom, String employePrenom,
                          Long projetId,  String projetNom,
                          LocalDate dateDebut, LocalDate dateFin) {
        this.id            = id;
        this.employeId     = employeId;
        this.employeNom    = employeNom;
        this.employePrenom = employePrenom;
        this.projetId      = projetId;
        this.projetNom     = projetNom;
        this.dateDebut     = dateDebut;
        this.dateFin       = dateFin;
    }

    // ── Getters / Setters ──────────────────────────────────────────
    public Long      getId()                         { return id; }
    public void      setId(Long id)                  { this.id = id; }
    public Long      getEmployeId()                  { return employeId; }
    public void      setEmployeId(Long id)           { this.employeId = id; }
    public String    getEmployeNom()                 { return employeNom; }
    public void      setEmployeNom(String n)         { this.employeNom = n; }
    public String    getEmployePrenom()              { return employePrenom; }
    public void      setEmployePrenom(String p)      { this.employePrenom = p; }
    public Long      getProjetId()                   { return projetId; }
    public void      setProjetId(Long id)            { this.projetId = id; }
    public String    getProjetNom()                  { return projetNom; }
    public void      setProjetNom(String n)          { this.projetNom = n; }
    public LocalDate getDateDebut()                  { return dateDebut; }
    public void      setDateDebut(LocalDate d)       { this.dateDebut = d; }
    public LocalDate getDateFin()                    { return dateFin; }
    public void      setDateFin(LocalDate d)         { this.dateFin = d; }
}