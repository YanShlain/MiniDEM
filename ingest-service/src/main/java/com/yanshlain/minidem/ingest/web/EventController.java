package com.yanshlain.minidem.ingest.web;

import com.yanshlain.minidem.common.IngestEventRequest;
import com.yanshlain.minidem.ingest.service.EventIngestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @RestController = @Controller + @ResponseBody: every method's return value is written
 * straight to the HTTP response body as JSON (via Jackson), instead of being resolved as
 * a view name. This is the plain, non-Java/Spring-specific REST controller pattern you
 * already know from ASP.NET Core / any Go HTTP router — nothing new to flag here beyond
 * the annotation name itself.
 */
@RestController
public class EventController {

    private final EventIngestService eventIngestService;

    public EventController(EventIngestService eventIngestService) {
        this.eventIngestService = eventIngestService;
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
}
