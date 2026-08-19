package com.pulsewatch.ai.model;

import java.util.List;

public record AiIncidentAnalysis(
        String summary,
        String probableCause,
        String impact,
        List<String> evidence,
        List<String> recommendations
) {
}