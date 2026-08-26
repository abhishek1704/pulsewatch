package com.pulsewatch.simulator.ai;

import com.pulsewatch.ai.AiIncidentAnalysisService;
import com.pulsewatch.ai.model.AiIncidentAnalysis;
import com.pulsewatch.anomaly.model.AnomalyAnalysis;
import com.pulsewatch.anomaly.model.AnomalySeverity;
import com.pulsewatch.anomaly.model.AnomalySignal;
import com.pulsewatch.anomaly.model.AnomalyType;
import com.pulsewatch.monitoring.model.MonitoringMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiTestController {

    private final AiTestService aiTestService;
    private final AiIncidentAnalysisService aiIncidentAnalysisService;

    @PostMapping("/test")
    public AiIncidentAnalysis test() {

        Instant now = Instant.now();
        MonitoringMetrics previousMetrics = new MonitoringMetrics(
                "payment-api",
                now.minusSeconds(30 * 60),
                now.minusSeconds(15 * 60),
                1200,
                72,
                0.06,
                280.0,
                420.0
        );
        MonitoringMetrics currentMetrics = new MonitoringMetrics(
                "payment-api",
                now.minusSeconds(15 * 60),
                now,
                900,
                350,
                0.3888888889,
                950.0,
                1807.0
        );

        AnomalyAnalysis analysis = new AnomalyAnalysis(
                "payment-api",
                now,
                AnomalySeverity.CRITICAL,
                List.of(
                        new AnomalySignal(
                                AnomalyType.HIGH_ERROR_RATE,
                                AnomalySeverity.HIGH,
                                "Error rate exceeded configured threshold",
                                0.3888888889,
                                null,
                                0.1
                        ),
                        new AnomalySignal(
                                AnomalyType.HIGH_LATENCY,
                                AnomalySeverity.HIGH,
                                "P95 latency exceeded configured threshold",
                                1807.0,
                                null,
                                1000.0
                        ),
                        new AnomalySignal(
                                AnomalyType.CRITICAL_DEGRADATION,
                                AnomalySeverity.CRITICAL,
                                "Service is experiencing both elevated error rate and latency",
                                0.3888888889,
                                null,
                                null
                        )
                )
        );

        return aiIncidentAnalysisService.analyze(new com.pulsewatch.anomaly.model.AnomalyDetectedEvent(
                "payment-api",
                AnomalySeverity.CRITICAL,
                analysis.signals(),
                currentMetrics,
                previousMetrics,
                now
        ));
    }
}