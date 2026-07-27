package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.OffreStorageRepository;
import fr.sirene.jobtracker.domain.model.EtatOffre;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.domain.model.ResultatPagine;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsulterOffresUseCase {

    private final OffreStorageRepository offreStorageRepository;

    public ConsulterOffresUseCase(OffreStorageRepository offreStorageRepository) {
        this.offreStorageRepository = offreStorageRepository;
    }

    public ResultatPagine<Offre> executer(int page, int taille, List<EtatOffre> etats) {
        List<Offre> offres = offreStorageRepository.rechercher(page, taille, etats);
        long total = offreStorageRepository.compter(etats);
        return new ResultatPagine<>(offres, page, taille, total);
    }
}
