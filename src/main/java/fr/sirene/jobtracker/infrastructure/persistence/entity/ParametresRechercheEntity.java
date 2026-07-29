package fr.sirene.jobtracker.infrastructure.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parametres_recherche")
@Getter
public class ParametresRechercheEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "parametres_recherche_mot_cle", joinColumns = @JoinColumn(name = "parametres_recherche_id"))
    @Column(name = "valeur")
    @Setter
    private List<String> motsCles = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "parametres_recherche_commune", joinColumns = @JoinColumn(name = "parametres_recherche_id"))
    @Setter
    private List<CommuneRechercheEmbeddable> communes = new ArrayList<>();

    @Column(name = "type_contrat")
    @Setter
    private String typeContrat;
}
