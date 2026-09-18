package com.energyplatform.device;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.energyplatform.auth.AppUserDetailsService;
import com.energyplatform.auth.JwtAccessDeniedHandler;
import com.energyplatform.auth.JwtAuthenticationEntryPoint;
import com.energyplatform.auth.JwtService;
import com.energyplatform.auth.SecurityConfig;
import com.energyplatform.common.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

// Real SecurityConfig, not Boot's slice default, so CSRF-disabled + role rules actually apply.
@WebMvcTest(DeviceController.class)
@Import({SecurityConfig.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
class DeviceControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private DeviceService deviceService;

  // JwtAuthenticationFilter is a servlet Filter, so @WebMvcTest scans it in even though its own
  // @Service dependencies are outside this slice — mock those out so the filter bean can wire up.
  @MockitoBean private JwtService jwtService;

  @MockitoBean private AppUserDetailsService appUserDetailsService;

  @Test
  @WithMockUser(roles = "ADMIN")
  void create_returnsCreatedWithBody() throws Exception {
    DeviceRequest request =
        new DeviceRequest("Compressor A1", "COMPRESSOR", "Building 3", DeviceStatus.ONLINE);
    DeviceResponse response =
        new DeviceResponse(
            1L,
            "Compressor A1",
            "COMPRESSOR",
            "Building 3",
            DeviceStatus.ONLINE,
            LocalDateTime.of(2026, 9, 10, 12, 0));
    when(deviceService.create(any(DeviceRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/api/devices")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Compressor A1"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void create_withBlankName_returnsBadRequest() throws Exception {
    DeviceRequest invalidRequest =
        new DeviceRequest("", "COMPRESSOR", "Building 3", DeviceStatus.ONLINE);

    mockMvc
        .perform(
            post("/api/devices")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.details").isNotEmpty());
  }

  @Test
  @WithMockUser(roles = "OPERATOR")
  void findAll_returnsListOfDevices() throws Exception {
    DeviceResponse response =
        new DeviceResponse(
            1L,
            "Boiler 2",
            "BOILER",
            "Building 1",
            DeviceStatus.ONLINE,
            LocalDateTime.of(2026, 9, 10, 12, 0));
    when(deviceService.findAll(any())).thenReturn(new PageImpl<>(List.of(response)));

    mockMvc
        .perform(get("/api/devices"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].name").value("Boiler 2"));
  }

  @Test
  @WithMockUser(roles = "OPERATOR")
  void findById_whenMissing_returnsNotFound() throws Exception {
    when(deviceService.findById(eq(99L)))
        .thenThrow(new ResourceNotFoundException("Device not found: 99"));

    mockMvc
        .perform(get("/api/devices/99"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Device not found: 99"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void delete_returnsNoContent() throws Exception {
    mockMvc
        .perform(delete("/api/devices/1"))
        .andExpect(status().isNoContent())
        .andExpect(content().string(""));
  }
}
