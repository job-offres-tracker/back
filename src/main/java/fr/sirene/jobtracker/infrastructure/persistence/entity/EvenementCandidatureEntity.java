package fr.sirene.jobtracker.infrastructure.persistence.entity;

import fr.sirene.jobtracker.domain.model.TypeEvenement;

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

import java.time.LocalDate;

@Entity
@Table(name = "evenement_candidature")
@Getter
@Setter
public class EvenementCandidatureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidature_id", nullable = false)
    private CandidatureEntity candidature;

    @Column(name = "date_evenement", nullable = false)
    private LocalDate dateEvenement;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeEvenement type;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    protected EvenementCandidatureEntity() {
    }

    public EvenementCandidatureEntity(CandidatureEntity candidature) {
        this.candidature = candidature;
    }
}
