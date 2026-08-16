package com.pulsewatch.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Entity
@Table(name = "telemetry_record")
@Getter
@NoArgsConstructor
public class TelemetryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "environment", nullable = false)
    private String environment;

    @Column(name = "endpoint", nullable = false)
    private String endpoint;

    @Column(name = "status_code", nullable = false)
    private Integer statusCode;

    @Column(name = "latency_ms", nullable = false)
    private Long latencyMs;

    @Column(name = "event_timestamp", nullable = false)
    private Instant eventTimestamp;

    public TelemetryRecord(TelemetryEvent event) {
        this.serviceName = event.getServiceName();
        this.environment = event.getEnvironment();
        this.endpoint = event.getEndpoint();
        this.statusCode = event.getStatusCode();
        this.latencyMs = event.getLatencyMs();
        this.eventTimestamp = event.getTimestamp();
    }
}
