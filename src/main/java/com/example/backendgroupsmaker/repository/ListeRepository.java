package com.example.backendgroupsmaker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendgroupsmaker.model.Liste;

public interface ListeRepository extends JpaRepository<Liste, Long> {
    List<Liste> findByUtilisateurId(Long utilisateurId);
    Liste findByNomAndUtilisateurId(String nom, Long utilisateurId);
}
