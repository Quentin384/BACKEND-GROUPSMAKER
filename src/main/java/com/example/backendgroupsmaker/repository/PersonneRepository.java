package com.example.backendgroupsmaker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendgroupsmaker.model.Personne;

public interface PersonneRepository extends JpaRepository<Personne, Long> {
    List<Personne> findByListeId(Long listeId);
}
