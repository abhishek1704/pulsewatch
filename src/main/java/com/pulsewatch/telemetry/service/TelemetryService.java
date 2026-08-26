package com.pulsewatch.telemetry.service;

import com.pulsewatch.configuration.KafkaProperties;
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
    private final KafkaProperties kafkaProperties;

    /**
     * Submits telemetry data to the Kafka topic for processing.
     *
     * @param event The telemetry event to be submitted.
     */
    public void submitTelemetry(TelemetryEvent event) {
        log.info("Initiating Telemetry Event: {}", event);
        kafkaTemplate.send(kafkaProperties.getTelemetryTopic(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send telemetry data to Kafka", ex);
                    } else {
                        log.debug(
                                "Telemetry event published successfully. service={}, partition={}, offset={}",
                                event.getServiceName(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );
                    }
                });
    }
}
