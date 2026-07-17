package com.yanshlain.minidem.common;

/**
 * The unit that metricValue is expressed in, depending on the EventType
 * it's attached to (e.g. MS for LATENCY_SPIKE, PERCENT for PACKET_LOSS).
 */
public enum MetricUnit {
    MS,
    PERCENT,
    COUNT
}
