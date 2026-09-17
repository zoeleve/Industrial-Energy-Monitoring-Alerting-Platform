# Industrial Energy Monitoring & Alerting Platform

Industrial equipment quietly wastes energy and fails long before anyone notices — usually the first signal is the monthly bill or a breakdown on the line. This platform ingests measurements from industrial devices in real time, evaluates them against configurable rules, and raises alerts the moment something goes wrong, turning raw energy data into an early-warning system instead of a retrospective report.

Backend-only. Built to scale from a single deployable service today into independently deployable modules later, without a rewrite.

**Status:** Phase 1 — Device management API. Measurement ingestion (Kafka), rule-based alerting, auth and OPC-UA integration are planned next.

## Tech stack

| Layer | Technology |
|---|---|
| Language / runtime | Java 21 |
| Framework | Spring Boot 4.1.1 (Web, Data JPA, Validation) |
| Database | PostgreSQL 16 |
| Migrations | Flyway |
| API docs | springdoc-openapi (Swagger UI) |
| Testing | JUnit 5, Mockito, MockMvc |
| Build | Maven (wrapper included) |
| Local infra | Docker Compose |
| CI | GitHub Actions |

## Architecture

```
Industrial Devices
      │  MQTT / OPC-UA           (planned)
      ▼
 Data Ingestion ──▶ Kafka ──▶ Measurement / Alerting services   (planned)
                                        │
                                        ▼
                              PostgreSQL ──▶ REST API   (built)
```

Package layout is **package-by-feature**, not package-by-layer: each domain owns its full vertical slice (entity, DTO, repository, service, controller). This keeps bounded contexts self-contained now, and makes extracting a domain into its own service later a boundary move, not a redesign.

```
src/main/java/com/energyplatform/
├── device/              # entity, dto, repository, service, controller
└── common/exception/    # cross-cutting error handling
```

Schema is owned exclusively by Flyway migrations (`src/main/resources/db/migration`); Hibernate is set to `validate`, never `update` — no implicit schema drift between environments.

## Getting started

**Requirements:** JDK 21, Docker.

```bash
docker compose up -d postgres
./mvnw spring-boot:run
```

API: `http://localhost:8080/api/devices`
Interactive docs: `http://localhost:8080/swagger-ui.html`

## API

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/devices` | Create a device |
| GET | `/api/devices` | List all devices |
| GET | `/api/devices/{id}` | Get a device by id |
| PUT | `/api/devices/{id}` | Update a device |
| DELETE | `/api/devices/{id}` | Delete a device |

## Testing

```bash
./mvnw test
```

Unit tests (Mockito) cover service logic in isolation; `@WebMvcTest` slice tests cover the HTTP layer. Neither requires a running database, so they run the same locally and in CI.

## CI

Every push/PR to `main` runs the full test suite against a real PostgreSQL service container in GitHub Actions — see [`.github/workflows/ci.yml`](.github/workflows/ci.yml).
