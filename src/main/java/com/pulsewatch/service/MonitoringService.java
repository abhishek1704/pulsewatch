package com.pulsewatch.service;

import com.pulsewatch.model.MonitoringMetrics;
import com.pulsewatch.model.TelemetryRecord;
import com.pulsewatch.repository.TelemetryRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonitoringService {

    private final TelemetryRecordRepository telemetryRecordRepository;

    public MonitoringMetrics getMonitoringMetrics(String serviceName) {

        Instant endTime = Instant.now();
        Instant startTime = endTime.minus(1, ChronoUnit.HOURS);

        List<TelemetryRecord> records =
                telemetryRecordRepository.findByServiceNameAndEventTimestampBetween(
                        serviceName,
                        startTime,
                        endTime
                );

        long requestCount = records.size();
        long errorCount = records.stream()
                .filter(record -> record.getStatusCode() >= 500).count();
        double errorRate = requestCount == 0 ? 0.0 : (double) errorCount / requestCount;
        double averageLatencyMs = records.stream()
                .mapToLong(TelemetryRecord::getLatencyMs)
                .average()
                .orElse(0.0);
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
