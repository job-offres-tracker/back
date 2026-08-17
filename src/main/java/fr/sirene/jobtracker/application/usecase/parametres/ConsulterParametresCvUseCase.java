package fr.sirene.jobtracker.application.usecase.parametres;

import fr.sirene.jobtracker.application.port.parametres.ParametresCvRepository;
import fr.sirene.jobtracker.domain.model.ParametresCv;

import org.springframework.stereotype.Service;

@Service
public class ConsulterParametresCvUseCase {

    private final ParametresCvRepository parametresCvRepository;

    public ConsulterParametresCvUseCase(ParametresCvRepository parametresCvRepository) {
        this.parametresCvRepository = parametresCvRepository;
    }

    public ParametresCv executer() {
        return parametresCvRepository.recuperer();
    }
}
