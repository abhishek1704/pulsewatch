package com.pulsewatch.anomaly.model;

public record AnomalySignal(
        AnomalyType type,
        AnomalySeverity severity,
        String description,
        double currentValue,
        Double previousValue,
        Double threshold
) {
}