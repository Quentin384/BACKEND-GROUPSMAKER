package com.example.backendgroupsmaker.service;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backendgroupsmaker.model.Liste;
import com.example.backendgroupsmaker.model.Utilisateur;
import com.example.backendgroupsmaker.repository.ListeRepository;
import com.example.backendgroupsmaker.repository.UtilisateurRepository;

/**
 * Service utilitaire pour valider l’accès aux listes d’un utilisateur.
 */
@Service
public class ListeAccessService {

    @Autowired
    private UtilisateurRepository utilisateurRepo;

    @Autowired
    private ListeRepository listeRepo;

    /**
     * Retourne l'utilisateur associé au "Principal" HTTP.
     */
    public Utilisateur getCurrentUser(Principal principal) {
        return utilisateurRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException(
                        "Utilisateur non trouvé : " + principal.getName()));
    }

    /**
     * Vérifie qu'une liste appartient bien à l'utilisateur connecté.
     */
    public Liste getOwnedListe(Long idListe, Utilisateur current) {
        Liste liste = listeRepo.findById(idListe)
                .orElseThrow(() -> new RuntimeException(
                        "Liste non trouvée : " + idListe));
        if (!liste.getUtilisateur().getId().equals(current.getId())) {
            throw new RuntimeException("Accès refusé à la liste");
        }
        return liste;
    }
}
