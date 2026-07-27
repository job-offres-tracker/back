package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.OffreStorageRepository;
import fr.sirene.jobtracker.domain.model.EtatOffre;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MettreAJourEtatOffresUseCaseTest {

    @Mock
    private OffreStorageRepository offreStorageRepository;

    @InjectMocks
    private MettreAJourEtatOffresUseCase useCase;

    @Test
    void delegue_la_mise_a_jour_au_repository() {
        useCase.executer(List.of("123", "456"), EtatOffre.POSTULE);

        verify(offreStorageRepository).mettreAJourEtat(List.of("123", "456"), EtatOffre.POSTULE);
    }
}
