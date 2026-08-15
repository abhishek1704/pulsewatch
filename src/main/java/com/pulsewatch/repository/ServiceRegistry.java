package com.pulsewatch.repository;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import com.pulsewatch.model.ServiceConfiguration;
import java.util.Collection;

@Component
public class ServiceRegistry {
    private final ConcurrentHashMap<String, ServiceConfiguration> store = new ConcurrentHashMap<>();

    public ServiceConfiguration save(ServiceConfiguration cfg) {
        store.put(cfg.getId(), cfg);
        return cfg;
    }

    public ServiceConfiguration findById(String id) {
        return store.get(id);
    }

    public Collection<ServiceConfiguration> findAll() {
        return store.values();
    }
}