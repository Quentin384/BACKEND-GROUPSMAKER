package com.example.backendgroupsmaker.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backendgroupsmaker.model.*;
import com.example.backendgroupsmaker.repository.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ListeRepository listeRepository;

    @Autowired
    private PersonneRepository personneRepository;

    @Autowired
    private TirageRepository tirageRepository;

    // 1. Voir tous les utilisateurs
    @GetMapping("/utilisateurs")
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    // 2. Supprimer un utilisateur
    @DeleteMapping("/utilisateurs/{id}")
    public ResponseEntity<?> deleteUtilisateur(@PathVariable Long id) {
        if (!utilisateurRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        utilisateurRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // 3. Statistiques générales
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        long nbUtilisateurs = utilisateurRepository.count();

        // Nombre de listes par utilisateur
        Map<Long, Long> listesParUtilisateur = listeRepository.findAll()
            .stream()
            .collect(Collectors.groupingBy(l -> l.getUtilisateur().getId(), Collectors.counting()));

        double avgListesParUtilisateur = listesParUtilisateur.values().stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0);

        // Nombre de personnes par liste en moyenne
        Map<Long, Long> personnesParListe = personneRepository.findAll()
            .stream()
            .collect(Collectors.groupingBy(p -> p.getListe().getId(), Collectors.counting()));

        double avgPersonnesParListe = personnesParListe.values().stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0);

        // Nombre de groupes (tirages) par liste en moyenne
        Map<Long, Long> tiragesParListe = tirageRepository.findAll()
            .stream()
            .collect(Collectors.groupingBy(t -> t.getListe().getId(), Collectors.counting()));

        double avgTiragesParListe = tiragesParListe.values().stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0);

        // TODO: Pour listes partagées et utilisateurs associés, besoin d’ajouter gestion des listes partagées
        // Hypothèse : ta classe Liste a un booléen 'partagee' et une relation vers plusieurs utilisateurs

        long nbListesPartagees = listeRepository.countByPartageeTrue();

        // Moyenne des utilisateurs associés aux listes partagées
        // Pour cela, une méthode à créer dans le repo, ou calcul direct
        double avgUtilisateursParListePartagee = 0; // à implémenter selon ta structure

        return Map.of(
            "nombreUtilisateurs", nbUtilisateurs,
            "moyenneListesParUtilisateur", avgListesParUtilisateur,
            "moyennePersonnesParListe", avgPersonnesParListe,
            "moyenneGroupesParListe", avgTiragesParListe,
            "nombreListesPartagees", nbListesPartagees,
            "moyenneUtilisateursParListePartagee", avgUtilisateursParListePartagee
        );
    }

    // 4. Modifier réglages généraux du site
    // Pour cela, il faut définir une entité "Settings" ou gérer via fichier config modifiable

    // Exemple simple avec une entité Settings (clé-valeur)
}
