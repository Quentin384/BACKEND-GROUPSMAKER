package com.example.backendgroupsmaker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendgroupsmaker.model.Tirage;

public interface TirageRepository extends JpaRepository<Tirage, Long> {

    // Compte combien de tirages (groupes) sont associés à une liste donnée
    long countByListeId(Long listeId);
}
