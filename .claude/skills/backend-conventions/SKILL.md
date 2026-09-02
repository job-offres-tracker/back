---
name: backend-conventions
description: Rappelle les règles d'architecture hexagonale, les pratiques de code et les conventions de test du backend job-offres-tracker avant d'écrire ou modifier du code. Use when adding or modifying a use case, a port/adapter, a REST endpoint, une exception, de la configuration, or a test in job-offres-tracker (backend Spring Boot).
paths:
  - src/main/java/**
  - src/main/resources/**
  - src/test/java/**
---

Backend Spring Boot 4.1.0 / Java 25, architecture hexagonale (Ports & Adapters), mêmes conventions que le projet `sirene-backend`. Voir CLAUDE.md §Architecture pour l'arborescence complète des packages et §Testing conventions pour la stack et les règles de test — ce qui suit ne reprend que ce qui n'y figure pas déjà.

## Architecture — règles strictes

Au-delà de l'arborescence documentée dans CLAUDE.md :

- **`domain/` reste du Java pur, zéro annotation framework** (ni Spring, ni Swagger, ni Jackson). Si un champ de domaine doit être documenté dans Swagger, mettre le `@Schema` sur le DTO REST qui l'expose, pas sur le record de domaine.
- **Modélisation** : identité propre (ex. `Offre` identifiée par `idExterne`) → `class` avec `Builder`/`toBuilder()` ; identité = valeur (ex. `Lieu`, `Commune`, `ResultatPagine`) → `record`.
- Les ports vivent dans `application/port`, jamais dans `domain/port` : ce sont les use cases qui les appellent, pas le domaine.
- **Le sous-package de `usecase/` et `port/` correspond au domaine métier, aligné sur le contrôleur REST qui expose le use case** (`offre` ↔ `OffreController`, `candidature` ↔ `CandidatureController`, `cv` ↔ `CvController`, `parametres` ↔ `ParametresController`, `commune` ↔ `CommuneController`). Un nouveau use case/port rejoint le sous-package du domaine concerné plutôt que de créer un nouveau découpage ; en cas de doute (ex. une classe utilisée par un seul autre use case d'un autre domaine, comme `CandidatureAutoCreationService` appelée par les use cases `offre`), le rattacher au domaine qui possède la donnée manipulée, et importer explicitement depuis l'autre sous-package (plus de visibilité implicite de package une fois séparés).
- Chaque intégration externe a son propre sous-package d'infrastructure avec `client/` (appel HTTP brut), `dto/` (records miroir de l'API externe, `@JsonProperty` pour les champs snake_case), `config/` (properties + RestClient), et l'Adapter au niveau racine du sous-package qui implémente le port et fait la conversion DTO → domaine. Voir `ban/`, `geo/`, `ai/`, `francetravail/`, `scraping/` comme modèles.
- Un use case reste un orchestrateur fin : il appelle un ou plusieurs ports, ne contient pas de logique HTTP/JSON/SQL.

## Pratiques de code

- **Rien n'est codé en dur.** Toute config (URLs, timeouts, clés, cron) passe par `application.yml` via un record `@ConfigurationProperties`, surchargeable par `${VAR:defaut}`.
- **`spring-boot-configuration-processor` n'est pas une dépendance du projet** : pour chaque nouveau groupe `@ConfigurationProperties`, ajouter une entrée dans `src/main/resources/META-INF/additional-spring-configuration-metadata.json` (sinon pas d'autocomplétion IDE). Ne pas oublier cette étape — c'est facile à zapper.
- **Client HTTP externe** : un bean `RestClient` par intégration, construit avec `RestClient.Builder` + `JdkClientHttpRequestFactory` pour les timeouts (`Duration` lus depuis les properties), et un `defaultStatusHandler` sur 4xx/5xx qui lève une exception de domaine dédiée. Cette exception a deux constructeurs (`message` et `message, cause`) et hérite de `RuntimeException`.
- **Lombok sur les entités JPA uniquement** (jamais dans `domain/`, cf. règle de pureté ci-dessus) : `@Getter` au niveau classe, `@Setter` au niveau classe avec `@Setter(AccessLevel.NONE)` en exclusion sur les champs non modifiables après construction (ex. `id` généré, `idExterne` fixé une fois au constructeur). Voir `OffreEntity`, `LieuEntity`.
- **Logging** : `@Slf4j` (Lombok) sur toute classe qui logue — pas de `LoggerFactory.getLogger(...)` manuel.
- **Chaque nouvelle exception de domaine doit avoir un handler dans `GlobalExceptionHandler`** : `ProblemDetail.forStatusAndDetail(status, message)` + `setTitle(...)` + `setProperty("timestamp", Instant.now())`. Mapping de statut : échec d'un appel externe → 502, ressource introuvable → 404, conflit/doublon → 409, validation → 400.
- **Validation aux frontières uniquement** : `jakarta.validation` directement sur les paramètres de contrôleur (`@RequestParam @Min(...)`) ou les champs des DTO de requête (`@NotBlank`, `@NotEmpty`, `@URL`...). Pas de validation dupliquée dans les use cases.
- **Documentation OpenAPI obligatoire sur tout nouvel endpoint** : `@Tag` sur le contrôleur, `@Operation` + `@ApiResponses` (avec `schema = @Schema(implementation = ProblemDetail.class)` pour les réponses d'erreur) sur chaque méthode, `@Schema(description = ...)` sur les champs des DTO REST.
- **CORS** : `CorsConfig` couvre `/api/v1/**` pour GET/PATCH/POST/OPTIONS — ajouter la méthode HTTP si un nouvel endpoint utilise un verbe non listé.
- **Spring Boot 4 / Jackson 3** : `ObjectMapper` vient de `tools.jackson.databind`, pas de `com.fasterxml.jackson.databind`. `@WebMvcTest` vient de `org.springframework.boot.webmvc.test.autoconfigure`. Préférer `@MockitoBean` à `@MockBean` (déprécié).
- Pas de classe Mapper dédiée pour un mapping trivial 1:1 (voir `BanGeocodageAdapter`, `GeoApiCommuneAdapter`, `AiExtractionAdapter` — mapping inline dans l'adapter) ; réserver un Mapper séparé aux conversions non triviales avec plusieurs champs imbriqués (voir `OffreMapper` pour France Travail).

## Tests

Voir CLAUDE.md §Testing conventions (stack, nommage, `@Nested`, `@WebMvcTest`, test des clients HTTP, pas de `@SpringBootTest`).

## Avant de considérer une tâche terminée

- [ ] Nouvelle config → entrée ajoutée dans `additional-spring-configuration-metadata.json`
- [ ] Nouvelle exception → handler ajouté dans `GlobalExceptionHandler`
- [ ] Nouvel endpoint → annoté Swagger (`@Tag`/`@Operation`/`@ApiResponses`) et méthode HTTP couverte par `CorsConfig` si besoin
- [ ] Nouveau port/adapter → test unitaire du use case avec le port mocké
- [ ] Rien de codé en dur — tout passe par `application.yml`
