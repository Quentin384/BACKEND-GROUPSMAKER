package com.example.backendgroupsmaker.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.backendgroupsmaker.model.Liste;
import com.example.backendgroupsmaker.repository.ListeRepository;

@Service
public class ListeService {

    private final ListeRepository listeRepository;

    public ListeService(ListeRepository listeRepository) {
        this.listeRepository = listeRepository;
    }

    public Liste creerListe(Liste liste) {
        return listeRepository.save(liste);
    }

    public Optional<Liste> getListeParId(Long id) {
        return listeRepository.findById(id);
    }

    public List<Liste> getListesParUtilisateurId(Long utilisateurId) {
        return listeRepository.findByUtilisateurId(utilisateurId);
    }

    public List<Liste> getToutesListes() {
        return listeRepository.findAll();
    }
}
