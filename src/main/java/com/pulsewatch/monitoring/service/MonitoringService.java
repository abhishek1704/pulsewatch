package com.pulsewatch.monitoring.service;

import com.pulsewatch.anomaly.model.AnomalyAnalysis;
import com.pulsewatch.anomaly.model.AnomalyDetectedEvent;
import com.pulsewatch.anomaly.service.AnomalyEventPublisher;
import com.pulsewatch.monitoring.model.MonitoringMetrics;
import com.pulsewatch.model.ServiceConfigurationEntity;
import com.pulsewatch.anomaly.service.AnomalyDetector;
import com.pulsewatch.repository.ServiceRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonitoringService {

    @Value("${pulsewatch.monitoring.window.minutes:15}")
    private int monitoringWindowMinutes;

    private final ServiceRegistrationRepository serviceRegistrationRepository;
    private final AnomalyDetector anomalyDetector;
    private final MonitoringMetricsService monitoringMetricsService;
    private final AnomalyEventPublisher anomalyEventPublisher;

    /**
     * Monitors all registered services for anomalies based on telemetry data.
     * This method retrieves all registered services and analyzes their telemetry data
     * for the current and previous monitoring windows. Anomalies are detected and logged.
     */
    public void monitorRegisteredServices() {
        List<ServiceConfigurationEntity> services = serviceRegistrationRepository.findAll();
        if (services.isEmpty()) {
            log.info("No registered services found for monitoring");
            return;
        }
        Instant currentEnd = Instant.now();
        Instant currentStart = currentEnd.minus(monitoringWindowMinutes, ChronoUnit.MINUTES);
        Instant previousStart = currentStart.minus(monitoringWindowMinutes, ChronoUnit.MINUTES);

        for (ServiceConfigurationEntity service : services) {
            try {
                monitorService(service.getName(), currentStart,currentEnd, previousStart);
            } catch (Exception e) {
                log.error("Failed to monitor service: {}", service.getName(), e);
            }
        }
    }

    /**
     * Monitors a specific service for anomalies based on telemetry data.
     * This method retrieves telemetry metrics for the current and previous monitoring windows,
     * performs anomaly detection, and logs the results.
     *
     * @param serviceName   The name of the service to monitor.
     * @param currentStart  The start time of the current monitoring window.
     * @param currentEnd    The end time of the current monitoring window.
     * @param previousStart The start time of the previous monitoring window.
     */
    private void monitorService(String serviceName,
                                Instant currentStart, Instant currentEnd, Instant previousStart) {

        MonitoringMetrics current = monitoringMetricsService.getMonitoringMetrics(serviceName, currentStart, currentEnd);
        MonitoringMetrics previous = monitoringMetricsService.getMonitoringMetrics(serviceName, previousStart, currentStart);
        AnomalyAnalysis analysis = anomalyDetector.detect(current, previous);

        if (analysis.hasAnomalies()) {
            log.info("Monitoring completed for service={}, severity={}, signals={}", serviceName, analysis.severity(), analysis.signals());
            log.info("Publishing anomaly event for Ai analytics for service={}", serviceName);

            AnomalyDetectedEvent anomalyEvent = new AnomalyDetectedEvent(
                    analysis.serviceName(),
                    analysis.severity(),
                    analysis.signals(),
                    current,
                    previous,
                    Instant.now()
            );

            anomalyEventPublisher.publish(anomalyEvent);
        }
    }

}

