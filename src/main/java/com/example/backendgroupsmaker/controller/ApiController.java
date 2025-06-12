package com.example.backendgroupsmaker.controller;

import com.example.backendgroupsmaker.model.*;
import com.example.backendgroupsmaker.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ListeRepository listeRepo;

    @Autowired
    private PersonneRepository personneRepo;

    @Autowired
    private TirageRepository tirageRepo;

    // Récupérer toutes les listes de l'utilisateur connecté
    @GetMapping("/listes")
    public List<Liste> getListes(Principal principal) {
        return listeRepo.findByProprietaireUsername(principal.getName());
    }

    // Créer une liste pour l'utilisateur
    @PostMapping("/listes")
    public ResponseEntity<Liste> createListe(Principal principal, @RequestBody Liste liste) {
        liste.setProprietaire(new Utilisateur());
        liste.getProprietaire().setUsername(principal.getName());
        Liste saved = listeRepo.save(liste);
        return ResponseEntity.ok(saved);
    }

    // Ajouter une personne dans une liste (propriété de l'utilisateur)
    @PostMapping("/listes/{idListe}/personnes")
    public ResponseEntity<?> addPersonne(Principal principal, @PathVariable Long idListe, @RequestBody Personne p) {
        Liste liste = listeRepo.findById(idListe).orElse(null);
        if (liste == null || !liste.getProprietaire().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }
        p.setListe(liste);
        personneRepo.save(p);
        return ResponseEntity.ok(p);
    }

    // Récupérer les personnes d’une liste
    @GetMapping("/listes/{idListe}/personnes")
    public ResponseEntity<List<Personne>> getPersonnes(Principal principal, @PathVariable Long idListe) {
        Liste liste = listeRepo.findById(idListe).orElse(null);
        if (liste == null || !liste.getProprietaire().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(personneRepo.findByListeId(idListe));
    }

    // Ajouter un tirage à une liste
    @PostMapping("/listes/{idListe}/tirages")
    public ResponseEntity<?> addTirage(Principal principal, @PathVariable Long idListe, @RequestBody Tirage tirage) {
        Liste liste = listeRepo.findById(idListe).orElse(null);
        if (liste == null || !liste.getProprietaire().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }
        tirage.setListe(liste);
        tirage.setDate(new Date());
        tirageRepo.save(tirage);
        return ResponseEntity.ok(tirage);
    }

    // Récupérer les tirages d’une liste
    @GetMapping("/listes/{idListe}/tirages")
    public ResponseEntity<List<Tirage>> getTirages(Principal principal, @PathVariable Long idListe) {
        Liste liste = listeRepo.findById(idListe).orElse(null);
        if (liste == null || !liste.getProprietaire().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(tirageRepo.findByListeId(idListe));
    }
}
