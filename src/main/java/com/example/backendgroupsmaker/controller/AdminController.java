package com.example.backendgroupsmaker.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backendgroupsmaker.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Endpoint GET qui retourne toutes les statistiques d’administration demandées.
     * Accessible uniquement aux utilisateurs avec le rôle ADMIN.
     */
    @GetMapping("/statistiques")
    public StatistiquesDTO getStatistiques() {
        StatistiquesDTO dto = new StatistiquesDTO();

        dto.setNombreListesParUtilisateur(adminService.getNombreListesParUtilisateur());
        dto.setMoyennePersonnesParListe(adminService.getMoyennePersonnesParListe());
        dto.setMoyenneGroupesParListe(adminService.getMoyenneGroupesParListe());
        dto.setNombreListesPartagees(adminService.getNombreListesPartagees());
        dto.setMoyenneUtilisateursParListePartagee(adminService.getMoyenneUtilisateursParListePartagee());

        return dto;
    }

    /**
     * DTO (Data Transfer Object) pour structurer la réponse JSON des statistiques.
     */
    public static class StatistiquesDTO {

        private Map<String, Long> nombreListesParUtilisateur;
        private double moyennePersonnesParListe;
        private double moyenneGroupesParListe;
        private long nombreListesPartagees;
        private double moyenneUtilisateursParListePartagee;

        // Getters et setters

        public Map<String, Long> getNombreListesParUtilisateur() {
            return nombreListesParUtilisateur;
        }

        public void setNombreListesParUtilisateur(Map<String, Long> nombreListesParUtilisateur) {
            this.nombreListesParUtilisateur = nombreListesParUtilisateur;
        }

        public double getMoyennePersonnesParListe() {
            return moyennePersonnesParListe;
        }

        public void setMoyennePersonnesParListe(double moyennePersonnesParListe) {
            this.moyennePersonnesParListe = moyennePersonnesParListe;
        }

        public double getMoyenneGroupesParListe() {
            return moyenneGroupesParListe;
        }

        public void setMoyenneGroupesParListe(double moyenneGroupesParListe) {
            this.moyenneGroupesParListe = moyenneGroupesParListe;
        }

        public long getNombreListesPartagees() {
            return nombreListesPartagees;
        }

        public void setNombreListesPartagees(long nombreListesPartagees) {
            this.nombreListesPartagees = nombreListesPartagees;
        }

        public double getMoyenneUtilisateursParListePartagee() {
            return moyenneUtilisateursParListePartagee;
        }

        public void setMoyenneUtilisateursParListePartagee(double moyenneUtilisateursParListePartagee) {
            this.moyenneUtilisateursParListePartagee = moyenneUtilisateursParListePartagee;
        }
    }
}
