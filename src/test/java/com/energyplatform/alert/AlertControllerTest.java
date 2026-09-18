package com.energyplatform.alert;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.energyplatform.auth.AppUserDetailsService;
import com.energyplatform.auth.JwtAccessDeniedHandler;
import com.energyplatform.auth.JwtAuthenticationEntryPoint;
import com.energyplatform.auth.JwtService;
import com.energyplatform.auth.SecurityConfig;
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

@WebMvcTest(AlertController.class)
@Import({SecurityConfig.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
class AlertControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private AlertService alertService;

  @MockitoBean private JwtService jwtService;

  @MockitoBean private AppUserDetailsService appUserDetailsService;

  @Test
  @WithMockUser(roles = "ADMIN")
  void createRule_returnsCreated() throws Exception {
    AlertRuleRequest request =
        new AlertRuleRequest(
            1L, MeasurementMetric.POWER, RuleOperator.GREATER_THAN, 100, "HIGH_POWER", true);
    AlertRuleResponse response =
        new AlertRuleResponse(
            1L, 1L, MeasurementMetric.POWER, RuleOperator.GREATER_THAN, 100, "HIGH_POWER", true);
    when(alertService.createRule(any(AlertRuleRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/api/alerts/rules")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.alertType").value("HIGH_POWER"));
  }

  @Test
  @WithMockUser(roles = "OPERATOR")
  void findAlerts_returnsPagedAlerts() throws Exception {
    AlertResponse response =
        new AlertResponse(
            1L, 1L, 1L, "HIGH_POWER", 137, AlertStatus.OPEN, LocalDateTime.now(), null);
    when(alertService.findAlerts(any())).thenReturn(new PageImpl<>(List.of(response)));

    mockMvc
        .perform(get("/api/alerts"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].alertType").value("HIGH_POWER"));
  }

  @Test
  @WithMockUser(roles = "OPERATOR")
  void resolve_returnsResolvedAlert() throws Exception {
    AlertResponse response =
        new AlertResponse(
            1L,
            1L,
            1L,
            "HIGH_POWER",
            137,
            AlertStatus.RESOLVED,
            LocalDateTime.now(),
            LocalDateTime.now());
    when(alertService.resolve(1L)).thenReturn(response);

    mockMvc
        .perform(patch("/api/alerts/1/resolve"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("RESOLVED"));
  }
}
