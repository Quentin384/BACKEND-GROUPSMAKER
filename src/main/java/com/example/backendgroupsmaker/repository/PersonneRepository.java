package com.example.backendgroupsmaker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendgroupsmaker.model.Personne;

public interface PersonneRepository extends JpaRepository<Personne, Long> {

    // Compte combien de personnes sont dans une liste donnée (id de la liste)
    long countByListeId(Long listeId);
}
