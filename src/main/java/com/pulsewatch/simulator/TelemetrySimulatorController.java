package com.pulsewatch.simulator;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/simulator")
public class TelemetrySimulatorController {

    private final TelemetrySimulatorService simulatorService;

    @PostMapping("/run")
    public ResponseEntity<String> runScenario(
            @RequestParam String scenario,
            @RequestParam(defaultValue = "20") int events,
            @RequestParam(defaultValue = "1000") long intervalMs) {

        simulatorService.runScenario(scenario, events, intervalMs);

        return ResponseEntity.ok(
                "Simulation completed: scenario=" + scenario +
                        ", events=" + events
        );
    }
}