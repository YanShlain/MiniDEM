package com.yanshlain.minidem.ingest.web;

import com.yanshlain.minidem.common.EventType;
import com.yanshlain.minidem.common.IngestEventRequest;
import com.yanshlain.minidem.ingest.domain.Event;
import com.yanshlain.minidem.ingest.repo.EventRepository;
import com.yanshlain.minidem.ingest.service.EventIngestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @RestController = @Controller + @ResponseBody: every method's return value is written
 * straight to the HTTP response body as JSON (via Jackson), instead of being resolved as
 * a view name. This is the plain, non-Java/Spring-specific REST controller pattern you
 * already know from ASP.NET Core / any Go HTTP router — nothing new to flag here beyond
 * the annotation name itself.
 *
 * The query side (GET) talks to EventRepository directly rather than through a service
 * class -- there's no business logic here beyond "pick the right derived query and map
 * to a DTO," so a service layer would just be an extra indirection with nothing in it.
 */
@RestController
public class EventController {

    private final EventIngestService eventIngestService;
    private final EventRepository eventRepository;

    public EventController(EventIngestService eventIngestService, EventRepository eventRepository) {
        this.eventIngestService = eventIngestService;
        this.eventRepository = eventRepository;
    }

    /**
     * @RequestBody deserializes the incoming JSON straight into an IngestEventRequest.
     * Jackson (Spring Boot's default JSON library) can bind directly into a record's
     * canonical constructor — no setters, no no-arg constructor needed, unlike older
     * Jackson versions or plain Java classes. Instant fields are handled automatically
     * too: Spring Boot auto-configures Jackson's JavaTimeModule whenever it's on the
     * classpath (it is, transitively), so ISO-8601 strings like
     * "2026-07-15T14:00:00Z" convert to/from Instant with no extra code.
     */
    @PostMapping("/events")
    public ResponseEntity<Void> ingest(@RequestBody IngestEventRequest request) {
        eventIngestService.ingest(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * @RequestParam(required = false) makes both filters optional -- a plain "GET /events"
     * has both as null. Binding "type" to the EventType enum is another bit of Spring's
     * built-in conversion service: it matches the query string against the enum constant
     * names automatically, and if a client passes a value that isn't one of our four
     * constants, Spring rejects the request with 400 before this method body even runs --
     * no manual validation needed, for the same reason "observedAt" strings convert to
     * Instant for free in the POST body.
     */
    @GetMapping("/events")
    public List<EventResponse> list(
            @RequestParam(required = false) String site,
            @RequestParam(required = false) EventType type
    ) {
        List<Event> events;
        if (site != null && type != null) {
            events = eventRepository.findTop100BySite_CodeAndEventTypeOrderByObservedAtDesc(site, type);
        } else if (site != null) {
            events = eventRepository.findTop100BySite_CodeOrderByObservedAtDesc(site);
        } else if (type != null) {
            events = eventRepository.findTop100ByEventTypeOrderByObservedAtDesc(type);
        } else {
            events = eventRepository.findTop100ByOrderByObservedAtDesc();
        }

        return events.stream().map(EventResponse::from).toList();
    }
}
