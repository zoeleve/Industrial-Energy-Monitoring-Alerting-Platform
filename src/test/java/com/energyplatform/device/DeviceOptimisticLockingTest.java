package com.energyplatform.device;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.energyplatform.TestcontainersConfiguration;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;

/**
 * Optimistic locking is a Hibernate flush-time mechanism, not something our own code implements —
 * it can only be proven against a real database, hence @DataJpaTest + Testcontainers rather than a
 * Mockito unit test.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class DeviceOptimisticLockingTest {

  @Autowired private EntityManager entityManager;

  @Autowired private DeviceRepository deviceRepository;

  @Test
  void concurrentUpdate_throwsOptimisticLockingException() {
    Device saved =
        deviceRepository.saveAndFlush(
            new Device("Compressor A1", "COMPRESSOR", "Building 3", DeviceStatus.ONLINE));
    Long id = saved.getId();
    entityManager.clear();

    // Two independent "reads", each detached immediately so Hibernate can't silently collapse
    // them onto the same in-session managed instance (which would hide the whole conflict).
    Device copy1 = deviceRepository.findById(id).orElseThrow();
    entityManager.clear();
    Device copy2 = deviceRepository.findById(id).orElseThrow();
    entityManager.clear();

    copy1.setStatus(DeviceStatus.MAINTENANCE);
    deviceRepository.saveAndFlush(copy1);
    entityManager.clear();

    copy2.setStatus(DeviceStatus.OFFLINE);
    assertThatThrownBy(() -> deviceRepository.saveAndFlush(copy2))
        .isInstanceOf(OptimisticLockingFailureException.class);
  }
}
