package com.energyplatform.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private UserRepository userRepository;

  @MockitoBean private PasswordEncoder passwordEncoder;

  @MockitoBean private AuthenticationManager authenticationManager;

  @MockitoBean private JwtService jwtService;

  @MockitoBean private AppUserDetailsService appUserDetailsService;

  @Test
  void register_returnsCreated() throws Exception {
    when(userRepository.existsByUsername("alice")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("hashed");
    RegisterRequest request = new RegisterRequest("alice", "password123", Role.OPERATOR);

    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
  }

  @Test
  void register_returnsConflictWhenUsernameTaken() throws Exception {
    when(userRepository.existsByUsername("alice")).thenReturn(true);
    RegisterRequest request = new RegisterRequest("alice", "password123", Role.OPERATOR);

    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @Test
  void login_returnsToken() throws Exception {
    User user = new User("alice", "hashed", Role.OPERATOR);
    when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
    when(jwtService.generateToken("alice", Role.OPERATOR)).thenReturn("token123");
    LoginRequest request = new LoginRequest("alice", "password123");

    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("token123"));
  }

  @Test
  void login_withBadCredentials_returnsUnauthorized() throws Exception {
    when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));
    LoginRequest request = new LoginRequest("alice", "wrong-password");

    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }
}
