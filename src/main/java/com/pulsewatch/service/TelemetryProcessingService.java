package com.pulsewatch.service;

import com.pulsewatch.model.TelemetryEvent;
import com.pulsewatch.model.TelemetryRecord;
import com.pulsewatch.repository.TelemetryRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelemetryProcessingService {
    private final TelemetryRecordRepository repository;

    public void processTelemetryData(TelemetryEvent event) {
        TelemetryRecord record = new TelemetryRecord(event);
        repository.save(record);
        log.info("Telemetry data processed and saved: {}", record);
    }
}
