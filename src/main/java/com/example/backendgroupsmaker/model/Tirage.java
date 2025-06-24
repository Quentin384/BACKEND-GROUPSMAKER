package com.example.backendgroupsmaker.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.List;

/**
 * Entité représentant un tirage :
 * stocke le JSONB brut en base et gère la (dé)sérialisation.
 */
@Entity
@Table(name = "tirage")
public class Tirage {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "liste_id", nullable = false)
    @JsonIgnore
    private Liste liste;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private boolean valide;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "groupes_json", columnDefinition = "jsonb", nullable = false)
    @JsonIgnore
    private String groupesJson;

    /** Champ exposé en JSON, injecté par Jackson au POST */
    @Transient
    @JsonProperty("groupes")
    private List<Groupe> groupes;

    public Tirage() {
        // pour JPA
    }

    /**
     * Pour Jackson : binder groupes, date et valide depuis le JSON.
     * Nécessite l'import de @JsonCreator.
     */
    @JsonCreator
    public Tirage(
        @JsonProperty("groupes") List<Groupe> groupes,
        @JsonProperty("date") Date date,
        @JsonProperty("valide") boolean valide
    ) {
        this.groupes = groupes;
        this.date = date;
        this.valide = valide;
    }

    @PostLoad
    private void onLoad() {
        try {
            this.groupes = MAPPER.readValue(
                this.groupesJson,
                new TypeReference<List<Groupe>>() {}
            );
        } catch (Exception e) {
            throw new RuntimeException("Erreur désérialisation JSON de Tirage", e);
        }
    }

    @PrePersist @PreUpdate
    private void onSave() {
        try {
            this.groupesJson = MAPPER.writeValueAsString(this.groupes);
        } catch (Exception e) {
            throw new RuntimeException("Erreur sérialisation JSON de Tirage", e);
        }
    }

    // --- Getters & Setters ---

    public Long getId() { return id; }

    public Liste getListe() { return liste; }
    public void setListe(Liste liste) { this.liste = liste; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public boolean isValide() { return valide; }
    public void setValide(boolean valide) { this.valide = valide; }

    public List<Groupe> getGroupes() { return groupes; }
    public void setGroupes(List<Groupe> groupes) { this.groupes = groupes; }

    @Override
    public String toString() {
        return "Tirage{" +
               "id=" + id +
               ", date=" + date +
               ", valide=" + valide +
               ", groupes=" + groupes +
               '}';
    }
}
