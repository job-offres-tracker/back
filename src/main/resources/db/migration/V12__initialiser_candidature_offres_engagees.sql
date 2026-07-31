INSERT INTO candidature (offre_id, date_candidature)
SELECT o.id, o.date_import
FROM offre o
WHERE o.etat IN ('POSTULE', 'ENTRETIEN', 'ACCEPTE', 'RECALE')
  AND NOT EXISTS (
      SELECT 1 FROM candidature c WHERE c.offre_id = o.id
  );
