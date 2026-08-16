package com.pulsewatch.scheduler;

import com.pulsewatch.model.MonitoringMetrics;
import com.pulsewatch.service.MonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonitoringScheduler {

    private final MonitoringService monitoringService;

    @Scheduled(cron = "0 */5 * * * ?")
    public void monitorServices() {
        // Logic to monitor registered services
        log.info("Monitoring registered services...");
        MonitoringMetrics metrics = monitoringService.getMonitoringMetrics("payment-api");
        log.info("Retrieved monitoring metrics for payment-api");
        log.info("Monitoring Metrics: {}", metrics);
    }
}
