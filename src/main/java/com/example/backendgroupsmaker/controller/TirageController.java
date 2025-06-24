package com.example.backendgroupsmaker.controller;

import java.net.URI;
import java.security.Principal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.example.backendgroupsmaker.repository.TirageRepository;
import com.example.backendgroupsmaker.service.ListeAccessService;

import jakarta.validation.Valid;

/**
 * Endpoints pour la gestion des tirages d'une liste.
 */
@RestController
@RequestMapping("/api/listes/{idListe}/tirages")
public class TirageController {

    @Autowired
    private TirageRepository tirageRepo;

    @Autowired
    private ListeAccessService accessService;

    @PostMapping
    public ResponseEntity<Tirage> addTirage(Principal principal,
                                            @PathVariable Long idListe,
                                            @RequestBody @Valid Tirage tirage) {
        System.out.println("\u25B6\u25B6 addTirage called for liste=" + idListe + ", groupes=" + tirage.getGroupes());
        Liste liste = accessService.getOwnedListe(idListe, accessService.getCurrentUser(principal));

        if (tirage.getGroupes() == null || tirage.getGroupes().isEmpty()) {
            System.out.println("\u26A0 addTirage: no groupes in payload");
            return ResponseEntity.badRequest().build();
        }

        tirage.setListe(liste);
        tirage.setDate(new Date());
        Tirage saved = tirageRepo.save(tirage);
        System.out.println("\u25B6\u25B6 addTirage saved id=" + saved.getId());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                          .path("/{id}")
                          .buildAndExpand(saved.getId())
                          .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Tirage>> getTirages(Principal principal, @PathVariable Long idListe) {
        accessService.getOwnedListe(idListe, accessService.getCurrentUser(principal));
        List<Tirage> tirages = tirageRepo.findByListeId(idListe);
        System.out.println("\u25B6\u25B6 getTirages for liste=" + idListe + " -> " + tirages.size() + " tirages");
        return ResponseEntity.ok(tirages);
    }

    @PatchMapping("/{tirageId}")
    public ResponseEntity<Tirage> validerTirage(Principal principal,
                                                @PathVariable Long idListe,
                                                @PathVariable Long tirageId) {
        accessService.getOwnedListe(idListe, accessService.getCurrentUser(principal));
        Tirage tirage = tirageRepo.findById(tirageId)
            .orElseThrow(() -> new RuntimeException("Tirage non trouvé : " + tirageId));
        tirage.setValide(true);
        Tirage updated = tirageRepo.save(tirage);
        System.out.println("\u25B6\u25B6 validerTirage id=" + tirageId);
        return ResponseEntity.ok(updated);
    }
}
