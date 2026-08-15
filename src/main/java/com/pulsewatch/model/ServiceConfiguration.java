package com.pulsewatch.model;

import java.util.UUID;

public class ServiceConfiguration {
    private String id;
    private String name;
    private String environment;
    private String baseUrl;
    private String healthEndpoint;

    public ServiceConfiguration() {
    }

    public ServiceConfiguration(ServiceRegistrationRequest req) {
        this.id = UUID.randomUUID().toString();
        this.name = req.getName();
        this.environment = req.getEnvironment();
        this.baseUrl = req.getBaseUrl();
        this.healthEndpoint = req.getHealthEndpoint();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getHealthEndpoint() {
        return healthEndpoint;
    }

    public void setHealthEndpoint(String healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }
}