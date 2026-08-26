package com.pulsewatch.anomaly.service;

import com.pulsewatch.ai.AiIncidentAnalysisService;
import com.pulsewatch.anomaly.model.AnomalyDetectedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnomalyEventConsumer {

    private final AiIncidentAnalysisService aiIncidentAnalysisService;

    @KafkaListener(
            topics = "${pulsewatch.kafka.anomaly-topic}",
            groupId = "${pulsewatch.kafka.ai-consumer.group-id}"
    )
    public void consume(AnomalyDetectedEvent event) {

        log.info(
                "Received anomaly event for service={}",
                event.serviceName()
        );

        aiIncidentAnalysisService.analyze(event);
    }
}