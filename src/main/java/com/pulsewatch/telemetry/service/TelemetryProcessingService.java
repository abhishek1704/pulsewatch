package com.pulsewatch.telemetry.service;

import com.pulsewatch.telemetry.model.TelemetryEvent;
import com.pulsewatch.telemetry.model.TelemetryRecord;
import com.pulsewatch.telemetry.repository.TelemetryRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelemetryProcessingService {
    private final TelemetryRecordRepository repository;

    /**
     * Processes the incoming telemetry data and saves it to the database.
     *
     * @param event The telemetry event to be processed.
     */
    public void processTelemetryData(TelemetryEvent event) {
        TelemetryRecord record = new TelemetryRecord(event);
        repository.save(record);
        log.info("Telemetry data processed and saved: {}", record);
    }
}
