package com.example.backendgroupsmaker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Un tirage : on stocke en base le JSON brut,
 * et on le (dé)sérialise nous-mêmes.
 */
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Entity
@Table(name = "tirage")
public class Tirage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "liste_id", nullable = false)
    private Liste liste;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private boolean valide;

    /** Le JSONB tel que stocké en base */
    @Column(name = "groupes_json", columnDefinition = "jsonb", nullable = false)
    private String groupesJson;

    /** En mémoire : votre vraie liste de groupes */
    @Transient
    private List<Groupe> groupes;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Tirage() { }

    public Tirage(Liste liste, Date date, boolean valide, List<Groupe> groupes) {
        this.liste = liste;
        this.date = date;
        this.valide = valide;
        setGroupes(groupes);
    }

    /** Après chargement JPA, on désérialise */
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

    /** Avant insert/update, on sérialise */
    @PrePersist @PreUpdate
    private void onSave() {
        try {
            this.groupesJson = MAPPER.writeValueAsString(this.groupes);
        } catch (Exception e) {
            throw new RuntimeException("Erreur sérialisation JSON de Tirage", e);
        }
    }

    // --- getters / setters ---

    public Long getId() { return id; }

    public Liste getListe() { return liste; }
    public void setListe(Liste liste) { this.liste = liste; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public boolean isValide() { return valide; }
    public void setValide(boolean valide) { this.valide = valide; }

    public String getGroupesJson() { return groupesJson; }
    public void setGroupesJson(String groupesJson) { this.groupesJson = groupesJson; }

    public List<Groupe> getGroupes() { return groupes; }
    public void setGroupes(List<Groupe> groupes) {
        this.groupes = groupes;
        try {
            this.groupesJson = MAPPER.writeValueAsString(groupes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
