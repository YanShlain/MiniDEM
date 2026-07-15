package com.yanshlain.minidem.common;

import java.time.Instant;

/**
 * The wire payload event-generator POSTs to ingest-service's /events endpoint.
 * A Java "record" is an immutable data carrier: this one line generates a constructor,
 * getters (siteCode(), eventType(), ...), equals/hashCode, and toString() automatically.
 * It's the closest thing Java has to a C# record.
 *
 * siteCode is a plain String, not a Site object/FK: event-generator doesn't know about
 * ingest-service's database at all, it just knows the string codes of the sites it's
 * pretending to be. ingest-service is the one that resolves the code to an actual Site row.
 */
public record IngestEventRequest(
        String siteCode,
        EventType eventType,
        double metricValue,
        MetricUnit unit,
        Instant observedAt
) {
}
