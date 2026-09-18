package com.energyplatform.measurement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

  Page<Measurement> findByDeviceId(Long deviceId, Pageable pageable);
}
