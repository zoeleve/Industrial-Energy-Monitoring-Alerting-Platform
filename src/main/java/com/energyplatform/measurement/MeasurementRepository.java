package com.energyplatform.measurement;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

  List<Measurement> findByDeviceId(Long deviceId);
}
