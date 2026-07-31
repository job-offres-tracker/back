package fr.sirene.jobtracker.infrastructure.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "candidature")
@Getter
@Setter
public class CandidatureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "offre_id", unique = true, nullable = false)
    private OffreEntity offre;

    @Column(name = "date_candidature", nullable = false)
    private LocalDateTime dateCandidature;

    @OneToMany(mappedBy = "candidature", cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    private List<EvenementCandidatureEntity> evenements = new ArrayList<>();

    @OneToMany(mappedBy = "candidature", cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    private List<DocumentCandidatureEntity> documents = new ArrayList<>();

    protected CandidatureEntity() {
    }

    public CandidatureEntity(OffreEntity offre) {
        this.offre = offre;
    }
}
