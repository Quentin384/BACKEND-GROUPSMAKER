package com.example.backendgroupsmaker.model;

import java.util.List;

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

@Entity
@Table(name = "liste")
public class Liste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    // Nombre de tirages (groupes) réalisés dans cette liste
    @Column(nullable = false)
    private int tirages = 0;

    // Association vers l'utilisateur propriétaire de la liste (relation ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    // Liste des personnes associées à cette liste (relation OneToMany)
    @OneToMany(mappedBy = "liste", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Personne> personnes;

    // Nouveau champ indiquant si la liste est partagée ou non
    // false par défaut
    @Column(nullable = false)
    private boolean partagee = false;

    // Getters et setters

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

    public boolean isPartagee() {
        return partagee;
    }

    public void setPartagee(boolean partagee) {
        this.partagee = partagee;
    }
}
