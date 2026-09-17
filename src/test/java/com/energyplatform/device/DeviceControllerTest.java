package com.energyplatform.device;

import com.energyplatform.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeviceController.class)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DeviceService deviceService;

    @Test
    void create_returnsCreatedWithBody() throws Exception {
        DeviceRequest request = new DeviceRequest("Compressor A1", "COMPRESSOR", "Building 3", DeviceStatus.ONLINE);
        DeviceResponse response = new DeviceResponse(1L, "Compressor A1", "COMPRESSOR", "Building 3",
                DeviceStatus.ONLINE, LocalDateTime.of(2026, 9, 10, 12, 0));
        when(deviceService.create(any(DeviceRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/devices")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Compressor A1"));
    }

    @Test
    void create_withBlankName_returnsBadRequest() throws Exception {
        DeviceRequest invalidRequest = new DeviceRequest("", "COMPRESSOR", "Building 3", DeviceStatus.ONLINE);

        mockMvc.perform(post("/api/devices")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").isNotEmpty());
    }

    @Test
    void findAll_returnsListOfDevices() throws Exception {
        DeviceResponse response = new DeviceResponse(1L, "Boiler 2", "BOILER", "Building 1",
                DeviceStatus.ONLINE, LocalDateTime.of(2026, 9, 10, 12, 0));
        when(deviceService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Boiler 2"));
    }

    @Test
    void findById_whenMissing_returnsNotFound() throws Exception {
        when(deviceService.findById(eq(99L))).thenThrow(new ResourceNotFoundException("Device not found: 99"));

        mockMvc.perform(get("/api/devices/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Device not found: 99"));
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/devices/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }
}
