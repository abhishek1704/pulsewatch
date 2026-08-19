package com.pulsewatch.controller;

import com.pulsewatch.service.ServiceRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.pulsewatch.model.ServiceRegistrationRequest;
import com.pulsewatch.model.ServiceConfigurationEntity;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/services")
@Slf4j
public class ServiceRegistrationApiController {

    private final ServiceRegistrationService serviceRegistrationService;

    @PostMapping
    public ResponseEntity<String> register(
            @Valid @RequestBody ServiceRegistrationRequest request) {
        try {
            ServiceConfigurationEntity cfg = serviceRegistrationService.registerService(request);
            log.info("Service registered: {}", cfg.getName());
            URI location = URI.create("/api/services/" + cfg.getId());
            return ResponseEntity.created(location).body("Service registered successfully with ID: " + cfg.getId());
        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error registering service: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ServiceConfigurationEntity>> getAllServices() {
        return ResponseEntity.ok(serviceRegistrationService.getAllServices());
    }
}