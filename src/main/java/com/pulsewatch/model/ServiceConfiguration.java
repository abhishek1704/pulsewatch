package com.pulsewatch.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "service_configuration")
@Getter
@NoArgsConstructor
public class ServiceConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String environment;

    @Column(nullable = false)
    private String baseUrl;

    @Column(nullable = false)
    private String healthEndpoint;

    public ServiceConfiguration(ServiceRegistrationRequest request) {
        this.name = request.getName();
        this.environment = request.getEnvironment();
        this.baseUrl = request.getBaseUrl();
        this.healthEndpoint = request.getHealthEndpoint();
    }
}