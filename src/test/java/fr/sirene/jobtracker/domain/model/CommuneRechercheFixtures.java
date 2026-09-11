package fr.sirene.jobtracker.domain.model;

import java.util.List;
import java.util.stream.IntStream;

public final class CommuneRechercheFixtures {

    public static final CommuneRecherche NANTES = new CommuneRecherche("44109", "Nantes");
    public static final CommuneRecherche SAINT_HERBLAIN = new CommuneRecherche("44020", "Saint-Herblain");
    public static final CommuneRecherche REZE = new CommuneRecherche("44143", "Rezé");
    public static final CommuneRecherche ORVAULT = new CommuneRecherche("44114", "Orvault");
    public static final CommuneRecherche COUERON = new CommuneRecherche("44047", "Couëron");
    public static final CommuneRecherche VERTOU = new CommuneRecherche("44215", "Vertou");
    public static final CommuneRecherche BOUGUENAIS = new CommuneRecherche("44018", "Bouguenais");
    public static final CommuneRecherche SABLES_D_OLONNE = new CommuneRecherche("85191", "Les Sables-d'Olonne");
    public static final CommuneRecherche CHALLANS = new CommuneRecherche("85047", "Challans");
    public static final CommuneRecherche TALMONT_SAINT_HILAIRE = new CommuneRecherche("85194", "Talmont-Saint-Hilaire");
    public static final CommuneRecherche NANTES_CENTRE = new CommuneRecherche("44000", "Nantes centre");

    private CommuneRechercheFixtures() {
    }

    public static List<CommuneRecherche> septCommunesNantesMetropole() {
        return List.of(NANTES, SAINT_HERBLAIN, REZE, ORVAULT, COUERON, VERTOU, BOUGUENAIS);
    }

    public static List<CommuneRecherche> sixCommunesValides() {
        return List.of(NANTES, SAINT_HERBLAIN, SABLES_D_OLONNE, CHALLANS, TALMONT_SAINT_HILAIRE, NANTES_CENTRE);
    }

    public static List<CommuneRecherche> vingtEtUneCommunes() {
        return IntStream.rangeClosed(1, 21)
                .mapToObj(i -> new CommuneRecherche("4410" + i, "Commune " + i))
                .toList();
    }
}
