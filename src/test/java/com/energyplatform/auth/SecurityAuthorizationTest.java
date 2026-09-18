package com.energyplatform.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.energyplatform.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * Exercises the real authorization matrix end to end — real Spring Security filter chain, real
 * JWTs, real roles — rather than the mocked-out security beans used in the @WebMvcTest slices.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Import(TestcontainersConfiguration.class)
class SecurityAuthorizationTest {

  @Autowired private TestRestTemplate restTemplate;

  private static final String DEVICE_BODY =
      "{\"name\":\"Compressor A1\",\"type\":\"COMPRESSOR\",\"location\":\"Building 3\",\"status\":\"ONLINE\"}";

  @Test
  void unauthenticatedRequest_isRejected() {
    var response =
        restTemplate.postForEntity(
            "/api/devices", new HttpEntity<>(DEVICE_BODY, jsonHeaders()), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void operatorRole_cannotCreateDevice() {
    register("operator1", "password123", Role.OPERATOR);
    String token = login("operator1", "password123");

    var response =
        restTemplate.postForEntity(
            "/api/devices", new HttpEntity<>(DEVICE_BODY, authHeaders(token)), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void adminRole_canCreateDevice() {
    register("admin1", "password123", Role.ADMIN);
    String token = login("admin1", "password123");

    var response =
        restTemplate.postForEntity(
            "/api/devices", new HttpEntity<>(DEVICE_BODY, authHeaders(token)), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  private void register(String username, String password, Role role) {
    String body =
        "{\"username\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}"
            .formatted(username, password, role);
    restTemplate.postForEntity(
        "/api/auth/register", new HttpEntity<>(body, jsonHeaders()), Void.class);
  }

  private String login(String username, String password) {
    String body = "{\"username\":\"%s\",\"password\":\"%s\"}".formatted(username, password);
    var response =
        restTemplate.postForEntity(
            "/api/auth/login", new HttpEntity<>(body, jsonHeaders()), AuthResponse.class);
    return response.getBody().token();
  }

  private HttpHeaders jsonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  private HttpHeaders authHeaders(String token) {
    HttpHeaders headers = jsonHeaders();
    headers.setBearerAuth(token);
    return headers;
  }
}
