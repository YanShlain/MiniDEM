package com.yanshlain.minidem.ingest.repo;

import com.yanshlain.minidem.common.EventType;
import com.yanshlain.minidem.ingest.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Four more derived queries for GET /events, covering "no filter" / "site only" /
 * "type only" / "both" -- EventController picks whichever matches the request's params.
 *
 * "Top100" is another piece of Spring Data's method-name parsing, alongside "OrderByX":
 * it caps the result at 100 rows (a LIMIT 100 under the hood) without needing Pageable
 * or a separate count query -- appropriate here since we only ever want "the most recent
 * N", not true pagination through the whole table.
 */
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findTop100ByOrderByObservedAtDesc();

    List<Event> findTop100BySite_CodeOrderByObservedAtDesc(String siteCode);

    List<Event> findTop100ByEventTypeOrderByObservedAtDesc(EventType eventType);

    List<Event> findTop100BySite_CodeAndEventTypeOrderByObservedAtDesc(String siteCode, EventType eventType);
}
