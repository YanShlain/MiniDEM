# miniDEM — Progress

Tracks implementation progress across sessions. See `CLAUDE.md` for project context and
working conventions. Full requirements/design detail for the phase below lives in the
session's plan doc (not checked into the repo).

## Session 1 — Two-Service Event Ingestion + Query (HTTP)

Goal: a separate agent service (`event-generator`) emits synthetic network events over HTTP
to a collector service (`ingest-service`) that persists them to PostgreSQL and serves a REST
query endpoint. No GraphQL, no Redis, no gRPC, no Kafka this session.

- [x] **Phase 0 — Multi-module Maven skeleton**
      Parent pom + `common` / `ingest-service` / `event-generator` modules, docker-compose
      Postgres, Maven Wrapper. Both apps boot and do nothing yet.
      Merged: [PR #1](https://github.com/YanShlain/MiniDEM/pull/1)

- [x] **Phase 1 — Contract + persistence**
      `common`: `IngestEventRequest` record, `EventType`, `MetricUnit` enums.
      `ingest-service`: `Site`/`Event` JPA entities, repositories, `DataSeeder` (seeds ~6 sites).
      Verify: tables created on boot, sites seeded.

- [x] **Phase 2 — Ingestion endpoint**
      `ingest-service`: `EventIngestService` (`@Transactional`) + `POST /events`.
      Verify: manual `curl -X POST localhost:8080/events` inserts a row.

- [x] **Phase 3 — Generator service**
      `event-generator`: `@Scheduled` loop building a random `IngestEventRequest`, POSTing
      via `RestClient` to the collector.
      Verify: both apps running, event count grows every ~5s.

- [x] **Phase 4 — Query endpoint**
      `ingest-service`: `GET /events` with optional `?site=` / `?type=` filters, newest-first.
      Verify: `curl localhost:8080/events` (and filtered variants) return expected JSON.

## Later sessions (roadmap, not yet scoped in detail)

| Session | Adds | Realism it buys |
|---|---|---|
| Later | gRPC transport (protobuf contract in `common`) | Typed, efficient agent↔collector calls |
| Later | Kafka (generator produces → topic → ingest consumes), run in K8s | Decoupling + real no-loss delivery (acks/offsets/retries) |
| Later | Redis hot-state cache in ingest | Fast recent-state reads |
| Later | GraphQL query API alongside/replacing REST | Flexible client queries |
| Later | Grafana dashboards; all services on K8s (Docker Desktop) | Full observability stack |
