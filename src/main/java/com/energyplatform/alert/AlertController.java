package com.energyplatform.alert;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alerts")
@Tag(name = "Alerts")
public class AlertController {

  private final AlertService alertService;

  public AlertController(AlertService alertService) {
    this.alertService = alertService;
  }

  @PostMapping("/rules")
  public ResponseEntity<AlertRuleResponse> createRule(
      @Valid @RequestBody AlertRuleRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(alertService.createRule(request));
  }

  @GetMapping("/rules")
  public Page<AlertRuleResponse> findRules(Pageable pageable) {
    return alertService.findRules(pageable);
  }

  @GetMapping
  public Page<AlertResponse> findAlerts(Pageable pageable) {
    return alertService.findAlerts(pageable);
  }

  @PatchMapping("/{id}/resolve")
  public AlertResponse resolve(@PathVariable Long id) {
    return alertService.resolve(id);
  }
}
