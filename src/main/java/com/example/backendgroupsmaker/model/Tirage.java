package com.example.backendgroupsmaker.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Tirage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Liste liste;

    private Date date;

    private boolean valide;

    @Column(columnDefinition = "jsonb")
    private String groupesJson;

    // getters & setters ...
}
