ALTER TABLE candidature ADD COLUMN statut_offre VARCHAR(20);

-- Backfill à partir de l'offre.etat AVANT toute modification de cette colonne :
-- ENTRETIEN (entretien en cours, sans résultat final -> POSTULE côté candidature),
-- ACCEPTE et RECALE sont reportés tels quels sur la candidature.
UPDATE candidature c SET statut_offre = 'POSTULE'
FROM offre o WHERE c.offre_id = o.id AND c.type_candidature = 'OFFRE' AND o.etat IN ('POSTULE', 'ENTRETIEN');
UPDATE candidature c SET statut_offre = 'ACCEPTE'
FROM offre o WHERE c.offre_id = o.id AND c.type_candidature = 'OFFRE' AND o.etat = 'ACCEPTE';
UPDATE candidature c SET statut_offre = 'RECALE'
FROM offre o WHERE c.offre_id = o.id AND c.type_candidature = 'OFFRE' AND o.etat = 'RECALE';

ALTER TABLE candidature DROP CONSTRAINT chk_candidature_coherence;
ALTER TABLE candidature ADD CONSTRAINT chk_candidature_coherence CHECK (
    (type_candidature = 'OFFRE' AND offre_id IS NOT NULL AND statut_offre IS NOT NULL
        AND statut_spontanee IS NULL AND statut_prise_de_contact IS NULL
        AND nom_entreprise IS NULL AND type_entreprise IS NULL)
    OR (type_candidature = 'SPONTANEE' AND offre_id IS NULL AND statut_offre IS NULL
        AND statut_spontanee IS NOT NULL AND statut_prise_de_contact IS NULL
        AND nom_entreprise IS NOT NULL AND type_entreprise IS NOT NULL)
    OR (type_candidature = 'PRISE_DE_CONTACT' AND offre_id IS NULL AND statut_offre IS NULL
        AND statut_prise_de_contact IS NOT NULL AND statut_spontanee IS NULL
        AND nom_entreprise IS NOT NULL AND type_entreprise IS NOT NULL)
);
ALTER TABLE candidature ADD CONSTRAINT chk_candidature_statut_offre
    CHECK (statut_offre IS NULL OR statut_offre IN ('POSTULE', 'ACCEPTE', 'REFUSE', 'RECALE'));

-- L'entretien est déjà tracké via les événements de la candidature (TypeEvenement.ENTRETIEN) :
-- ENTRETIEN/ACCEPTE/RECALE disparaissent de l'état de l'offre, qui reste POSTULE.
UPDATE offre SET etat = 'POSTULE' WHERE etat IN ('ENTRETIEN', 'ACCEPTE', 'RECALE');
ALTER TABLE offre ADD CONSTRAINT chk_offre_etat CHECK (etat IN ('NON_LU', 'LU', 'POSTULE', 'REFUSE'));
