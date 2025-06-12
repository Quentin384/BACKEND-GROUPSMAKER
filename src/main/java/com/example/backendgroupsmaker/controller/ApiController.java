package com.example.backendgroupsmaker.controller;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
        Utilisateur user = utilisateurRepo.findByUsername(principal.getName())
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return listeRepo.findByUtilisateurId(user.getId());
    }

    @PostMapping("/listes")
    public ResponseEntity<Liste> createListe(Principal principal, @RequestBody Liste liste) {
        Utilisateur utilisateur = utilisateurRepo.findByUsername(principal.getName())
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        liste.setUtilisateur(utilisateur);
        Liste saved = listeRepo.save(liste);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/listes/{idListe}/personnes")
    public ResponseEntity<?> addPersonne(Principal principal, @PathVariable Long idListe, @RequestBody Personne p) {
        Liste liste = listeRepo.findById(idListe).orElse(null);
        if (liste == null || !liste.getUtilisateur().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }
        p.setListe(liste);
        personneRepo.save(p);
        return ResponseEntity.ok(p);
    }

    @GetMapping("/listes/{idListe}/personnes")
    public ResponseEntity<List<Personne>> getPersonnes(Principal principal, @PathVariable Long idListe) {
        Liste liste = listeRepo.findById(idListe).orElse(null);
        if (liste == null || !liste.getUtilisateur().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(personneRepo.findByListeId(idListe));
    }

    @PostMapping("/listes/{idListe}/tirages")
    public ResponseEntity<?> addTirage(Principal principal, @PathVariable Long idListe, @RequestBody Tirage tirage) {
        Liste liste = listeRepo.findById(idListe).orElse(null);
        if (liste == null || !liste.getUtilisateur().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }
        tirage.setListe(liste);
        tirage.setDate(new Date());
        tirageRepo.save(tirage);
        return ResponseEntity.ok(tirage);
    }

    @GetMapping("/listes/{idListe}/tirages")
    public ResponseEntity<List<Tirage>> getTirages(Principal principal, @PathVariable Long idListe) {
        Liste liste = listeRepo.findById(idListe).orElse(null);
        if (liste == null || !liste.getUtilisateur().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(tirageRepo.findByListeId(idListe));
    }
}
