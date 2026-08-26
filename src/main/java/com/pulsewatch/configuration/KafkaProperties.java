package com.pulsewatch.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "pulsewatch.kafka")
public class KafkaProperties {

    private String telemetryTopic;
    private String consumerGroupId;
    private String anomalyTopic;
    private String aiConsumerGroupId;
}