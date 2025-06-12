package com.example.backendgroupsmaker.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.backendgroupsmaker.model.Personne;
import com.example.backendgroupsmaker.repository.PersonneRepository;

@Service
public class PersonneService {

    private final PersonneRepository personneRepository;

    public PersonneService(PersonneRepository personneRepository) {
        this.personneRepository = personneRepository;
    }

    public Personne ajouterPersonne(Personne personne) {
        return personneRepository.save(personne);
    }

    public Optional<Personne> getPersonneParId(Long id) {
        return personneRepository.findById(id);
    }

    public List<Personne> getPersonnesParListeId(Long listeId) {
        return personneRepository.findByListeId(listeId);
    }
}
