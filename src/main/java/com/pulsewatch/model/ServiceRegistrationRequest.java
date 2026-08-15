package com.pulsewatch.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceRegistrationRequest {
    private String name;
    private String environment;
    private String baseUrl;
    private String healthEndpoint;
}