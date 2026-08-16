package com.pulsewatch.model;

public record AnomalySignal(
        String type,
        String description,
        double currentValue,
        double previousValue
) {
}