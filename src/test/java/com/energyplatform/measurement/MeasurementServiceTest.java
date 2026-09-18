package com.energyplatform.measurement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.energyplatform.alert.AlertService;
import com.energyplatform.common.exception.ResourceNotFoundException;
import com.energyplatform.device.Device;
import com.energyplatform.device.DeviceRepository;
import com.energyplatform.device.DeviceStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class MeasurementServiceTest {

  @Mock private MeasurementRepository measurementRepository;

  @Mock private DeviceRepository deviceRepository;

  @Mock private AlertService alertService;

  @InjectMocks private MeasurementService measurementService;

  @Test
  void save_persistsMeasurementForExistingDevice() {
    Device device = new Device("Compressor A1", "COMPRESSOR", "Building 3", DeviceStatus.ONLINE);
    when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));
    MeasurementEvent event =
        new MeasurementEvent(1L, LocalDateTime.of(2026, 9, 17, 12, 0), 14.7, 5.2, 230);

    measurementService.save(event);

    ArgumentCaptor<Measurement> captor = ArgumentCaptor.forClass(Measurement.class);
    verify(measurementRepository).save(captor.capture());
    assertThat(captor.getValue().getDevice()).isEqualTo(device);
    assertThat(captor.getValue().getPower()).isEqualTo(5.2);
  }

  @Test
  void save_throwsWhenDeviceDoesNotExist() {
    when(deviceRepository.findById(99L)).thenReturn(Optional.empty());
    MeasurementEvent event = new MeasurementEvent(99L, LocalDateTime.now(), 14.7, 5.2, 230);

    assertThatThrownBy(() -> measurementService.save(event))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("99");

    verify(measurementRepository, never()).save(any());
  }

  @Test
  void findByDevice_returnsMappedResponses() {
    Device device = new Device("Boiler 2", "BOILER", "Building 1", DeviceStatus.ONLINE);
    Measurement measurement =
        new Measurement(device, LocalDateTime.of(2026, 9, 17, 12, 0), 14.7, 5.2, 230);
    PageRequest pageable = PageRequest.of(0, 20);
    when(measurementRepository.findByDeviceId(1L, pageable))
        .thenReturn(new PageImpl<>(List.of(measurement)));

    Page<MeasurementResponse> result = measurementService.findByDevice(1L, pageable);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).power()).isEqualTo(5.2);
  }
}
