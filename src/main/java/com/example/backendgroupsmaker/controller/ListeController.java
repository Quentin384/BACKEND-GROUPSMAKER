package com.example.backendgroupsmaker.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backendgroupsmaker.model.Liste;
import com.example.backendgroupsmaker.model.Utilisateur;
import com.example.backendgroupsmaker.repository.ListeRepository;
import com.example.backendgroupsmaker.service.ListeAccessService;

import jakarta.validation.Valid;

/**
 * Endpoints de gestion des listes.
 */
@RestController
@RequestMapping("/api/listes")
public class ListeController {

    @Autowired
    private ListeRepository listeRepo;

    @Autowired
    private ListeAccessService accessService;

    @GetMapping
    public List<Liste> getListes(Principal principal) {
        Utilisateur current = accessService.getCurrentUser(principal);
        List<Liste> listes = listeRepo.findByUtilisateurId(current.getId());
        System.out.println("\u25B6\u25B6 getListes user=" + current.getUsername() + " -> " + listes.size() + " listes");
        return listes;
    }

    @PostMapping
    public ResponseEntity<Liste> createListe(Principal principal, @RequestBody @Valid Liste liste) {
        Utilisateur current = accessService.getCurrentUser(principal);
        liste.setUtilisateur(current);
        Liste saved = listeRepo.save(liste);
        System.out.println("\u25B6\u25B6 createListe created id=" + saved.getId());
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{idListe}")
    public ResponseEntity<Void> deleteListe(Principal principal, @PathVariable Long idListe) {
        Liste liste = accessService.getOwnedListe(idListe, accessService.getCurrentUser(principal));
        listeRepo.delete(liste);
        System.out.println("\u25B6\u25B6 deleteListe id=" + idListe);
        return ResponseEntity.noContent().build();
    }
}
