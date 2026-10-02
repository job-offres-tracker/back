ALTER TABLE candidature
    ADD COLUMN poste VARCHAR(255),
    ADD COLUMN client VARCHAR(255);

ALTER TABLE candidature DROP CONSTRAINT chk_candidature_coherence;
ALTER TABLE candidature ADD CONSTRAINT chk_candidature_coherence CHECK (
    (type_candidature = 'OFFRE' AND offre_id IS NOT NULL AND statut_offre IS NOT NULL
        AND statut_spontanee IS NULL AND statut_prise_de_contact IS NULL
        AND nom_entreprise IS NULL AND type_entreprise IS NULL
        AND poste IS NULL AND client IS NULL)
    OR (type_candidature = 'SPONTANEE' AND offre_id IS NULL AND statut_offre IS NULL
        AND statut_spontanee IS NOT NULL AND statut_prise_de_contact IS NULL
        AND nom_entreprise IS NOT NULL AND type_entreprise IS NOT NULL
        AND poste IS NULL AND client IS NULL)
    OR (type_candidature = 'PRISE_DE_CONTACT' AND offre_id IS NULL AND statut_offre IS NULL
        AND statut_prise_de_contact IS NOT NULL AND statut_spontanee IS NULL
        AND nom_entreprise IS NOT NULL AND type_entreprise IS NOT NULL)
);

ALTER TABLE candidature ADD CONSTRAINT chk_candidature_client_type_entreprise
    CHECK (client IS NULL OR type_entreprise IN ('ESN', 'CABINET_RECRUTEMENT'));
