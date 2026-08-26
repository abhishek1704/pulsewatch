package com.pulsewatch.anomaly.model;

import com.pulsewatch.monitoring.model.MonitoringMetrics;

import java.time.Instant;
import java.util.List;

public record AnomalyDetectedEvent(
        String serviceName,
        AnomalySeverity severity,
        List<AnomalySignal> signals,
        MonitoringMetrics currentMetrics,
        MonitoringMetrics previousMetrics,
        Instant detectedAt
) {
}