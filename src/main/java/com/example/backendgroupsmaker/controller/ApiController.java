package com.example.backendgroupsmaker.controller;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backendgroupsmaker.model.Liste;
import com.example.backendgroupsmaker.model.Personne;
import com.example.backendgroupsmaker.model.Tirage;
import com.example.backendgroupsmaker.model.Utilisateur;
import com.example.backendgroupsmaker.repository.ListeRepository;
import com.example.backendgroupsmaker.repository.PersonneRepository;
import com.example.backendgroupsmaker.repository.TirageRepository;
import com.example.backendgroupsmaker.repository.UtilisateurRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ListeRepository listeRepo;

    @Autowired
    private PersonneRepository personneRepo;

    @Autowired
    private TirageRepository tirageRepo;

    @Autowired
    private UtilisateurRepository utilisateurRepo;

    @GetMapping("/listes")
    public List<Liste> getListes(Principal principal) {
        Utilisateur current = getCurrentUser(principal);
        return listeRepo.findByUtilisateurId(current.getId());
    }

    @PostMapping("/listes")
    public ResponseEntity<Liste> createListe(Principal principal, @RequestBody @Valid Liste liste) {
        Utilisateur current = getCurrentUser(principal);
        liste.setUtilisateur(current);
        return ResponseEntity.ok(listeRepo.save(liste));
    }

    @DeleteMapping("/listes/{idListe}")
    public ResponseEntity<Void> deleteListe(Principal principal, @PathVariable Long idListe) {
        Utilisateur current = getCurrentUser(principal);
        Liste liste = getOwnedListe(idListe, current);
        listeRepo.delete(liste);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/listes/{idListe}/personnes")
    public ResponseEntity<Personne> addPersonne(Principal principal, @PathVariable Long idListe, @RequestBody @Valid Personne p) {
        Liste liste = getOwnedListe(idListe, getCurrentUser(principal));
        p.setListe(liste);
        return ResponseEntity.ok(personneRepo.save(p));
    }

    @GetMapping("/listes/{idListe}/personnes")
    public ResponseEntity<List<Personne>> getPersonnes(Principal principal, @PathVariable Long idListe) {
        Liste liste = getOwnedListe(idListe, getCurrentUser(principal));
        return ResponseEntity.ok(personneRepo.findByListeId(idListe));
    }

    /** ✅ Route correcte pour enregistrer un tirage */
    @PostMapping("/listes/{idListe}/tirages")
    public ResponseEntity<?> addTirage(
            Principal principal,
            @PathVariable Long idListe,
            @RequestBody Tirage tirage
    ) {
        Utilisateur current = getCurrentUser(principal);
        Liste liste = getOwnedListe(idListe, current);

        // Vérifie que les groupes sont bien présents
        if (tirage.getGroupes() == null || tirage.getGroupes().isEmpty()) {
            return ResponseEntity.badRequest().body("Le tirage ne contient aucun groupe");
        }

        // Liaison à la liste
        tirage.setListe(liste);
        tirage.setDate(new Date());

        // Forcer la sérialisation du JSON
        try {
            String json = new ObjectMapper().writeValueAsString(tirage.getGroupes());
            tirage.setGroupesJson(json);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur de sérialisation JSON");
        }

        tirageRepo.save(tirage);
        return ResponseEntity.ok("Tirage enregistré avec succès");
    }

    @GetMapping("/listes/{idListe}/tirages")
    public ResponseEntity<List<Tirage>> getTirages(Principal principal, @PathVariable Long idListe) {
        Liste liste = getOwnedListe(idListe, getCurrentUser(principal));
        return ResponseEntity.ok(tirageRepo.findByListeId(idListe));
    }

    @PatchMapping("/listes/{idListe}/tirages/{tirageId}")
    public ResponseEntity<Tirage> validerTirage(Principal principal, @PathVariable Long idListe, @PathVariable Long tirageId) {
        Utilisateur current = getCurrentUser(principal);
        Liste liste = getOwnedListe(idListe, current);
        Tirage tirage = tirageRepo.findById(tirageId)
                .orElseThrow(() -> new RuntimeException("Tirage non trouvé : " + tirageId));

        if (!tirage.getListe().getId().equals(idListe)) {
            return ResponseEntity.badRequest().build();
        }

        tirage.setValide(true);
        return ResponseEntity.ok(tirageRepo.save(tirage));
    }

    // -- Méthodes utilitaires internes --

    private Utilisateur getCurrentUser(Principal principal) {
        return utilisateurRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + principal.getName()));
    }

    private Liste getOwnedListe(Long idListe, Utilisateur current) {
        Liste liste = listeRepo.findById(idListe)
                .orElseThrow(() -> new RuntimeException("Liste non trouvée : " + idListe));
        if (!liste.getUtilisateur().getId().equals(current.getId())) {
            throw new RuntimeException("Accès refusé à la liste");
        }
        return liste;
    }
}
