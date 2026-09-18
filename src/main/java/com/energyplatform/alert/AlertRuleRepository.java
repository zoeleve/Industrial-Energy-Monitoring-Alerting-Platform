package com.energyplatform.alert;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {

  List<AlertRule> findByDeviceIdAndEnabledTrue(Long deviceId);
}
