package com.yanshlain.minidem.ingest.repo;

import com.yanshlain.minidem.ingest.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * No custom query methods yet — Phase 2's ingestion endpoint only needs the inherited
 * save(). The derived queries the GET /events endpoint needs (filter by site/type,
 * newest first) get added here in Phase 4.
 */
public interface EventRepository extends JpaRepository<Event, Long> {
}
