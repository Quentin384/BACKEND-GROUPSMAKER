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
import com.example.backendgroupsmaker.model.Tirage;
import com.example.backendgroupsmaker.model.Utilisateur;
import com.example.backendgroupsmaker.repository.ListeRepository;
import com.example.backendgroupsmaker.repository.TirageRepository;
import com.example.backendgroupsmaker.repository.UtilisateurRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ListeRepository listeRepo;

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
    public ResponseEntity<Liste> createListe(Principal principal,
                                             @RequestBody @Valid Liste liste) {
        Utilisateur current = getCurrentUser(principal);
        liste.setUtilisateur(current);
        Liste saved = listeRepo.save(liste);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/listes/{idListe}")
    public ResponseEntity<Void> deleteListe(Principal principal,
                                            @PathVariable Long idListe) {
        Liste liste = getOwnedListe(idListe, getCurrentUser(principal));
        listeRepo.delete(liste);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/listes/{idListe}/tirages")
    public ResponseEntity<Tirage> addTirage(Principal principal,
                                            @PathVariable Long idListe,
                                            @RequestBody @Valid Tirage tirage) {
        Utilisateur current = getCurrentUser(principal);
        Liste liste = getOwnedListe(idListe, current);

        if (tirage.getGroupes() == null || tirage.getGroupes().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        tirage.setListe(liste);
        tirage.setDate(new Date());
        // Sérialisation JSON gérée par @PrePersist/@PreUpdate de l'entité Tirage

        Tirage saved = tirageRepo.save(tirage);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                          .path("/{id}")
                          .buildAndExpand(saved.getId())
                          .toUri();

        return ResponseEntity.created(location)
                             .body(saved);
    }

    @GetMapping("/listes/{idListe}/tirages")
    public ResponseEntity<List<Tirage>> getTirages(Principal principal,
                                                   @PathVariable Long idListe) {
        Liste liste = getOwnedListe(idListe, getCurrentUser(principal));
        return ResponseEntity.ok(tirageRepo.findByListeId(idListe));
    }

    @PatchMapping("/listes/{idListe}/tirages/{tirageId}")
    public ResponseEntity<Tirage> validerTirage(Principal principal,
                                                @PathVariable Long idListe,
                                                @PathVariable Long tirageId) {
        Utilisateur current = getCurrentUser(principal);
        getOwnedListe(idListe, current);  // vérifie l’appartenance

        Tirage tirage = tirageRepo.findById(tirageId)
            .orElseThrow(() -> new RuntimeException("Tirage non trouvé : " + tirageId));

        tirage.setValide(true);
        Tirage updated = tirageRepo.save(tirage);
        return ResponseEntity.ok(updated);
    }

    // --- méthodes utilitaires ---

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
