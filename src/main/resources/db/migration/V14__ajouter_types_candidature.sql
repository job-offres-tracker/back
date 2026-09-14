ALTER TABLE candidature ALTER COLUMN offre_id DROP NOT NULL;

ALTER TABLE candidature
    ADD COLUMN type_candidature VARCHAR(20) NOT NULL DEFAULT 'OFFRE',
    ADD COLUMN statut_spontanee VARCHAR(20),
    ADD COLUMN statut_prise_de_contact VARCHAR(20),
    ADD COLUMN nom_entreprise VARCHAR(255),
    ADD COLUMN url_entreprise VARCHAR(500),
    ADD COLUMN type_entreprise VARCHAR(30);

ALTER TABLE candidature ALTER COLUMN type_candidature DROP DEFAULT;

ALTER TABLE candidature ADD CONSTRAINT chk_candidature_coherence CHECK (
    (type_candidature = 'OFFRE' AND offre_id IS NOT NULL
        AND statut_spontanee IS NULL AND statut_prise_de_contact IS NULL
        AND nom_entreprise IS NULL AND type_entreprise IS NULL)
    OR (type_candidature = 'SPONTANEE' AND offre_id IS NULL
        AND statut_spontanee IS NOT NULL AND statut_prise_de_contact IS NULL
        AND nom_entreprise IS NOT NULL AND type_entreprise IS NOT NULL)
    OR (type_candidature = 'PRISE_DE_CONTACT' AND offre_id IS NULL
        AND statut_prise_de_contact IS NOT NULL AND statut_spontanee IS NULL
        AND nom_entreprise IS NOT NULL AND type_entreprise IS NOT NULL)
);

ALTER TABLE candidature ADD CONSTRAINT chk_candidature_statut_spontanee
    CHECK (statut_spontanee IS NULL OR statut_spontanee IN ('ENVOYE', 'REFUSE', 'ACCEPTE', 'RECALE'));

ALTER TABLE candidature ADD CONSTRAINT chk_candidature_statut_prise_de_contact
    CHECK (statut_prise_de_contact IS NULL OR statut_prise_de_contact IN ('ETABLI', 'REFUSE', 'ACCEPTE', 'RECALE'));
