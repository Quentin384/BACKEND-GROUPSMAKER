package com.example.backendgroupsmaker.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Représente une liste de personnes et ses tirages associés.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "liste")
public class Liste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    /** Nombre de tirages réalisés */
    @Column(nullable = false)
    private int tirages = 0;

    /** Utilisateur propriétaire — ignoré en sortie JSON pour éviter les boucles */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    @JsonIgnore
    private Utilisateur utilisateur;

    /** Personnes associées */
    @OneToMany(
        mappedBy = "liste",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<Personne> personnes = new ArrayList<>();

    /** Tirages enregistrés */
    @OneToMany(
        mappedBy = "liste",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<Tirage> tiragesHistorique = new ArrayList<>();

    /** Indique si la liste est partagée */
    @Column(nullable = false)
    private boolean partagee = false;

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getTirages() {
        return tirages;
    }

    public void setTirages(int tirages) {
        this.tirages = tirages;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public List<Personne> getPersonnes() {
        return personnes;
    }

    public void setPersonnes(List<Personne> personnes) {
        this.personnes = personnes;
    }

    public List<Tirage> getTiragesHistorique() {
        return tiragesHistorique;
    }

    public void setTiragesHistorique(List<Tirage> tiragesHistorique) {
        this.tiragesHistorique = tiragesHistorique;
    }

    public boolean isPartagee() {
        return partagee;
    }

    public void setPartagee(boolean partagee) {
        this.partagee = partagee;
    }
}
