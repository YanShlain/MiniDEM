package com.yanshlain.minidem.ingest.domain;

import com.yanshlain.minidem.common.EventType;
import com.yanshlain.minidem.common.MetricUnit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * A single observed network condition at a Site — one row per event, backed by the
 * "events" table. This is the thing event-generator sends and clients query for.
 */
@Entity
@Table(
        name = "events",
        // Every query in Phase 4 filters/sorts by site and/or time, so we index the pair
        // up front rather than relying on a full table scan once the table has data in it.
        indexes = @Index(name = "idx_events_site_observed_at", columnList = "site_id, observed_at")
)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ManyToOne = "many events belong to one site" (the FK lives on this side, events.site_id).
    // FetchType.LAZY means Hibernate does NOT load the related Site row automatically when it
    // loads an Event — it only fetches it the first time you actually call getSite().*() on it.
    // That query only works while the original database session/transaction is still open;
    // calling it later throws LazyInitializationException. A classic JPA gotcha, harmless here
    // since we map to a DTO before the transaction ends.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    // @Enumerated(STRING) stores the enum's name ("PACKET_LOSS") in the column instead of
    // its ordinal position (0, 1, 2...). Slightly more storage, but immune to breaking if we
    // ever reorder the EventType constants later — ordinals would silently point at the wrong
    // value if that happened.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @Column(nullable = false)
    private double metricValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetricUnit unit;

    // When the condition actually happened (event time), set by event-generator.
    @Column(nullable = false)
    private Instant observedAt;

    // When ingest-service received and stored it (processing time), set by us on receipt.
    // Kept distinct from observedAt on purpose — see the plan doc's "event-time vs
    // ingest-time" note.
    @Column(nullable = false)
    private Instant ingestedAt;

    protected Event() {
    }

    public Event(Site site, EventType eventType, double metricValue, MetricUnit unit,
                 Instant observedAt, Instant ingestedAt) {
        this.site = site;
        this.eventType = eventType;
        this.metricValue = metricValue;
        this.unit = unit;
        this.observedAt = observedAt;
        this.ingestedAt = ingestedAt;
    }

    public Long getId() {
        return id;
    }

    public Site getSite() {
        return site;
    }

    public EventType getEventType() {
        return eventType;
    }

    public double getMetricValue() {
        return metricValue;
    }

    public MetricUnit getUnit() {
        return unit;
    }

    public Instant getObservedAt() {
        return observedAt;
    }

    public Instant getIngestedAt() {
        return ingestedAt;
    }
}
