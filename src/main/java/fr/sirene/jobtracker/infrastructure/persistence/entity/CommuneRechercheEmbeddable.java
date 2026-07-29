package fr.sirene.jobtracker.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class CommuneRechercheEmbeddable {

    @Column(name = "code_insee")
    private String codeInsee;

    @Column(name = "libelle")
    private String libelle;

    protected CommuneRechercheEmbeddable() {
    }

    public CommuneRechercheEmbeddable(String codeInsee, String libelle) {
        this.codeInsee = codeInsee;
        this.libelle = libelle;
    }
}
