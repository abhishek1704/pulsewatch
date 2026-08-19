package com.pulsewatch.monitoring.service;

import com.pulsewatch.monitoring.model.MonitoringMetrics;
import com.pulsewatch.telemetry.model.TelemetryRecord;
import com.pulsewatch.telemetry.repository.TelemetryRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MonitoringMetricsUtility {

    private final TelemetryRecordRepository telemetryRecordRepository;

    /**
     * Retrieves monitoring metrics for a specific service within a given time window.
     * This method queries the telemetry records for the specified service and calculates
     * various metrics such as request count, error count, error rate, average latency,
     * and p95 latency.
     *
     * @param serviceName The name of the service for which to retrieve metrics.
     * @param startTime   The start time of the monitoring window.
     * @param endTime     The end time of the monitoring window.
     * @return A MonitoringMetrics object containing the calculated metrics for the service.
     */
    public MonitoringMetrics getMonitoringMetrics(String serviceName, Instant startTime, Instant endTime) {

        List<TelemetryRecord> records = telemetryRecordRepository
                .findByServiceNameAndEventTimestampBetween(serviceName, startTime, endTime);
        long requestCount = records.size();
        long errorCount = records.stream().filter(record -> record.getStatusCode() >= 500).count();
        double errorRate = requestCount == 0 ? 0.0 : (double) errorCount / requestCount;
        double averageLatencyMs = records.stream().mapToLong(TelemetryRecord::getLatencyMs).average().orElse(0.0);
        long p95LatencyMs = calculateP95(records);

        return new MonitoringMetrics(
                serviceName,
                startTime,
                endTime,
                requestCount,
                errorCount,
                errorRate,
                averageLatencyMs,
                p95LatencyMs
        );
    }

    /**
     * Calculates the 95th percentile (P95) latency from a list of telemetry records.
     * If the list is empty, returns 0. Otherwise, it sorts the latencies and finds the value
     * at the index corresponding to the 95th percentile.
     *
     * @param records The list of telemetry records from which to calculate P95 latency.
     * @return The P95 latency in milliseconds.
     */
    private long calculateP95(List<TelemetryRecord> records) {
        if (records.isEmpty()) {
            return 0L;
        }

        List<Long> latencies = records.stream()
                .map(TelemetryRecord::getLatencyMs)
                .sorted()
                .toList();

        int index = (int) Math.ceil(0.95 * latencies.size()) - 1;
        return latencies.get(index);
    }
}
