package com.example.backendgroupsmaker.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backendgroupsmaker.model.Liste;
import com.example.backendgroupsmaker.model.Utilisateur;
import com.example.backendgroupsmaker.repository.ListeRepository;
import com.example.backendgroupsmaker.repository.PersonneRepository;
import com.example.backendgroupsmaker.repository.TirageRepository;
import com.example.backendgroupsmaker.repository.UtilisateurRepository;

@Service
public class AdminService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ListeRepository listeRepository;

    @Autowired
    private PersonneRepository personneRepository;

    @Autowired
    private TirageRepository tirageRepository;

    /**
     * Retourne un Map où la clé est le username et la valeur le nombre de listes créées par cet utilisateur.
     */
    public Map<String, Long> getNombreListesParUtilisateur() {
        // Récupère tous les utilisateurs
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();

        // Pour chaque utilisateur, compte le nombre de listes qu’il possède
        return utilisateurs.stream()
            .collect(Collectors.toMap(
                Utilisateur::getUsername,
                utilisateur -> (long) listeRepository.findByUtilisateurId(utilisateur.getId()).size()
            ));
    }

    /**
     * Calcule la moyenne du nombre de personnes par liste.
     */
    public double getMoyennePersonnesParListe() {
        List<Liste> listes = listeRepository.findAll();

        if (listes.isEmpty()) return 0;

        // Somme du nombre de personnes dans chaque liste
        long totalPersonnes = listes.stream()
            .mapToLong(liste -> personneRepository.countByListeId(liste.getId()))
            .sum();

        // Moyenne = total personnes / nombre de listes
        return (double) totalPersonnes / listes.size();
    }

    /**
     * Calcule la moyenne du nombre de groupes (tirages) par liste.
     */
    public double getMoyenneGroupesParListe() {
        List<Liste> listes = listeRepository.findAll();

        if (listes.isEmpty()) return 0;

        // Somme du nombre de groupes dans chaque liste
        long totalGroupes = listes.stream()
            .mapToLong(liste -> tirageRepository.countByListeId(liste.getId()))
            .sum();

        // Moyenne = total groupes / nombre de listes
        return (double) totalGroupes / listes.size();
    }

    /**
     * Retourne le nombre total de listes partagées.
     */
    public long getNombreListesPartagees() {
        return listeRepository.countByPartageeTrue();
    }

    /**
     * Calcule la moyenne du nombre d’utilisateurs associés aux listes partagées.
     * IMPORTANT : Adapter selon ta modélisation, ici on suppose qu’une liste a un seul utilisateur.
     */
    public double getMoyenneUtilisateursParListePartagee() {
        // Récupère toutes les listes partagées
        List<Liste> listesPartagees = listeRepository.findAll().stream()
            .filter(Liste::isPartagee)  // Méthode à créer dans entité Liste : public boolean isPartagee() { return this.partagee; }
            .collect(Collectors.toList());

        if (listesPartagees.isEmpty()) return 0;

        // Dans ce cas simple, chaque liste a 1 utilisateur
        long totalUtilisateurs = listesPartagees.size();

        // Moyenne = total utilisateurs / nombre de listes partagées
        return (double) totalUtilisateurs / listesPartagees.size();
    }
}
