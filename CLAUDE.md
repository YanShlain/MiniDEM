# DEM Home-Lab Monitor (miniDEM)

## What this application does
miniDEM is a toy "network health monitor" that mirrors, in miniature, what
Cato's DEM (Digital Experience Monitoring) team builds for real: a system
that watches network telemetry and surfaces problems through a dashboard.

Concretely:
- A **simulator** generates fake network events on a timer — packet loss,
  latency spikes, trace-route hops — for a handful of made-up "sites"
  (standing in for real customer network locations). This replaces DEM's
  real firehose of telemetry from many network sources.
- **Spring Boot** ingests these events as they arrive.
  - Full event history is stored in **PostgreSQL** (e.g. "site X had a
    latency spike at 14:02").
  - Current/recent state per site is cached in **Redis** (e.g. "site X is
    RIGHT NOW showing packet loss") — this is the "hot path" a real
    dashboard would poll constantly, separate from historical queries.
- A **GraphQL** API lets you query this flexibly: "show me all events for
  site X in the last hour," "which sites currently have packet loss,"
  etc. — mirroring how DEM's real dashboards query varied shapes of data
  without needing a new REST endpoint for every question.
- **Grafana** visualizes it: event volume over time, loss rate per site,
  latency trends.
- Later (stretch goal): reimplement one small piece in **Play Framework**
  to feel the difference from Spring Boot, since DEM/XSOAP both touch it.

This is NOT meant to be production-grade or feature-complete. It exists so
Yan can speak fluently about this stack (Java/Spring/GraphQL/Postgres/
Redis/k8s) in his new role — the learning is the deliverable, not the app.

## Why this project exists
Learning project ahead of joining Cato Networks as Software Team Lead,
DEM team. Goal is to UNDERSTAND every piece, not just generate a working
repo — explain reasoning, don't just produce code.

## Stack
- Java 21, Spring Boot 3.x, Maven
- GraphQL (graphql-java / spring-graphql)
- PostgreSQL (event history)
- Redis (hot/recent state)
- Deployed via Kubernetes manifests (Docker Desktop's local k8s)
- Grafana for dashboards

## Working style
- Yan is learning Spring Boot/Java; he knows C#/.NET and Go well — use
  those as reference points when explaining new concepts
  (e.g. Spring Data JPA ~ Entity Framework, Spring DI ~ .NET DI)
- Discuss and agree on requirements/approach before implementing anything
  non-trivial (use plan mode for this) — don't jump straight to code
- Explain each new Spring annotation/concept the first time it's used
- Prefer standard, idiomatic Spring Boot patterns over clever or unusual
  ones — the goal is to learn what a normal Spring codebase looks like
- Small, incremental commits per session; don't batch large unreviewed
  changes