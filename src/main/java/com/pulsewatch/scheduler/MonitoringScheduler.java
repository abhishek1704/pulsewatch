package com.pulsewatch.scheduler;

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

    @Scheduled(cron = "0 */1 * * * ?")
    public void monitorServices() {

        log.info("Starting scheduled monitoring");

        try {
            monitoringService.monitorRegisteredServices();
        } catch (Exception e) {
            log.error("Scheduled monitoring failed", e);
        }

        log.info("Scheduled monitoring completed");
    }
}
