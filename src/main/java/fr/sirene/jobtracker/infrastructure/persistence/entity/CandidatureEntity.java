package fr.sirene.jobtracker.infrastructure.persistence.entity;

import fr.sirene.jobtracker.domain.model.StatutCandidatureSpontanee;
import fr.sirene.jobtracker.domain.model.StatutPriseDeContact;
import fr.sirene.jobtracker.domain.model.TypeCandidature;
import fr.sirene.jobtracker.domain.model.TypeEntreprise;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "type_candidature", nullable = false)
    @Setter(AccessLevel.NONE)
    private TypeCandidature type;

    @ManyToOne
    @JoinColumn(name = "offre_id", unique = true, nullable = true)
    private OffreEntity offre;

    @Column(name = "nom_entreprise")
    private String nomEntreprise;

    @Column(name = "url_entreprise")
    private String urlEntreprise;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_entreprise")
    private TypeEntreprise typeEntreprise;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_spontanee")
    private StatutCandidatureSpontanee statutCandidatureSpontanee;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_prise_de_contact")
    private StatutPriseDeContact statutPriseDeContact;

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
        this.type = TypeCandidature.OFFRE;
    }

    public CandidatureEntity(TypeCandidature type) {
        this.type = type;
    }
}
