package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.ParametresRechercheRepository;
import fr.sirene.jobtracker.domain.model.CommuneRecherche;
import fr.sirene.jobtracker.domain.model.ParametresRecherche;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModifierParametresRechercheUseCase {

    private final ParametresRechercheRepository parametresRechercheRepository;

    public ModifierParametresRechercheUseCase(ParametresRechercheRepository parametresRechercheRepository) {
        this.parametresRechercheRepository = parametresRechercheRepository;
    }

    public ParametresRecherche executer(List<String> motsCles, List<CommuneRecherche> communes, String typeContrat) {
        ParametresRecherche parametres = new ParametresRecherche(motsCles, communes, typeContrat);
        return parametresRechercheRepository.sauvegarder(parametres);
    }
}
