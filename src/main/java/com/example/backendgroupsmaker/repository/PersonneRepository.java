package com.example.backendgroupsmaker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendgroupsmaker.model.Personne;

/**
 * Interface repository pour l'entité Personne.
 * Elle permet d'effectuer des opérations CRUD et des requêtes personnalisées
 * sur la table "personne" en base de données.
 */
public interface PersonneRepository extends JpaRepository<Personne, Long> {

    /**
     * Compte le nombre de personnes associées à une liste donnée, identifiée par son ID.
     * 
     * @param listeId l'identifiant de la liste
     * @return le nombre total de personnes dans cette liste
     */
    long countByListeId(Long listeId);

    /**
     * Récupère toutes les personnes appartenant à une liste spécifique, identifiée par son ID.
     * 
     * @param listeId l'identifiant de la liste
     * @return la liste des personnes liées à cette liste
     */
    List<Personne> findByListeId(Long listeId);
}
