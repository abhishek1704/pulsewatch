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
}