package com.pulsewatch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.pulsewatch.model.ServiceRegistrationRequest;
import com.pulsewatch.model.ServiceConfiguration;
import com.pulsewatch.repository.ServiceRegistry;

import java.net.URI;

@RestController
@RequestMapping("/api/services")
public class ServiceRegistrationApiController {

    private final ServiceRegistry registry;

    @Autowired
    public ServiceRegistrationApiController(ServiceRegistry registry) {
        this.registry = registry;
    }

    @PostMapping
    public ResponseEntity<ServiceConfiguration> register(@RequestBody ServiceRegistrationRequest request) {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        ServiceConfiguration cfg = new ServiceConfiguration(request);
        registry.save(cfg);

        URI location = URI.create("/api/services/" + cfg.getId());
        return ResponseEntity.created(location).body(cfg);
    }
}