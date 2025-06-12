package com.example.backendgroupsmaker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendgroupsmaker.model.Tirage;

public interface TirageRepository extends JpaRepository<Tirage, Long> {

    /**
     * Retourne la liste des tirages associés à une liste spécifique.
     * 
     * @param listeId L'ID de la liste
     * @return Liste des tirages pour cette liste
     */
    List<Tirage> findByListeId(Long listeId);

    /**
     * Compte combien de tirages sont associés à une liste donnée
     * 
     * @param listeId L'ID de la liste
     * @return Nombre de tirages
     */
    long countByListeId(Long listeId);
}
