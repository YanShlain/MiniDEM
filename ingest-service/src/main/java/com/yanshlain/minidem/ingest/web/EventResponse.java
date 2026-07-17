package com.yanshlain.minidem.ingest.web;

import com.yanshlain.minidem.common.EventType;
import com.yanshlain.minidem.common.MetricUnit;
import com.yanshlain.minidem.ingest.domain.Event;

import java.time.Instant;

/**
 * The GET /events response shape -- a flattened view of Event, with the Site relationship
 * reduced to its code rather than serializing the whole Site (or the Event entity
 * directly, which would also expose Hibernate's lazy-loading proxy machinery to Jackson).
 */
public record EventResponse(
        Long id,
        String siteCode,
        EventType eventType,
        double metricValue,
        MetricUnit unit,
        Instant observedAt,
        Instant ingestedAt
) {

    public static EventResponse from(Event event) {
        return new EventResponse(
                event.getId(),
                // event.getSite() is a lazy-loaded proxy (see Event.java's @ManyToOne
                // comment) -- calling .getCode() here only works because Spring Boot's
                // "Open Session In View" default (spring.jpa.open-in-view=true, the
                // warning you've seen at every startup) keeps the Hibernate session open
                // for the whole HTTP request, not just for the repository call. That's
                // why this line doesn't throw LazyInitializationException even though the
                // repository method that fetched this Event already returned. OSIV is
                // convenient for a project this size, but it's also why Spring prints
                // that warning -- it hides when/where extra queries happen, which is a
                // real cost in a bigger app. Worth knowing as a tradeoff, not a free lunch.
                event.getSite().getCode(),
                event.getEventType(),
                event.getMetricValue(),
                event.getUnit(),
                event.getObservedAt(),
                event.getIngestedAt()
        );
    }
}
