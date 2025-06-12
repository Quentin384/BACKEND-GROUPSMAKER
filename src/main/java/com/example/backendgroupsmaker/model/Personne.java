package com.example.backendgroupsmaker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "personne")
public class Personne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private Integer age;

    private Boolean ancienDWWM;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liste_id")
    private Liste liste;

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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Boolean getAncienDWWM() {
        return ancienDWWM;
    }

    public void setAncienDWWM(Boolean ancienDWWM) {
        this.ancienDWWM = ancienDWWM;
    }

    public Liste getListe() {
        return liste;
    }

    public void setListe(Liste liste) {
        this.liste = liste;
    }
}
