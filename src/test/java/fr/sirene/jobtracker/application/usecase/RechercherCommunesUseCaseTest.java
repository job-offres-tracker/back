package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.RechercheCommunePort;
import fr.sirene.jobtracker.domain.model.Commune;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RechercherCommunesUseCaseTest {

    @Mock
    private RechercheCommunePort rechercheCommunePort;

    @InjectMocks
    private RechercherCommunesUseCase useCase;

    @Test
    void transmet_la_recherche_au_port_et_retourne_les_communes_trouvees() {
        Commune nantes = new Commune("Nantes", "44109", List.of("44000", "44100"));
        when(rechercheCommunePort.rechercher("nant")).thenReturn(List.of(nantes));

        List<Commune> resultat = useCase.executer("nant");

        assertThat(resultat).containsExactly(nantes);
    }

    @Test
    void retourne_une_liste_vide_quand_aucune_commune_ne_correspond() {
        when(rechercheCommunePort.rechercher("zzzzz")).thenReturn(List.of());

        List<Commune> resultat = useCase.executer("zzzzz");

        assertThat(resultat).isEmpty();
    }
}
