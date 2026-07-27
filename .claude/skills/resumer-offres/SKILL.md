---
name: resumer-offres
description: Synchronise les offres d'emploi France Travail via le backend job-offres-tracker puis résume les offres non lues. Use when the user asks to synchroniser les offres, checker les nouvelles offres d'emploi, or résumer/faire le point sur les offres non lues.
---

Le backend `job-offres-tracker` doit tourner en local sur le port 8081 (`./mvnw spring-boot:run`), avec Postgres démarré. Si un appel échoue avec une erreur de connexion, arrête-toi et indique à l'utilisateur de démarrer le backend au lieu de réessayer en boucle.

1. Déclencher la synchronisation depuis France Travail :
   ```powershell
   Invoke-RestMethod -Uri "http://localhost:8081/api/v1/offres/synchroniser" -Method Post
   ```
   Réponse attendue : `202 Accepted`, pas de corps.

2. Récupérer les offres non lues (jusqu'à 200) :
   ```powershell
   Invoke-RestMethod -Uri "http://localhost:8081/api/v1/offres?etats=NON_LU&page=0&taille=200" -Method Get
   ```
   La réponse (`PagedResponse`) contient `elements` : chaque offre a déjà tous les champs utiles (`intitule`, `description`, `entreprise`, `lieu`, `typeContrat`, `salaire`, `urlOrigine`, `dateCreation`, `provenance`). N'appelle PAS `GET /api/v1/offres/{idExterne}` pour chaque offre — c'est redondant, la liste contient déjà tout.

3. Résumer les offres pour l'utilisateur :
   - Nombre total d'offres non lues (`totalElements` de la réponse)
   - Une synthèse groupée (ex: par entreprise ou par lieu si des doublons/patterns apparaissent)
   - Pour chaque offre : intitulé, entreprise, lieu, type de contrat, salaire (si présent), lien `urlOrigine`
   - Signale les offres qui semblent les plus pertinentes en priorité si le contexte le permet (ex: correspond aux mots-clés de recherche configurés : Java, Back-end, lead tech)
   - L'entreprise n'est pas toujours bien renseigné, essaie de l'identifier à partir de la description et toujours à partir de la description de suggérer s'il s'agit d'une ESN.

Reste concis dans le résumé final — une liste ou un tableau plutôt qu'un paragraphe par offre.
