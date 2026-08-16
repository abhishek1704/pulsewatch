package com.pulsewatch.consumer;

import com.pulsewatch.model.TelemetryEvent;
import com.pulsewatch.service.TelemetryProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelemetryConsumer {

    private final TelemetryProcessingService telemetryProcessingService;

    @KafkaListener(topics = "api-telemetry", groupId = "pulsewatch-monitoring")
    public void consume(TelemetryEvent event) {
        log.info("Received telemetry data: {}", event);
        telemetryProcessingService.processTelemetryData(event);
    }

}

