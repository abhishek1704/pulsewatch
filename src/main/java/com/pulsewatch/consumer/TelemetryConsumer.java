package com.pulsewatch.consumer;

import com.pulsewatch.model.TelemetryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TelemetryConsumer {

    @KafkaListener(topics = "api-telemetry", groupId = "pulsewatch-monitoring")
    public void consume(TelemetryEvent event) {
        // Process the telemetry data here
        log.info("Received telemetry data: {}", event);
    }

}

