package com.pulsewatch.service;

import com.pulsewatch.model.ServiceConfiguration;
import com.pulsewatch.model.ServiceRegistrationRequest;
import com.pulsewatch.repository.ServiceRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceRegistrationService {

    private final ServiceRegistrationRepository serviceRegistry;

    public ServiceConfiguration registerService(ServiceRegistrationRequest request) {
        validate(request);
        ServiceConfiguration config = new ServiceConfiguration(request);
        return serviceRegistry.save(config);
    }

    private void validate(ServiceRegistrationRequest request) {
        if (request == null || isBlank(request.getName()) || isBlank(request.getBaseUrl())) {
            throw new IllegalArgumentException("Service name and baseUrl are required");
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }


    public List<ServiceConfiguration> getAllServices() {
        return serviceRegistry.findAll();
    }
}