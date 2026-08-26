package com.pulsewatch.anomaly.service;

import com.pulsewatch.anomaly.model.AnomalyDetectedEvent;
import com.pulsewatch.configuration.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnomalyEventPublisher {

    private final KafkaTemplate<String, AnomalyDetectedEvent> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public void publish(AnomalyDetectedEvent event) {

        kafkaTemplate.send(
                kafkaProperties.getAnomalyTopic(),
                event.serviceName(),
                event
        ).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish anomaly event. service={}", event.serviceName(), ex);
                return;
            }
            log.debug("Anomaly event published. service={}, offset={}", event.serviceName(),
                    result.getRecordMetadata().offset()
            );
        });
    }
}