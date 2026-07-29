package fr.sirene.jobtracker.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "cv")
@Getter
@Setter
public class CvEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "nom_unique", unique = true, nullable = false)
    @Setter(AccessLevel.NONE)
    private String nomUnique;

    @Column(name = "nom_original", nullable = false)
    private String nomOriginal;

    @Column(name = "taille_octets", nullable = false)
    private long tailleOctets;

    @Column(name = "date_upload", nullable = false)
    private Instant dateUpload;

    protected CvEntity() {
    }

    public CvEntity(String nomUnique) {
        this.nomUnique = nomUnique;
    }
}
