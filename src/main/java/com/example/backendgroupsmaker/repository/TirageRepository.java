package com.example.backendgroupsmaker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendgroupsmaker.model.Tirage;

public interface TirageRepository extends JpaRepository<Tirage, Long> {
    List<Tirage> findByListeId(Long listeId);
}
