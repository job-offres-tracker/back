# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

Spring Boot 4.1.0 / Java 25 backend that synchronizes job offers from the France Travail API (`api.francetravail.io`) on a schedule, enriches them with reverse-geocoded addresses (via the French BAN API), persists them in PostgreSQL, and exposes a REST API to browse/filter offers, manually create one, and update their reading/processing state.

## Commands

```powershell
# Start Postgres (+ pgadmin) locally
docker-compose up -d

# Build
.\mvnw package

# Run (needs FRANCETRAVAIL_CLIENT_ID / FRANCETRAVAIL_CLIENT_SECRET; DB defaults to jobtracker/jobtracker on localhost:5432)
$env:FRANCETRAVAIL_CLIENT_ID = "..."
$env:FRANCETRAVAIL_CLIENT_SECRET = "..."
.\mvnw spring-boot:run

# Run all tests
.\mvnw test

# Run a single test class / method
.\mvnw test -Dtest=ConsulterOffreUseCaseTest
.\mvnw test -Dtest=ConsulterOffreUseCaseTest#retourne_l_offre_correspondant_a_l_id_externe
```

The app listens on port `8081`, no context-path. DB credentials/France Travail credentials/CORS origins/HTTP timeouts are all overridable via env vars or `application.yml` (see below) — nothing is hardcoded in Java.

## Architecture

Hexagonal (Ports & Adapters), enforced by package structure — same conventions as the sibling `sirene-backend` project:

```
fr.sirene.jobtracker
├── domain/
│   ├── model/         Offre, EtatOffre, Lieu, CritereRecherche, ResultatPagine<T>
│   └── exception/     OffreEmploiApiException, OffreNonTrouveeException, OffreDejaExistanteException
├── application/
│   ├── usecase/       Un sous-package par domaine (offre/, candidature/, cv/, parametres/, commune/),
│   │                  aligné sur les contrôleurs REST — ex. usecase/offre/ConsulterOffreUseCase,
│   │                  usecase/candidature/AjouterDocumentCvUseCase
│   └── port/          Même découpage par domaine — ex. port/offre/OffreStorageRepository,
│                      port/candidature/CandidatureRepository
├── infrastructure/
│   ├── francetravail/ client/ (auth + search RestClients), mapper/, dto/*FranceTravail, config/,
│   │                  FranceTravailOffreEmploiAdapter (implements OffreEmploiApiPort)
│   ├── ban/            client/, dto/*Ban, config/, BanGeocodageAdapter (implements GeocodageAdressePort)
│   ├── persistence/    JPA entities/repositories, JpaOffreStorageRepository, JpaLieuRepository
│   └── config/         CorsConfig/CorsProperties — top-level because CORS is cross-cutting, not owned
│                        by a single adapter (mirrors sirene-backend's convention)
└── interfaces/
    ├── rest/           OffreController, GlobalExceptionHandler, dto/ (*Request/*Response, not *Dto)
    └── scheduler/       OffreSyncScheduler
```

**Key flows:**

- **Scheduled sync** (cron in `jobtracker.sync.cron`, default every 6h): `OffreSyncScheduler` (catches/logs any exception so a failed run never kills future ones) → `SynchroniserOffresUseCase` → for each keyword entry in `jobtracker.recherche.mots-cles`, calls `OffreEmploiApiPort.rechercherOffres` → `FranceTravailOffreEmploiAdapter` pages through results (`FranceTravailApiClient`, `Content-Range` header drives pagination) using a bearer token cached/renewed by `FranceTravailAuthClient` → results across keywords are deduped by `idExterne` → each offer is enriched with a resolved address via `AdresseEnrichisseur` (looks up the `lieu` table cache by lat/long first, falls back to `GeocodageAdressePort`/BAN reverse-geocoding and caches the result) → persisted via `OffreStorageRepository.sauvegarderTout` (transactional upsert by `idExterne`; **existing `etat` is preserved on resync**, see `JpaOffreStorageRepository.toEntity`).
- **Manual offer creation** (`POST /api/v1/offres`): `CreerOffreManuelleUseCase` generates an `idExterne` prefixed `MANUEL-` if the caller doesn't supply one, checks for a collision against `OffreStorageRepository.trouverParIdExterne` first (409 `OffreDejaExistanteException` — `sauvegarderTout` itself is upsert-semantics and would otherwise silently overwrite), and defaults `provenance` to `"MANUELLE"` / `etat` to `NON_LU` when not provided.
- `Offre.provenance` is a **free-text `String`**, not an enum — `"FRANCE_TRAVAIL"` for synced offers (the `Offre.Builder` default), anything the caller wants for manual ones. Don't confuse it with `EtatOffre` (the closed enum for reading/processing status: `NON_LU, LU, REFUSE, POSTULE, ENTRETIEN, ACCEPTE, RECALE`).
- Bulk state update: `PATCH /api/v1/offres/etat` takes a list of `idsExternes` + one target `EtatOffre`.
- HTTP client timeouts (connect/read) for all three external integrations (France Travail auth, France Travail search, BAN) are configured via `RestClient.Builder.requestFactory(...)` (`JdkClientHttpRequestFactory`) using `Duration` values read from each integration's own `@ConfigurationProperties` record — not hardcoded, overridable per Spring profile.

## Testing conventions

- Test method names are French, descriptive, snake_case, describing the behavior under test — not a rephrasing of the method name.
- Stack: JUnit 5 + AssertJ (`assertThat`/`assertThatThrownBy`) + Mockito (`@ExtendWith(MockitoExtension.class)`, `@Mock`/`@InjectMocks` for single-dependency use cases; manual construction in `@BeforeEach` when there are several collaborators).
- `@Nested` is used to group tests by method/endpoint **only** where a test class covers more than one method under test (e.g. `OffreControllerTest` grouped by endpoint, `JpaOffreStorageRepositoryTest` grouped by repository method) — not applied to single-method test classes.
- REST layer: `@WebMvcTest(controllers = OffreController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CorsConfig.class))`. `CorsConfig` must be excluded — it implements `WebMvcConfigurer` so `@WebMvcTest` auto-picks it up, but its `CorsProperties` (`@ConfigurationProperties`) bean isn't registered in that slice, which breaks context loading.
- HTTP client adapters (`FranceTravailAuthClient`, `FranceTravailApiClient`) are tested against `MockRestServiceServer.bindTo(RestClient.Builder)`, not by mocking `RestClient` directly.
- **Spring Boot 4 / Jackson 3 gotcha**: `ObjectMapper` is `tools.jackson.databind.ObjectMapper`, not `com.fasterxml.jackson.databind.ObjectMapper`. `@WebMvcTest` also moved to `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`. Prefer `@MockitoBean` over the deprecated `@MockBean`.

## Database

Flyway migrations in `src/main/resources/db/migration/V{n}__description.sql` (currently up to V5). `spring.jpa.hibernate.ddl-auto` is `validate` — schema changes always go through a new migration, never through entity annotations alone.

## Git conventions

- A commit that is a pure refactoring (no behavior change — file moves/renames, package restructuring, extracting a helper with identical semantics) must have its subject line start with `Refactoring` (e.g. `Refactoring : sous-package application/usecase et application/port par domaine`). Feature/fix commits don't use this prefix.
