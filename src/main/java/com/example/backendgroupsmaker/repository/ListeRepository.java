package com.example.backendgroupsmaker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendgroupsmaker.model.Liste;

public interface ListeRepository extends JpaRepository<Liste, Long> {

    // Récupérer toutes les listes d’un utilisateur via son Id
    List<Liste> findByUtilisateurId(Long utilisateurId);

    // Trouver une liste par son nom et l’utilisateur qui la possède
    Liste findByNomAndUtilisateurId(String nom, Long utilisateurId);

    // Récupérer toutes les listes d’un utilisateur via son username
    List<Liste> findByUtilisateurUsername(String username);

    // Compter le nombre de listes qui sont partagées (champ boolean 'partagee' à true)
    long countByPartageeTrue();
}
