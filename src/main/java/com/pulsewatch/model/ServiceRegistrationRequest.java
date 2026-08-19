package com.pulsewatch.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ServiceRegistrationRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String environment;

    @NotBlank
    private String baseUrl;

    @NotBlank
    private String healthEndpoint;
}