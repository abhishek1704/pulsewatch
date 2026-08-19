package com.pulsewatch.telemetry.service;

import com.pulsewatch.telemetry.model.TelemetryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelemetryService {

    private final KafkaTemplate<String, TelemetryEvent> kafkaTemplate;

    /**
     * Submits telemetry data to the Kafka topic for processing.
     *
     * @param event The telemetry event to be submitted.
     */
    public void submitTelemetry(TelemetryEvent event) {
        kafkaTemplate.send("api-telemetry", event);
        log.info("Telemetry data submitted successfully. Event: {}", event);
    }
}
