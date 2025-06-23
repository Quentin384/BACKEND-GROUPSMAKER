package com.example.backendgroupsmaker.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "personne")
public class Personne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 49)
    @Size(min = 4, max = 49)
    private String nom;

    @Min(1)
    @Max(99)
    @Column(nullable = false)
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genre genre;

    @Min(1)
    @Max(4)
    @Column(nullable = false)
    private Integer aisanceFrancais;

    @Column(name = "ancien_dwwm", nullable = false)
    private Boolean ancienDWWM;

    @Min(1)
    @Max(4)
    @Column(nullable = false)
    private Integer niveauTechnique;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Profil profil;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnoreProperties({ "personnes" })
    @JoinColumn(name = "liste_id", nullable = false)
    private Liste liste;

    // Getters & Setters...

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }

    public Integer getAisanceFrancais() { return aisanceFrancais; }
    public void setAisanceFrancais(Integer aisanceFrancais) { this.aisanceFrancais = aisanceFrancais; }

    public Boolean getAncienDWWM() { return ancienDWWM; }
    public void setAncienDWWM(Boolean ancienDWWM) { this.ancienDWWM = ancienDWWM; }

    public Integer getNiveauTechnique() { return niveauTechnique; }
    public void setNiveauTechnique(Integer niveauTechnique) { this.niveauTechnique = niveauTechnique; }

    public Profil getProfil() { return profil; }
    public void setProfil(Profil profil) { this.profil = profil; }

    public Liste getListe() { return liste; }
    public void setListe(Liste liste) { this.liste = liste; }
}

// Enums en dehors de la classe et SANS accents !

enum Genre {
    MASCULIN, FEMININ, NE_SE_PRONONCE_PAS
}

enum Profil {
    TIMIDE, RESERVE, A_L_AISE
}
