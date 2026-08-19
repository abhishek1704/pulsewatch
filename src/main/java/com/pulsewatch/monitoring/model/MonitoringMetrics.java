package com.pulsewatch.monitoring.model;

import java.time.Instant;

public record MonitoringMetrics(
        String serviceName,
        Instant windowStart,
        Instant windowEnd,
        long requestCount,
        long errorCount,
        double errorRate,
        double averageLatencyMs,
        double p95LatencyMs
) {
}