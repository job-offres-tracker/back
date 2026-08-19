package fr.sirene.jobtracker.application.usecase.parametres;

import fr.sirene.jobtracker.application.port.parametres.ParametresRechercheRepository;
import fr.sirene.jobtracker.domain.model.ParametresRecherche;

import org.springframework.stereotype.Service;

@Service
public class ConsulterParametresRechercheUseCase {

    private final ParametresRechercheRepository parametresRechercheRepository;

    public ConsulterParametresRechercheUseCase(ParametresRechercheRepository parametresRechercheRepository) {
        this.parametresRechercheRepository = parametresRechercheRepository;
    }

    public ParametresRecherche executer() {
        return parametresRechercheRepository.recuperer();
    }
}
