# Industrial Energy Monitoring & Alerting Platform

Industrial equipment quietly wastes energy and fails long before anyone notices, usually the first signal is the monthly bill or a breakdown on the line. This platform ingests measurements from industrial devices in real time, evaluates them against configurable rules, and raises alerts the moment something goes wrong, turning raw energy data into an early-warning system instead of a retrospective report.

Backend-only. Built to scale from a single deployable service today into independently deployable modules later, without a rewrite.

**Status:** Phase 3 — Device management, Kafka-based measurement ingestion, rule-based alerting, and JWT-authenticated role-based access (ADMIN / OPERATOR). OPC-UA integration is planned next.

## Tech stack

| Layer | Technology |
|---|---|
| Language / runtime | Java 21 |
| Framework | Spring Boot 4.1.1 (Web, Data JPA, Validation, Kafka, Security) |
| Auth | JWT (jjwt), BCrypt, role-based access control |
| Messaging | Apache Kafka |
| Database | PostgreSQL 16 |
| Migrations | Flyway |
| API docs | springdoc-openapi (Swagger UI) |
| Testing | JUnit 5, Mockito, MockMvc, `@EmbeddedKafka`, Testcontainers |
| Code quality | Spotless (Google Java Format), Checkstyle |
| Build | Maven (wrapper included), Makefile |
| Local infra | Docker Compose |
| CI | GitHub Actions |

## Architecture

```
Industrial Devices
      │  MQTT / OPC-UA                       (planned)
      ▼
 Data Ingestion ──▶ Kafka ──▶ Measurement Consumer ──▶ Alert Evaluator ──▶ Alert
                                        │
                                        ▼
                                   PostgreSQL ◀── Device CRUD
                                        │
                                        ▼
                             REST API (JWT-secured, role-based)
```

`POST /api/measurements` publishes to Kafka and returns immediately (`202 Accepted`); a consumer persists it, then evaluates it against enabled alert rules for that device, raising an `Alert` when one fires. A `GET` right after a `POST` may not yet reflect it — that lag is the deliberate tradeoff of decoupling ingestion from processing, not a bug.

Package layout is **package-by-feature**, not package-by-layer: each domain owns its full vertical slice (entity, DTO, repository, service, controller). This keeps bounded contexts self-contained now, and makes extracting a domain into its own service later a boundary move, not a redesign.

```
src/main/java/com/energyplatform/
├── device/              # entity, dto, repository, service, controller
├── measurement/         # entity, dto, repository, service, controller, Kafka producer/consumer
├── alert/                # rule + alert entities, evaluator, service, controller
├── auth/                 # users, JWT issuing/validation, Spring Security config
└── common/               # cross-cutting: error handling, Kafka topic config
```

Schema is owned exclusively by Flyway migrations (`src/main/resources/db/migration`); Hibernate is set to `validate`, never `update` — no implicit schema drift between environments.

## Getting started

**Requirements:** JDK 21, Docker.

```bash
docker compose up -d
./mvnw spring-boot:run
```

API: `http://localhost:8080/api/devices`
Interactive docs: `http://localhost:8080/swagger-ui.html`

Every endpoint except `/api/auth/**` requires a JWT. Register, log in, then send the token as `Authorization: Bearer <token>`:

```bash
curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"adminpass123","role":"ADMIN"}'

curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"adminpass123"}'
# => {"token": "..."}
```

## API

All list endpoints are paginated (`?page=0&size=20&sort=...`).

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | — | Create a user (ADMIN or OPERATOR) |
| POST | `/api/auth/login` | — | Exchange credentials for a JWT |
| POST | `/api/devices` | ADMIN | Create a device |
| GET | `/api/devices` | any | List devices |
| GET | `/api/devices/{id}` | any | Get a device by id |
| PUT | `/api/devices/{id}` | ADMIN | Update a device |
| DELETE | `/api/devices/{id}` | ADMIN | Delete a device |
| POST | `/api/measurements` | any | Publish a measurement event (async, `202 Accepted`) |
| GET | `/api/measurements` | any | List all persisted measurements |
| GET | `/api/measurements/device/{deviceId}` | any | List measurements for one device |
| POST | `/api/alerts/rules` | ADMIN | Create an alert rule for a device |
| GET | `/api/alerts/rules` | any | List alert rules |
| GET | `/api/alerts` | any | List alerts |
| PATCH | `/api/alerts/{id}/resolve` | any | Mark an alert resolved |

## Testing

```bash
./mvnw test        # or: make test
```

Unit tests (Mockito) cover service and rule-evaluation logic in isolation; `@WebMvcTest` slice tests cover the HTTP and security layers; `@EmbeddedKafka` backs an integration test that exercises the full producer → topic → consumer → database path with a real (in-memory) broker; Testcontainers spins up a real, disposable PostgreSQL for tests that need one (context load, optimistic locking, full auth flow) — no manual `docker compose up` required before running tests.

## Code quality

```bash
make format   # auto-format (Spotless / Google Java Format)
make check    # tests + format check + Checkstyle — what CI runs
```

## CI

Every push/PR to `main` runs `mvnw clean verify` in GitHub Actions — see [`.github/workflows/ci.yml`](.github/workflows/ci.yml). No services to configure: Testcontainers provisions PostgreSQL on demand using the Docker already available on GitHub-hosted runners.
