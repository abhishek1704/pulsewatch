package com.pulsewatch.model;

import java.time.Instant;
import java.util.List;

public record AnomalyAnalysis(
        String serviceName,
        Instant analyzedAt,
        AnomalySeverity severity,
        List<AnomalySignal> signals
) {
}