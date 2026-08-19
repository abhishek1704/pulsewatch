package com.pulsewatch.service;

import com.pulsewatch.model.ServiceConfigurationEntity;
import com.pulsewatch.model.ServiceRegistrationRequest;
import com.pulsewatch.repository.ServiceRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceRegistrationService {

    private final ServiceRegistrationRepository serviceRegistry;

    /**
     * Registers a new service configuration.
     *
     * @param request the service registration request containing name and baseUrl
     * @return the saved ServiceConfiguration
     * @throws IllegalArgumentException if the request is invalid
     */
    public ServiceConfigurationEntity registerService(ServiceRegistrationRequest request) {
        ServiceConfigurationEntity config = new ServiceConfigurationEntity(request);
        return serviceRegistry.save(config);
    }

    /**
     * Retrieves all registered service configurations.
     *
     * @return a list of all ServiceConfiguration objects
     */
    public List<ServiceConfigurationEntity> getAllServices() {
        return serviceRegistry.findAll();
    }
}