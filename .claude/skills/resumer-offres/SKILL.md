---
name: resumer-offres
description: Synchronise les offres d'emploi France Travail via le backend job-offres-tracker puis résume les offres non lues. Use when the user asks to synchroniser les offres, checker les nouvelles offres d'emploi, or résumer/faire le point sur les offres non lues.
context: fork
agent: job-offres-tracker
allowed-tools: PowerShell, Read
---

Le backend `job-offres-tracker` doit tourner en local sur le port 8081 (`./mvnw spring-boot:run`), avec Postgres démarré. Si un appel échoue avec une erreur de connexion, arrête-toi et indique à l'utilisateur de démarrer le backend au lieu de réessayer en boucle.

1. Déclencher la synchronisation depuis France Travail :
   ```powershell
   Invoke-RestMethod -Uri "http://localhost:8081/api/v1/offres/synchroniser" -Method Post
   ```
   Réponse attendue : `202 Accepted`, pas de corps.

2. Récupérer et lire le CV le plus récent, pour affiner l'analyse de pertinence :
   ```powershell
   Invoke-RestMethod -Uri "http://localhost:8081/api/v1/cvs" -Method Get
   ```
   La liste est déjà triée du plus récent au plus ancien — prends le premier élément (`nomUnique`). Si la liste est vide, continue sans CV et signale-le simplement dans le résumé final (ne bloque pas le reste du déroulé).

   Télécharge ensuite son contenu binaire vers un fichier temporaire, puis lis-le avec l'outil Read (qui sait extraire le texte d'un PDF) :
   ```powershell
   Invoke-WebRequest -Uri "http://localhost:8081/api/v1/cvs/<nomUnique>" -OutFile "$env:TEMP\cv-analyse.pdf"
   ```
   Le contenu extrait (compétences, stack technique, séniorité, expériences) sert de base à l'analyse de pertinence à l'étape 4 — ne le recharge pas à chaque offre, une seule lecture suffit pour toute la synthèse.

3. Récupérer les offres non lues (jusqu'à 100 — l'API rejette avec 400 au-delà, `taille` doit être <= 100) :
   ```powershell
   Invoke-RestMethod -Uri "http://localhost:8081/api/v1/offres?etats=NON_LU&page=0&taille=100" -Method Get
   ```
   La réponse (`PagedResponse`) contient `elements` : chaque offre a déjà tous les champs utiles (`intitule`, `description`, `entreprise`, `lieu`, `typeContrat`, `salaire`, `urlOrigine`, `dateCreation`, `provenance`). N'appelle PAS `GET /api/v1/offres/{idExterne}` pour chaque offre — c'est redondant, la liste contient déjà tout.

4. Résumer les offres pour l'utilisateur :
   - Nombre total d'offres non lues (`totalElements` de la réponse)
   - Une synthèse groupée (ex: par entreprise ou par lieu si des doublons/patterns apparaissent)
   - Pour chaque offre : intitulé, entreprise, lieu, type de contrat, salaire (si présent), lien `urlOrigine`
   - Priorise les offres en confrontant la description de chaque offre au contenu réel du CV (compétences/stack technique en commun, séniorité, intitulés de poste déjà occupés) — pas seulement les mots-clés de recherche configurés (`GET /api/v1/parametres/recherche`). Justifie brièvement chaque priorisation (ex: "stack Java/Spring déjà maîtrisée d'après le CV", "pas d'expérience frontend React mentionnée dans le CV").
   - L'entreprise n'est pas toujours bien renseigné, essaie de l'identifier à partir de la description et toujours à partir de la description de suggérer s'il s'agit d'une ESN.

Reste concis dans le résumé final — une liste ou un tableau plutôt qu'un paragraphe par offre.
