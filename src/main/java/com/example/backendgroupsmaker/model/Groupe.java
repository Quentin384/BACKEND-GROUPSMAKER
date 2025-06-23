// src/main/java/com/example/backendgroupsmaker/model/Groupe.java
package com.example.backendgroupsmaker.model;

import java.util.List;

/**
 * Représente un groupe formé, 
 * tel que renvoyé par ton service Angular 
 * (nom + liste des membres).
 */
public class Groupe {

    /** Nom du groupe (ex : "Groupe 1") */
    private String nom;

    /** Liste des personnes dans ce groupe */
    private List<Personne> membres;

    public Groupe() { }

    public Groupe(String nom, List<Personne> membres) {
        this.nom = nom;
        this.membres = membres;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Personne> getMembres() {
        return membres;
    }
    public void setMembres(List<Personne> membres) {
        this.membres = membres;
    }
}
