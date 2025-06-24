package com.example.backendgroupsmaker.controller;

import java.security.Principal;
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
import com.example.backendgroupsmaker.repository.PersonneRepository;
import com.example.backendgroupsmaker.service.ListeAccessService;

import jakarta.validation.Valid;

/**
 * Endpoints pour la gestion des personnes d'une liste.
 */
@RestController
@RequestMapping("/api/listes/{idListe}/personnes")
public class PersonneController {

    @Autowired
    private PersonneRepository personneRepo;

    @Autowired
    private ListeAccessService accessService;

    @GetMapping
    public ResponseEntity<List<Personne>> getPersonnes(Principal principal, @PathVariable Long idListe) {
        accessService.getOwnedListe(idListe, accessService.getCurrentUser(principal));
        List<Personne> personnes = personneRepo.findByListeId(idListe);
        System.out.println("\u25B6\u25B6 getPersonnes for liste=" + idListe + " -> " + personnes.size() + " personnes");
        return ResponseEntity.ok(personnes);
    }

    @PostMapping
    public ResponseEntity<Personne> addPersonne(Principal principal,
                                                @PathVariable Long idListe,
                                                @RequestBody @Valid Personne personne) {
        Liste liste = accessService.getOwnedListe(idListe, accessService.getCurrentUser(principal));
        personne.setListe(liste);
        Personne saved = personneRepo.save(personne);
        System.out.println("\u25B6\u25B6 addPersonne name=" + saved.getNom() + " to liste=" + idListe);
        return ResponseEntity.ok(saved);
    }
}
