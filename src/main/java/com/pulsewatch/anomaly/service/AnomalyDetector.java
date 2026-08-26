package com.pulsewatch.anomaly.service;

import com.pulsewatch.anomaly.model.AnomalyAnalysis;
import com.pulsewatch.anomaly.model.AnomalySeverity;
import com.pulsewatch.anomaly.model.AnomalySignal;
import com.pulsewatch.anomaly.model.AnomalyType;
import com.pulsewatch.monitoring.model.MonitoringMetrics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class AnomalyDetector {

    @Value("${pulsewatch.anomaly.errorRateThreshold:0.10}")
    private double errorRateThreshold;
    @Value("${pulsewatch.anomaly.p95LatencyThresholdMs:1000.0}")
    private double p95LatencyThresholdMs;
    @Value("${pulsewatch.anomaly.spikeMultiplier:4.0}")
    private double spikeMultiplier;

    /**
     * Detects anomalies in the current metrics compared to the previous metrics.
     * If the previous metrics are null, only absolute thresholds are checked.
     *
     * @param currentMetrics the current monitoring metrics
     * @param previousMetrics the previous monitoring metrics
     * @return the anomaly analysis result
     */
    public AnomalyAnalysis detect(MonitoringMetrics currentMetrics, MonitoringMetrics previousMetrics) {

        List<AnomalySignal> signals = new ArrayList<>();

        detectAbsoluteThresholds(currentMetrics, signals);
        if (previousMetrics != null) {
            detectSpikes(currentMetrics, previousMetrics, signals);
        }
        detectCorrelatedSignals(currentMetrics, signals);
        AnomalySeverity overallSeverity = determineOverallSeverity(signals);

        return new AnomalyAnalysis(
                currentMetrics.serviceName(),
                Instant.now(),
                overallSeverity,
                signals
        );
    }

    /**
     * Detects anomalies based on absolute thresholds for error rate and latency.
     * If the current metrics exceed the configured thresholds, an anomaly signal is added.
     *
     * @param metrics the current monitoring metrics
     * @param signals the list of anomaly signals to which detected anomalies will be added
     */
    private void detectAbsoluteThresholds(MonitoringMetrics metrics, List<AnomalySignal> signals) {

        if (metrics.errorRate() > errorRateThreshold) {
            signals.add(new AnomalySignal(
                    AnomalyType.HIGH_ERROR_RATE,
                    AnomalySeverity.HIGH,
                    "Error rate exceeded configured threshold",
                    metrics.errorRate(),
                    null,
                    errorRateThreshold
            ));
        }
        if (metrics.p95LatencyMs() > p95LatencyThresholdMs) {

            signals.add(new AnomalySignal(
                    AnomalyType.HIGH_LATENCY,
                    AnomalySeverity.HIGH,
                    "P95 latency exceeded configured threshold",
                    metrics.p95LatencyMs(),
                    null,
                    p95LatencyThresholdMs
            ));
        }
    }

    /**
     * Detects anomalies based on relative increases in error rate and latency compared to previous metrics.
     * If the current metrics show a significant increase (greater than SPIKE_MULTIPLIER) compared to the previous metrics,
     * an anomaly signal is added.
     *
     * @param current the current monitoring metrics
     * @param previous the previous monitoring metrics
     * @param signals the list of anomaly signals to which detected anomalies will be added
     */
    private void detectSpikes(MonitoringMetrics current, MonitoringMetrics previous, List<AnomalySignal> signals) {

        Double errorRateIncrease = calculateRelativeIncrease(current.errorRate(),
                previous.errorRate());

        if (errorRateIncrease != null && errorRateIncrease > spikeMultiplier) {
            signals.add(new AnomalySignal(
                    AnomalyType.ERROR_RATE_SPIKE,
                    AnomalySeverity.HIGH,
                    "Error rate increased significantly compared to previous window",
                    current.errorRate(),
                    previous.errorRate(),
                    spikeMultiplier
            ));
        }

        Double latencyIncrease = calculateRelativeIncrease(current.p95LatencyMs(), previous.p95LatencyMs());
        if (latencyIncrease != null && latencyIncrease > spikeMultiplier) {
            signals.add(new AnomalySignal(
                    AnomalyType.LATENCY_SPIKE,
                    AnomalySeverity.HIGH,
                    "P95 latency increased significantly compared to previous window",
                    current.p95LatencyMs(),
                    previous.p95LatencyMs(),
                    spikeMultiplier
            ));
        }
    }

    /**
     * Detects correlated anomalies where both error rate and latency exceed their respective thresholds.
     * If both metrics are above the configured thresholds, a critical degradation anomaly signal is added.
     *
     * @param metrics the current monitoring metrics
     * @param signals the list of anomaly signals to which detected anomalies will be added
     */
    private void detectCorrelatedSignals(MonitoringMetrics metrics, List<AnomalySignal> signals) {

        boolean highErrorRate = metrics.errorRate() > errorRateThreshold;
        boolean highLatency = metrics.p95LatencyMs() > p95LatencyThresholdMs;

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

    /**
     * Calculates the relative increase between the current and previous values.
     * If the previous value is zero or negative, returns null to avoid division by zero.
     *
     * @param current the current value
     * @param previous the previous value
     * @return the relative increase as a Double, or null if previous is zero or negative
     */
    private Double calculateRelativeIncrease(double current, double previous) {
        if (previous <= 0) {
            return null;
        }
        return (current - previous) / previous;
    }

    /**
     * Determines the overall severity of the detected anomalies based on the highest severity among the signals.
     * If no signals are present, returns LOW severity.
     *
     * @param signals the list of anomaly signals
     * @return the overall anomaly severity
     */
    private AnomalySeverity determineOverallSeverity(List<AnomalySignal> signals) {
        return signals.stream()
                .map(AnomalySignal::severity)
                .max(Enum::compareTo)
                .orElse(AnomalySeverity.LOW);
    }
}
