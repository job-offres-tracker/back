ALTER TABLE parametres_recherche ADD COLUMN type_contrat VARCHAR(50);
UPDATE parametres_recherche SET type_contrat = 'CDI';
