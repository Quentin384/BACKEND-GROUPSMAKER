package com.example.backendgroupsmaker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Tirage {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "liste_id", nullable = false)
    private Liste liste;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "valide", nullable = false)
    private boolean valide = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "groupes_json", columnDefinition = "jsonb", nullable = false)
    private String groupesJson;

    @Transient
    private List<Groupe> groupes;

    // --- Constructeurs ---

    public Tirage() {
    }

    public Tirage(Liste liste, Date date, List<Groupe> groupes) {
        this.liste = liste;
        this.date = date;
        this.groupes = groupes;
    }

    // --- Callbacks JPA ---

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

    @PrePersist
    @PreUpdate
    private void onSave() {
        try {
            this.groupesJson = MAPPER.writeValueAsString(this.groupes);
        } catch (Exception e) {
            throw new RuntimeException("Erreur sérialisation JSON de Tirage", e);
        }
    }

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public Liste getListe() {
        return liste;
    }

    public void setListe(Liste liste) {
        this.liste = liste;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isValide() {
        return valide;
    }

    public void setValide(boolean valide) {
        this.valide = valide;
    }

    public String getGroupesJson() {
        return groupesJson;
    }

    public void setGroupesJson(String groupesJson) {
        this.groupesJson = groupesJson;
    }

    public List<Groupe> getGroupes() {
        return groupes;
    }

    public void setGroupes(List<Groupe> groupes) {
        this.groupes = groupes;
    }

    @Override
    public String toString() {
        return "Tirage{" +
               "id=" + id +
               ", date=" + date +
               ", valide=" + valide +
               ", listeId=" + (liste != null ? liste.getId() : null) +
               ", groupesJson='" + groupesJson + '\'' +
               '}';
    }
}
