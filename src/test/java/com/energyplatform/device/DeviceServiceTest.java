package com.energyplatform.device;

import com.energyplatform.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceService deviceService;

    @Test
    void create_savesDeviceAndReturnsResponse() {
        DeviceRequest request = new DeviceRequest("Compressor A1", "COMPRESSOR", "Building 3", DeviceStatus.ONLINE);
        when(deviceRepository.save(any(Device.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DeviceResponse response = deviceService.create(request);

        assertThat(response.name()).isEqualTo("Compressor A1");
        assertThat(response.status()).isEqualTo(DeviceStatus.ONLINE);
    }

    @Test
    void findById_throwsWhenDeviceDoesNotExist() {
        when(deviceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deviceService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void findAll_returnsAllDevicesMappedToResponses() {
        Device device = new Device("Boiler 2", "BOILER", "Building 1", DeviceStatus.ONLINE);
        when(deviceRepository.findAll()).thenReturn(List.of(device));

        List<DeviceResponse> result = deviceService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Boiler 2");
    }

    @Test
    void update_changesManagedEntityWithoutExplicitSave() {
        Device device = new Device("Old name", "BOILER", "Building 1", DeviceStatus.OFFLINE);
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));
        DeviceRequest request = new DeviceRequest("New name", "BOILER", "Building 2", DeviceStatus.ONLINE);

        DeviceResponse response = deviceService.update(1L, request);

        assertThat(response.name()).isEqualTo("New name");
        assertThat(response.location()).isEqualTo("Building 2");
        assertThat(response.status()).isEqualTo(DeviceStatus.ONLINE);
        verify(deviceRepository, never()).save(any());
    }

    @Test
    void delete_throwsWhenDeviceDoesNotExist() {
        when(deviceRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> deviceService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(deviceRepository, never()).deleteById(any());
    }

    @Test
    void delete_removesDeviceWhenItExists() {
        when(deviceRepository.existsById(1L)).thenReturn(true);

        deviceService.delete(1L);

        verify(deviceRepository).deleteById(1L);
    }
}
