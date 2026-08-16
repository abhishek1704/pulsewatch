package com.pulsewatch.service;

import com.pulsewatch.model.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class AnomalyDetector {

    private static final double ERROR_RATE_THRESHOLD = 0.10;
    private static final double P95_LATENCY_THRESHOLD_MS = 1000.0;
    private static final double SPIKE_MULTIPLIER = 4.0;

    public AnomalyAnalysis detect(MonitoringMetrics currentMetrics, MonitoringMetrics previousMetrics) {

        List<AnomalySignal> signals = new ArrayList<>();

        detectAbsoluteThresholds(currentMetrics, signals);
        detectSpikes(currentMetrics, previousMetrics, signals);
        detectCorrelatedSignals(currentMetrics, signals);

        AnomalySeverity overallSeverity =
                determineOverallSeverity(signals);

        return new AnomalyAnalysis(
                currentMetrics.serviceName(),
                Instant.now(),
                overallSeverity,
                signals
        );
    }

    private void detectAbsoluteThresholds(MonitoringMetrics metrics, List<AnomalySignal> signals) {

        if (metrics.errorRate() > ERROR_RATE_THRESHOLD) {
            signals.add(new AnomalySignal(
                    AnomalyType.HIGH_ERROR_RATE,
                    AnomalySeverity.HIGH,
                    "Error rate exceeded configured threshold",
                    metrics.errorRate(),
                    null,
                    ERROR_RATE_THRESHOLD
            ));
        }
        if (metrics.p95LatencyMs() > P95_LATENCY_THRESHOLD_MS) {

            signals.add(new AnomalySignal(
                    AnomalyType.HIGH_LATENCY,
                    AnomalySeverity.HIGH,
                    "P95 latency exceeded configured threshold",
                    metrics.p95LatencyMs(),
                    null,
                    P95_LATENCY_THRESHOLD_MS
            ));
        }
    }

    private void detectSpikes(MonitoringMetrics current, MonitoringMetrics previous, List<AnomalySignal> signals) {
        Double errorRateIncrease = calculateRelativeIncrease(current.errorRate(),
                previous.errorRate());

        if (errorRateIncrease != null && errorRateIncrease > SPIKE_MULTIPLIER) {
            signals.add(new AnomalySignal(
                    AnomalyType.ERROR_RATE_SPIKE,
                    AnomalySeverity.HIGH,
                    "Error rate increased significantly compared to previous window",
                    current.errorRate(),
                    previous.errorRate(),
                    SPIKE_MULTIPLIER
            ));
        }

        Double latencyIncrease = calculateRelativeIncrease(current.p95LatencyMs(), previous.p95LatencyMs());
        if (latencyIncrease != null && latencyIncrease > SPIKE_MULTIPLIER) {
            signals.add(new AnomalySignal(
                    AnomalyType.LATENCY_SPIKE,
                    AnomalySeverity.HIGH,
                    "P95 latency increased significantly compared to previous window",
                    current.p95LatencyMs(),
                    previous.p95LatencyMs(),
                    SPIKE_MULTIPLIER
            ));
        }
    }

    private void detectCorrelatedSignals(MonitoringMetrics metrics, List<AnomalySignal> signals) {

        boolean highErrorRate = metrics.errorRate() > ERROR_RATE_THRESHOLD;
        boolean highLatency = metrics.p95LatencyMs() > P95_LATENCY_THRESHOLD_MS;

        if (highErrorRate && highLatency) {
            signals.add(new AnomalySignal(
                    AnomalyType.CRITICAL_DEGRADATION,
                    AnomalySeverity.CRITICAL,
                    "Service is experiencing both elevated error rate and latency",
                    metrics.errorRate(),
                    null,
                    null
            ));
        }
    }

    private Double calculateRelativeIncrease(double current, double previous) {
        if (previous <= 0) {
            return null;
        }
        return (current - previous) / previous;
    }

    private AnomalySeverity determineOverallSeverity(List<AnomalySignal> signals) {
        return signals.stream()
                .map(AnomalySignal::severity)
                .max(Enum::compareTo)
                .orElse(AnomalySeverity.LOW);
    }
}
