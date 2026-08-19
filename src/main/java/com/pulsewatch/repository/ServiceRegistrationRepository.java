package com.pulsewatch.repository;

import com.pulsewatch.model.ServiceConfigurationEntity;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ServiceRegistrationRepository
        extends JpaRepository<ServiceConfigurationEntity, Long> {
}