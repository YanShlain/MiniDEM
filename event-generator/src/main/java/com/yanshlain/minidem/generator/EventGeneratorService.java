package com.yanshlain.minidem.generator;

import com.yanshlain.minidem.common.EventType;
import com.yanshlain.minidem.common.IngestEventRequest;
import com.yanshlain.minidem.common.MetricUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.Random;

/**
 * Plays the role of the agent/probe fleet: on a timer, fabricates one plausible-looking
 * network event for a random site and POSTs it to ingest-service. This is a stand-in for
 * real probes reporting in -- the values are made up, but the shape on the wire is
 * identical to what a real collector would receive.
 *
 * @Component + @Scheduled is how Spring runs this without anything ever calling it
 * directly: @EnableScheduling (on EventGeneratorApplication) tells Spring to look for
 * @Scheduled methods on every bean, and hand each one to a background scheduler thread
 * that invokes it on the configured interval. There is no visible caller anywhere in
 * our code -- the annotation IS the wiring.
 */
@Component
public class EventGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(EventGeneratorService.class);

    private final RestClient ingestRestClient;
    private final String[] siteCodes;
    private final Random random = new Random();

    public EventGeneratorService(RestClient ingestRestClient,
                                  @Value("${minidem.generator.site-codes}") String[] siteCodes) {
        this.ingestRestClient = ingestRestClient;
        this.siteCodes = siteCodes;
    }

    /**
     * fixedRateString resolves the interval from application.properties at startup
     * rather than hardcoding a number in the annotation -- same effect as
     * @Scheduled(fixedRate = 5000), just tunable without recompiling.
     */
    @Scheduled(fixedRateString = "${minidem.generator.interval-ms}")
    public void generateAndSendEvent() {
        String siteCode = siteCodes[random.nextInt(siteCodes.length)];
        EventType eventType = randomEventType();
        IngestEventRequest request = buildRequest(siteCode, eventType);

        try {
            ingestRestClient.post()
                    .uri("/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Sent {} event for site {} ({} {})", eventType, siteCode, request.metricValue(), request.unit());
        } catch (RestClientException ex) {
            // If the collector is briefly unreachable or rejects the request, log and move
            // on -- a failed tick shouldn't stop future ticks. Matches the "durability isn't
            // guaranteed pre-commit" decision from planning: losing a synthetic event is fine.
            log.warn("Failed to send event for site {}: {}", siteCode, ex.getMessage());
        }
    }

    private EventType randomEventType() {
        EventType[] types = EventType.values();
        return types[random.nextInt(types.length)];
    }

    private IngestEventRequest buildRequest(String siteCode, EventType eventType) {
        MetricUnit unit;
        double value;

        switch (eventType) {
            case LATENCY_SPIKE -> {
                unit = MetricUnit.MS;
                value = 20 + random.nextDouble() * 280; // ~20-300ms
            }
            case PACKET_LOSS -> {
                unit = MetricUnit.PERCENT;
                value = random.nextDouble() * 15; // 0-15%
            }
            case JITTER -> {
                unit = MetricUnit.MS;
                value = random.nextDouble() * 50; // 0-50ms
            }
            case CONNECTIVITY_LOSS -> {
                unit = MetricUnit.COUNT;
                value = 1;
            }
            default -> throw new IllegalStateException("Unhandled event type: " + eventType);
        }

        return new IngestEventRequest(siteCode, eventType, value, unit, Instant.now());
    }
}
