package com.pulsewatch;

import com.pulsewatch.configuration.KafkaProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(KafkaProperties.class)
public class PulsewatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(PulsewatchApplication.class, args);
	}

}
