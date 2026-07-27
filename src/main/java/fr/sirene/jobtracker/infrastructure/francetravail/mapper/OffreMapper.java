package fr.sirene.jobtracker.infrastructure.francetravail.mapper;

import fr.sirene.jobtracker.domain.model.Lieu;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.infrastructure.francetravail.dto.EntrepriseFranceTravail;
import fr.sirene.jobtracker.infrastructure.francetravail.dto.LieuTravailFranceTravail;
import fr.sirene.jobtracker.infrastructure.francetravail.dto.OffreFranceTravail;
import fr.sirene.jobtracker.infrastructure.francetravail.dto.OrigineOffreFranceTravail;
import fr.sirene.jobtracker.infrastructure.francetravail.dto.SalaireFranceTravail;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
public class OffreMapper {

    public Offre toDomain(OffreFranceTravail dto) {
        return Offre.builder()
                .idExterne(dto.id())
                .intitule(dto.intitule())
                .description(dto.description())
                .entreprise(extraireNomEntreprise(dto.entreprise()))
                .lieu(extraireLieu(dto.lieuTravail()))
                .typeContrat(dto.typeContrat())
                .salaire(extraireLibelleSalaire(dto.salaire()))
                .urlOrigine(extraireUrlOrigine(dto.origineOffre()))
                .dateCreation(parserDate(dto.dateCreation()))
                .build();
    }

    public List<Offre> toDomainList(List<OffreFranceTravail> dtos) {
        return dtos.stream().map(this::toDomain).toList();
    }

    private String extraireNomEntreprise(EntrepriseFranceTravail entreprise) {
        return entreprise != null ? entreprise.nom() : null;
    }

    private Lieu extraireLieu(LieuTravailFranceTravail lieuTravail) {
        if (lieuTravail == null) {
            return null;
        }
        return new Lieu(lieuTravail.libelle(), lieuTravail.commune(), lieuTravail.latitude(), lieuTravail.longitude(), null);
    }

    private String extraireLibelleSalaire(SalaireFranceTravail salaire) {
        return salaire != null ? salaire.libelle() : null;
    }

    private String extraireUrlOrigine(OrigineOffreFranceTravail origineOffre) {
        return origineOffre != null ? origineOffre.urlOrigine() : null;
    }

    private LocalDateTime parserDate(String dateCreation) {
        if (dateCreation == null) {
            return null;
        }
        try {
            return OffsetDateTime.parse(dateCreation).toLocalDateTime();
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
