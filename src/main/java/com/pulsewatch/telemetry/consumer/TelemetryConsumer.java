package com.pulsewatch.telemetry.consumer;

import com.pulsewatch.telemetry.model.TelemetryEvent;
import com.pulsewatch.telemetry.service.TelemetryProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelemetryConsumer {

    private final TelemetryProcessingService telemetryProcessingService;

    @KafkaListener(topics = "${pulsewatch.kafka.telemetry-topic}", groupId = "${pulsewatch.kafka.consumer.group-id}")
    public void consume(TelemetryEvent event) {
        log.info("Received telemetry data: {}", event);
        telemetryProcessingService.processTelemetryData(event);
    }

}

