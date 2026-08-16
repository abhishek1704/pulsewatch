package com.pulsewatch.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
public class TelemetryEvent {

    private String serviceName;
    private String environment;
    private String endpoint;
    private Integer statusCode;
    private Long latencyMs;
    private Instant timestamp;

    public TelemetryEvent(String serviceName, String environment, String endpoint, int statusCode, long latencyMs, Instant timestamp) {
        this.serviceName = serviceName;
        this.environment = environment;
        this.endpoint = endpoint;
        this.statusCode = statusCode;
        this.latencyMs = latencyMs;
        this.timestamp = timestamp;
    }
}