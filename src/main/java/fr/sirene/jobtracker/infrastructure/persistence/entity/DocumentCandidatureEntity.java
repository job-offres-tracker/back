package fr.sirene.jobtracker.infrastructure.persistence.entity;

import fr.sirene.jobtracker.domain.model.TypeDocument;

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
@Table(name = "document_candidature")
@Getter
@Setter
public class DocumentCandidatureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidature_id", nullable = false)
    private CandidatureEntity candidature;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeDocument type;

    @Column(name = "libelle", nullable = false)
    private String libelle;

    @ManyToOne
    @JoinColumn(name = "cv_id")
    private CvEntity cv;

    @Column(name = "nom_stocke")
    private String nomStocke;

    @Column(name = "taille_octets")
    private Long tailleOctets;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "contenu_texte", columnDefinition = "TEXT")
    private String contenuTexte;

    @Column(name = "date_ajout", nullable = false)
    private LocalDateTime dateAjout;

    protected DocumentCandidatureEntity() {
    }

    public DocumentCandidatureEntity(CandidatureEntity candidature) {
        this.candidature = candidature;
    }
}
