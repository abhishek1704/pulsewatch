package com.pulsewatch.ai;

import com.pulsewatch.ai.model.AiIncidentAnalysis;
import com.pulsewatch.anomaly.model.AnomalyAnalysis;
import com.pulsewatch.monitoring.model.MonitoringMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiIncidentAnalysisService {

    private final ChatClient chatClient;

    public AiIncidentAnalysis analyze(MonitoringMetrics currentMetrics,
                                      MonitoringMetrics previousMetrics, AnomalyAnalysis analysis) {

        String prompt = """
                You are an incident analysis assistant for PulseWatch.

                Analyze the monitoring data and detected anomalies below.

                Your job is to explain what the data indicates and provide
                useful investigation recommendations.

                Rules:
                - Use only the information provided below.
                - Do not invent facts or assume an unsupported root cause.
                - Use the comparison between the current and previous windows
                  to identify meaningful changes.
                - Clearly distinguish observed facts from possible causes.
                - If the root cause cannot be determined from the available
                  data, explicitly state that.
                - Recommendations should be specific to the observed signals.
                - Do not simply repeat the input metrics.
                - Keep the response concise but technically useful.

                =========================
                CURRENT MONITORING WINDOW
                =========================

                Service: %s
                Window Start: %s
                Window End: %s

                Request Count: %d
                Error Count: %d
                Error Rate: %.2f%%
                Average Latency: %.2f ms
                P95 Latency: %.2f ms

                =========================
                PREVIOUS MONITORING WINDOW
                =========================

                Service: %s
                Window Start: %s
                Window End: %s

                Request Count: %d
                Error Count: %d
                Error Rate: %.2f%%
                Average Latency: %.2f ms
                P95 Latency: %.2f ms

                =========================
                DETECTED ANOMALIES
                =========================

                Overall Severity: %s

                %s
                """.formatted(
                currentMetrics.serviceName(),
                currentMetrics.windowStart(),
                currentMetrics.windowEnd(),
                currentMetrics.requestCount(),
                currentMetrics.errorCount(),
                currentMetrics.errorRate() * 100,
                currentMetrics.averageLatencyMs(),
                currentMetrics.p95LatencyMs(),

                previousMetrics.serviceName(),
                previousMetrics.windowStart(),
                previousMetrics.windowEnd(),
                previousMetrics.requestCount(),
                previousMetrics.errorCount(),
                previousMetrics.errorRate() * 100,
                previousMetrics.averageLatencyMs(),
                previousMetrics.p95LatencyMs(),

                analysis.severity(),
                formatSignals(analysis)
        );

        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .entity(AiIncidentAnalysis.class);
    }

    private String formatSignals(AnomalyAnalysis analysis) {
        return analysis.signals().stream()
                .map(signal -> """
                        Type: %s
                        Severity: %s
                        Description: %s
                        Current Value: %s
                        Previous Value: %s
                        Threshold: %s
                        """.formatted(
                        signal.type(),
                        signal.severity(),
                        signal.description(),
                        signal.currentValue(),
                        signal.previousValue(),
                        signal.threshold()
                ))
                .collect(Collectors.joining("\n"));
    }
}