package com.yanshlain.minidem.common;

/**
 * The kinds of network conditions our simulated events can represent.
 * Stored in Postgres as the actual enum name (e.g. "LATENCY_SPIKE"), not a number —
 * see Event.eventType's @Enumerated(EnumType.STRING) for why.
 */
public enum EventType {
    LATENCY_SPIKE,
    PACKET_LOSS,
    JITTER,
    CONNECTIVITY_LOSS
}
