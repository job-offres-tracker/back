package fr.sirene.jobtracker.application.usecase.parametres;

import fr.sirene.jobtracker.application.port.parametres.ParametresCvRepository;
import fr.sirene.jobtracker.domain.model.ParametresCv;

import org.springframework.stereotype.Service;

@Service
public class ModifierParametresCvUseCase {

    private final ParametresCvRepository parametresCvRepository;

    public ModifierParametresCvUseCase(ParametresCvRepository parametresCvRepository) {
        this.parametresCvRepository = parametresCvRepository;
    }

    public ParametresCv executer(long tailleMaxOctets) {
        ParametresCv parametres = new ParametresCv(tailleMaxOctets);
        return parametresCvRepository.sauvegarder(parametres);
    }
}
