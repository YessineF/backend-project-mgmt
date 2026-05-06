package com.gestion.models;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "affectations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"employe_id","projet_id"}))
public class Affectation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "projet_id", nullable = false)
    private Projet projet;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    // ── Constructeurs ──────────────────────────────────
    public Affectation() {}

    // ── Getters / Setters ──────────────────────────────
    public Long getId()                     { return id; }
    public void setId(Long id)              { this.id = id; }
    public Employe getEmploye()             { return employe; }
    public void setEmploye(Employe e)       { this.employe = e; }
    public Projet getProjet()               { return projet; }
    public void setProjet(Projet p)         { this.projet = p; }
    public LocalDate getDateDebut()         { return dateDebut; }
    public void setDateDebut(LocalDate d)   { this.dateDebut = d; }
    public LocalDate getDateFin()           { return dateFin; }
    public void setDateFin(LocalDate d)     { this.dateFin = d; }
}