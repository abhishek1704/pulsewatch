package com.pulsewatch.simulator.ai;

import com.pulsewatch.ai.model.AiIncidentAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiTestService {

    private final ChatClient chatClient;

    public String ask(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    public AiIncidentAnalysis testStructuredOutput() {

        return chatClient
                .prompt()
                .user("""
                    Analyze this hypothetical monitoring event:

                    Service: payment-api
                    Severity: CRITICAL

                    Signals:
                    - HIGH_ERROR_RATE: Error rate is 38.89%, threshold is 10%.
                    - HIGH_LATENCY: P95 latency is 1807ms, threshold is 1000ms.
                    - CRITICAL_DEGRADATION: Both elevated error rate and latency
                      were detected.

                    Provide:
                    - a concise summary
                    - the probable cause
                    - the impact
                    - recommended actions
                    """)
                .call()
                .entity(AiIncidentAnalysis.class);
    }
}