package com.pulsewatch.controller;

import com.pulsewatch.model.TelemetryEvent;
import com.pulsewatch.service.TelemetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TelemetryController {

    private final TelemetryService telemetryService;

    @PostMapping("/api/telemetry")
    public ResponseEntity<String> submitTelemetry(@RequestBody TelemetryEvent event) {
        telemetryService.submitTelemetry(event);
        return ResponseEntity.accepted().body("Telemetry data accepted for processing.");
    }
}
