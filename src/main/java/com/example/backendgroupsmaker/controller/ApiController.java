package com.example.backendgroupsmaker.controller;

import java.net.URI;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.backendgroupsmaker.model.Liste;
import com.example.backendgroupsmaker.model.Personne;
import com.example.backendgroupsmaker.model.Tirage;
import com.example.backendgroupsmaker.model.Utilisateur;
import com.example.backendgroupsmaker.repository.ListeRepository;
import com.example.backendgroupsmaker.repository.PersonneRepository;
import com.example.backendgroupsmaker.repository.TirageRepository;
import com.example.backendgroupsmaker.repository.UtilisateurRepository;

import jakarta.validation.Valid;

/**
 * Contrôleur exposant les API de gestion des listes, personnes et tirages.
 */
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
        List<Liste> listes = listeRepo.findByUtilisateurId(current.getId());
        System.out.println("▶▶ getListes user=" + current.getUsername() + " -> " + listes.size() + " listes");
        return listes;
    }

    @PostMapping("/listes")
    public ResponseEntity<Liste> createListe(Principal principal,
                                             @RequestBody @Valid Liste liste) {
        Utilisateur current = getCurrentUser(principal);
        liste.setUtilisateur(current);
        Liste saved = listeRepo.save(liste);
        System.out.println("▶▶ createListe created id=" + saved.getId());
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/listes/{idListe}")
    public ResponseEntity<Void> deleteListe(Principal principal,
                                            @PathVariable Long idListe) {
        Liste liste = getOwnedListe(idListe, getCurrentUser(principal));
        listeRepo.delete(liste);
        System.out.println("▶▶ deleteListe id=" + idListe);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/listes/{idListe}/personnes")
    public ResponseEntity<List<Personne>> getPersonnes(Principal principal,
                                                       @PathVariable Long idListe) {
        getOwnedListe(idListe, getCurrentUser(principal));
        List<Personne> personnes = personneRepo.findByListeId(idListe);
        System.out.println("▶▶ getPersonnes for liste=" + idListe + " -> " + personnes.size() + " personnes");
        return ResponseEntity.ok(personnes);
    }

    @PostMapping("/listes/{idListe}/personnes")
    public ResponseEntity<Personne> addPersonne(Principal principal,
                                                @PathVariable Long idListe,
                                                @RequestBody @Valid Personne personne) {
        Liste liste = getOwnedListe(idListe, getCurrentUser(principal));
        personne.setListe(liste);
        Personne saved = personneRepo.save(personne);
        System.out.println("▶▶ addPersonne name=" + saved.getNom() + " to liste=" + idListe);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/listes/{idListe}/tirages")
    public ResponseEntity<Tirage> addTirage(Principal principal,
                                            @PathVariable Long idListe,
                                            @RequestBody @Valid Tirage tirage) {
        System.out.println("▶▶ addTirage called for liste=" + idListe + ", groupes=" + tirage.getGroupes());
        Utilisateur current = getCurrentUser(principal);
        Liste liste = getOwnedListe(idListe, current);

        if (tirage.getGroupes() == null || tirage.getGroupes().isEmpty()) {
            System.out.println("⚠ addTirage: no groupes in payload");
            return ResponseEntity.badRequest().build();
        }

        tirage.setListe(liste);
        tirage.setDate(new Date());
        Tirage saved = tirageRepo.save(tirage);
        System.out.println("▶▶ addTirage saved id=" + saved.getId());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                          .path("/{id}")
                          .buildAndExpand(saved.getId())
                          .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @GetMapping("/listes/{idListe}/tirages")
    public ResponseEntity<List<Tirage>> getTirages(Principal principal,
                                                   @PathVariable Long idListe) {
        getOwnedListe(idListe, getCurrentUser(principal));
        List<Tirage> tirages = tirageRepo.findByListeId(idListe);
        System.out.println("▶▶ getTirages for liste=" + idListe + " -> " + tirages.size() + " tirages");
        return ResponseEntity.ok(tirages);
    }

    @PatchMapping("/listes/{idListe}/tirages/{tirageId}")
    public ResponseEntity<Tirage> validerTirage(Principal principal,
                                                @PathVariable Long idListe,
                                                @PathVariable Long tirageId) {
        getOwnedListe(idListe, getCurrentUser(principal));
        Tirage tirage = tirageRepo.findById(tirageId)
            .orElseThrow(() -> new RuntimeException("Tirage non trouvé : " + tirageId));
        tirage.setValide(true);
        Tirage updated = tirageRepo.save(tirage);
        System.out.println("▶▶ validerTirage id=" + tirageId);
        return ResponseEntity.ok(updated);
    }

    // --- méthodes utilitaires internes ---

    private Utilisateur getCurrentUser(Principal principal) {
        return utilisateurRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException(
                    "Utilisateur non trouvé : " + principal.getName()));
    }

    private Liste getOwnedListe(Long idListe, Utilisateur current) {
        Liste liste = listeRepo.findById(idListe)
                .orElseThrow(() -> new RuntimeException(
                    "Liste non trouvée : " + idListe));
        if (!liste.getUtilisateur().getId().equals(current.getId())) {
            throw new RuntimeException("Accès refusé à la liste");
        }
        return liste;
    }
}
