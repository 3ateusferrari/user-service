package com.mateusferrari.userservice.service;

import com.mateusferrari.userservice.dto.UserCreateRequest;
import com.mateusferrari.userservice.dto.UserResponse;
import com.mateusferrari.userservice.mapper.UserMapper;
import com.mateusferrari.userservice.model.User;
import com.mateusferrari.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void testCreateUser() {
        UserCreateRequest userRequest = new UserCreateRequest("Test User", "test@test.com", "password");
        User user = new User(1L, "Test User", "test@test.com", "encodedPassword", false);
        UserResponse expectedResponse = new UserResponse(1L, "Test User", "test@test.com");

        when(userMapper.toEntity(any(UserCreateRequest.class))).thenReturn(user);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(any(User.class))).thenReturn(expectedResponse);

        UserResponse userResponse = userService.createUser(userRequest);

        assertEquals(expectedResponse.getId(), userResponse.getId());
        assertEquals(expectedResponse.getName(), userResponse.getName());
        assertEquals(expectedResponse.getEmail(), userResponse.getEmail());
    }
}
