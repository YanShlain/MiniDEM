package com.yanshlain.minidem.ingest.service;

import com.yanshlain.minidem.common.IngestEventRequest;
import com.yanshlain.minidem.ingest.domain.Event;
import com.yanshlain.minidem.ingest.domain.Site;
import com.yanshlain.minidem.ingest.repo.EventRepository;
import com.yanshlain.minidem.ingest.repo.SiteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

/**
 * Turns an incoming wire request into a persisted Event.
 *
 * @Service is functionally identical to @Component (it makes this a Spring-managed bean
 * picked up by classpath scanning) — the different name is purely documentation, marking
 * "this is business logic," the way you might separate a service layer from controllers
 * in a .NET project by convention, except here it's an actual annotation rather than
 * just a folder/namespace choice.
 */
@Service
public class EventIngestService {

    private final SiteRepository siteRepository;
    private final EventRepository eventRepository;

    public EventIngestService(SiteRepository siteRepository, EventRepository eventRepository) {
        this.siteRepository = siteRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * @Transactional wraps this method call in a database transaction: Spring generates a
     * runtime proxy around this bean, and the proxy opens a transaction before the method
     * runs and commits it after — or rolls it back if an unchecked exception escapes.
     * Here that means the site lookup and the event insert are atomic, though with a
     * single insert the practical benefit is small; the pattern matters more once a
     * method does several related writes.
     *
     * Gotcha to remember: this only works because EventController calls this method on a
     * different bean (a proxy indirection happens). Calling a @Transactional method on
     * `this` from within the same class silently bypasses the proxy and runs with no
     * transaction at all.
     */
    @Transactional
    public Event ingest(IngestEventRequest request) {
        Site site = siteRepository.findByCode(request.siteCode())
                // ResponseStatusException is Spring's built-in way to abort a request with a
                // specific HTTP status from anywhere in the call stack, without needing a
                // custom exception class + a separate @ControllerAdvice to translate it.
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Unknown site code: " + request.siteCode()));

        Event event = new Event(
                site,
                request.eventType(),
                request.metricValue(),
                request.unit(),
                request.observedAt(),
                Instant.now() // ingestedAt = now, set by us on receipt, not by the sender
        );

        return eventRepository.save(event);
    }
}
