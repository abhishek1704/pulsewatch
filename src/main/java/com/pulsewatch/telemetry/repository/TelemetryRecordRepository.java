package com.pulsewatch.telemetry.repository;

import com.pulsewatch.telemetry.model.TelemetryRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface TelemetryRecordRepository extends JpaRepository<TelemetryRecord, Long> {

    List<TelemetryRecord> findByServiceNameAndEventTimestampBetween(
            String serviceName,
            Instant startTime,
            Instant endTime
    );
}
