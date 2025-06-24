package com.example.backendgroupsmaker.model;

import java.util.List;

/**
 * Représente un groupe de personnes, tel que renvoyé par le service Angular.
 * Chaque groupe comporte un nom et une liste de membres.
 */
public class Groupe {

    /** Nom du groupe (ex. "Groupe 1"). */
    private String nom;

    /** Membres appartenant à ce groupe. */
    private List<Personne> membres;

    // --- Constructeurs ---

    public Groupe() {
    }

    public Groupe(String nom, List<Personne> membres) {
        this.nom = nom;
        this.membres = membres;
    }

    // --- Getters & Setters ---

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

    @Override
    public String toString() {
        return "Groupe{nom='" + nom + "', membres=" + membres + '}';
    }
}
