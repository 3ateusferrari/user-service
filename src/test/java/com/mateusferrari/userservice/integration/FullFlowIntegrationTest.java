package com.mateusferrari.userservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mateusferrari.userservice.dto.*;
import com.mateusferrari.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FullFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testCompleteUserLifecycle() throws Exception {
        // 1. Register a new user
        UserCreateRequest registerRequest = new UserCreateRequest("Integration Test", "integration@test.com", "password123");
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Integration Test")))
                .andExpect(jsonPath("$.email", is("integration@test.com")));

        // 2. Login to get tokens
        LoginRequest loginRequest = new LoginRequest("integration@test.com", "password123");
        
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", notNullValue()))
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        JwtAuthenticationResponse authResponse = objectMapper.readValue(responseBody, JwtAuthenticationResponse.class);
        String accessToken = authResponse.getAccessToken();
        String refreshToken = authResponse.getRefreshToken();

        // 3. Access protected endpoint with JWT
        MvcResult listResult = mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].email", is("integration@test.com")))
                .andReturn();
        
        // Extract user ID from the list
        Long userId = userRepository.findByEmail("integration@test.com").get().getId();

        // 4. Update user
        UserUpdateRequest updateRequest = new UserUpdateRequest("Updated Name", "integration@test.com", "newpassword123");
        
        mockMvc.perform(put("/api/users/" + userId)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")));

        // 5. Use Refresh Token to get a new Access Token
        TokenRefreshRequest refreshRequest = new TokenRefreshRequest();
        refreshRequest.setRefreshToken(refreshToken);
        
        mockMvc.perform(post("/api/auth/refreshtoken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", is(refreshToken)));

        // 6. Test Soft Delete
        mockMvc.perform(delete("/api/users/" + userId)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());

        // 7. Verify login fails for deleted user
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());

        // 8. Register and login as a second user to verify 404 for the deleted user
        UserCreateRequest secondUserRequest = new UserCreateRequest("Second User", "second@test.com", "password123");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondUserRequest)))
                .andExpect(status().isOk());

        MvcResult secondLoginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("second@test.com", "password123"))))
                .andExpect(status().isOk())
                .andReturn();
        
        String secondAccessToken = objectMapper.readValue(secondLoginResult.getResponse().getContentAsString(), JwtAuthenticationResponse.class).getAccessToken();

        // 9. Verify first user is logically deleted (404 for other authenticated users)
        mockMvc.perform(get("/api/users/" + userId)
                .header("Authorization", "Bearer " + secondAccessToken))
                .andExpect(status().isNotFound());

        // 10. Test Actuator Endpoints (Public)
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));

        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk());
    }
}
