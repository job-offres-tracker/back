package fr.sirene.jobtracker.infrastructure.persistence.entity;

import fr.sirene.jobtracker.domain.model.EtatOffre;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "offre")
@Getter
@Setter
public class OffreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "id_externe", unique = true, nullable = false)
    @Setter(AccessLevel.NONE)
    private String idExterne;

    @Column(name = "intitule")
    private String intitule;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "entreprise")
    private String entreprise;

    @ManyToOne
    @JoinColumn(name = "lieu_id")
    private LieuEntity lieu;

    @Column(name = "type_contrat")
    private String typeContrat;

    @Column(name = "salaire")
    private String salaire;

    @Column(name = "url_origine")
    private String urlOrigine;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_import", nullable = false)
    private LocalDateTime dateImport;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat", nullable = false)
    private EtatOffre etat;

    @Column(name = "provenance", nullable = false)
    private String provenance;

    protected OffreEntity() {
    }

    public OffreEntity(String idExterne) {
        this.idExterne = idExterne;
    }
}
