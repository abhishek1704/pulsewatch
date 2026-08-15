package com.pulsewatch.controller;

import com.pulsewatch.service.ServiceRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.pulsewatch.model.ServiceRegistrationRequest;
import com.pulsewatch.model.ServiceConfiguration;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/services")
@Slf4j
public class ServiceRegistrationApiController {

    private final ServiceRegistrationService serviceRegistrationService;

    @PostMapping
    public ResponseEntity<ServiceConfiguration> register(@RequestBody ServiceRegistrationRequest request) {
        try {
            ServiceConfiguration cfg = serviceRegistrationService.registerService(request);
            log.info("Service registered: {}", cfg.getName());
            URI location = URI.create("/api/services/" + cfg.getId());
            return ResponseEntity.created(location).body(cfg);
        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error registering service: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ServiceConfiguration>> getAllServices() {
        return ResponseEntity.ok(serviceRegistrationService.getAllServices());
    }
}