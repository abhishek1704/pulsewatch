package com.pulsewatch.service;

import com.pulsewatch.model.AnomalyAnalysis;
import com.pulsewatch.model.MonitoringMetrics;
import com.pulsewatch.model.ServiceConfiguration;
import com.pulsewatch.model.TelemetryRecord;
import com.pulsewatch.repository.ServiceRegistrationRepository;
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

    private static final int MONITORING_WINDOW_MINUTES = 720;

    private final ServiceRegistrationRepository serviceRegistrationRepository;
    private final TelemetryRecordRepository telemetryRecordRepository;
    private final AnomalyDetector anomalyDetector;

    public void monitorRegisteredServices() {
        List<ServiceConfiguration> services = serviceRegistrationRepository.findAll();
        if (services.isEmpty()) {
            log.info("No registered services found for monitoring");
            return;
        }
        Instant currentEnd = Instant.now();
        Instant currentStart = currentEnd.minus(MONITORING_WINDOW_MINUTES, ChronoUnit.MINUTES);
        Instant previousStart = currentStart.minus(MONITORING_WINDOW_MINUTES, ChronoUnit.MINUTES);

        for (ServiceConfiguration service : services) {
            try {
                monitorService(service.getName(), currentStart,currentEnd, previousStart);
            } catch (Exception e) {
                log.error("Failed to monitor service: {}", service.getName(), e);
            }
        }
    }

    private void monitorService(String serviceName,
                                Instant currentStart, Instant currentEnd, Instant previousStart) {

        MonitoringMetrics current = getMonitoringMetrics(serviceName, currentStart, currentEnd);
        MonitoringMetrics previous = getMonitoringMetrics(serviceName, previousStart, currentStart);

        AnomalyAnalysis analysis = anomalyDetector.detect(current, previous);

        log.info("Monitoring completed for service={}, severity={}, signals={}",
                serviceName,
                analysis.severity(),
                analysis.signals());
    }

    private MonitoringMetrics getMonitoringMetrics(String serviceName, Instant startTime, Instant endTime) {

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
