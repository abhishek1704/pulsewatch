package com.pulsewatch.simulator;

import com.pulsewatch.telemetry.model.TelemetryEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
@Slf4j
public class TelemetrySimulatorService {

    private final RestClient restClient;
    private final Random random = new Random();

    public TelemetrySimulatorService(@Value("${pulsewatch.telemetry.endpoint.url}") String telemetryEndpoint) {
        this.restClient = RestClient.builder()
                .baseUrl(telemetryEndpoint)
                .build();
    }

    public void runScenario(String scenario, int events, long intervalMs) {

        for (int i = 0; i < events; i++) {
            TelemetryEvent event = generateEvent(scenario, i, events);
            send(event);
            sleep(intervalMs);
        }
    }

    private TelemetryEvent generateEvent(String scenario, int index, int totalEvents) {
        int statusCode;
        long latencyMs;

        switch (scenario.toUpperCase()) {

            case "HEALTHY":
                statusCode = 200;
                latencyMs = random.nextLong(100, 200);
                break;

            case "LATENCY_DEGRADATION":
                statusCode = 200;
                // Gradually increase latency throughout the scenario
                long baseLatency = 100 + (index * 100L);
                latencyMs = baseLatency + random.nextLong(0, 100);
                break;

            case "ERROR_SPIKE":
                // First half healthy, second half starts failing
                if (index < totalEvents / 2) {
                    statusCode = 200;
                    latencyMs = random.nextLong(100, 200);
                } else {
                    statusCode = random.nextBoolean() ? 500 : 503;
                    latencyMs = random.nextLong(150, 400);
                }
                break;

            case "OUTAGE":
                statusCode = random.nextBoolean() ? 500 : 503;
                latencyMs = random.nextLong(500, 2000);
                break;

            default:
                throw new IllegalArgumentException(
                        "Unknown scenario: " + scenario +
                                ". Supported: HEALTHY, LATENCY_DEGRADATION, ERROR_SPIKE, OUTAGE"
                );
        }

        return new TelemetryEvent(
                "payment-api",
                "local",
                "/payments",
                statusCode,
                latencyMs,
                Instant.now()
        );
    }

    private void send(TelemetryEvent event) {

        try {
            restClient.post()
                    .uri("/api/telemetry")
                    .body(event)
                    .retrieve()
                    .toBodilessEntity();

            log.info(
                    "Simulated telemetry: service={}, status={}, latency={}ms",
                    event.getServiceName(),
                    event.getStatusCode(),
                    event.getLatencyMs()
            );

        } catch (Exception e) {
            log.error("Failed to send simulated telemetry", e);
        }
    }

    private void sleep(long intervalMs) {
        try {
            Thread.sleep(intervalMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Simulator interrupted", e);
        }
    }
}