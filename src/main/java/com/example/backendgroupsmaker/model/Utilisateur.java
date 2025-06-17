package com.example.backendgroupsmaker.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @JsonIgnore // ⛔ Ne jamais exposer le mot de passe
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role = "USER";

    @JsonIgnore // ❌ Ne pas exposer les listes dans /signup ou /login
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Liste> listes;

    // Constructeurs
    public Utilisateur() {}

    public Utilisateur(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<Liste> getListes() {
        return listes;
    }

    public void setListes(Set<Liste> listes) {
        this.listes = listes;
    }
}
