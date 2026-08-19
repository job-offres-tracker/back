package fr.sirene.jobtracker.application.port.offre;

public interface GenerationLettreMotivationPort {

    String genererLettre(String idExterneOffre, String cvNomUnique);
}
