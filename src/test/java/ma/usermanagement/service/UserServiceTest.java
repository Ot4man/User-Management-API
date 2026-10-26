package ma.usermanagement.service;

import ma.usermanagement.dto.PasswordUpdateRequest;
import ma.usermanagement.dto.UserCreateRequest;
import ma.usermanagement.dto.UserResponse;
import ma.usermanagement.dto.UserUpdateRequest;
import ma.usermanagement.exception.BadRequestException;
import ma.usermanagement.exception.EmailAlreadyExistsException;
import ma.usermanagement.exception.UserNotFoundException;
import ma.usermanagement.mapper.UserMapper;
import ma.usermanagement.model.Role;
import ma.usermanagement.model.User;
import ma.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;
    private UserResponse sampleResponse;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setName("Otman");
        sampleUser.setEmail("otman@gmail.com");
        sampleUser.setPassword("$2a$10$abcdefghijklmnopqrstuvwxyz123456");
        sampleUser.setRole(Role.ADMIN);
        sampleUser.setActive(true);

        sampleResponse = new UserResponse();
        sampleResponse.setId(1L);
        sampleResponse.setName("Otman");
        sampleResponse.setEmail("otman@gmail.com");
        sampleResponse.setRole(Role.ADMIN);
        sampleResponse.setActive(true);
        sampleResponse.setCreatedAt(LocalDateTime.now());
        sampleResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createUser_Success() {
        UserCreateRequest request = new UserCreateRequest();
        request.setName("Otman");
        request.setEmail("otman@gmail.com");
        request.setPassword("OtmanPass2026!");
        request.setRole(Role.ADMIN);

        when(userRepository.existsByEmail("otman@gmail.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(sampleUser);
        when(passwordEncoder.encode("OtmanPass2026!")).thenReturn("$2a$10$abcdefghijklmnopqrstuvwxyz123456");
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);
        when(userMapper.toResponse(sampleUser)).thenReturn(sampleResponse);

        UserResponse result = userService.createUser(request);

        assertNotNull(result);
        assertEquals("otman@gmail.com", result.getEmail());
        verify(userRepository, times(1)).save(sampleUser);
        verify(passwordEncoder, times(1)).encode("OtmanPass2026!");
    }

    @Test
    void createUser_EmailAlreadyExists_ThrowsException() {
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("otman@gmail.com");

        when(userRepository.existsByEmail("otman@gmail.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getAllUsers_ReturnsList() {
        List<User> users = Arrays.asList(sampleUser);
        List<UserResponse> responses = Arrays.asList(sampleResponse);

        when(userRepository.findAllByActiveTrue()).thenReturn(users);
        when(userMapper.toResponseList(users)).thenReturn(responses);

        List<UserResponse> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("Otman", result.get(0).getName());
        verify(userRepository, times(1)).findAllByActiveTrue();
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(sampleUser));
        when(userMapper.toResponse(sampleUser)).thenReturn(sampleResponse);

        UserResponse result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("otman@gmail.com", result.getEmail());
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findByIdAndActiveTrue(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void updateUser_Success() {
        UserUpdateRequest updateRequest = new UserUpdateRequest("Otman Benani", "otman.pro@gmail.com");

        when(userRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.existsByEmailAndIdNot("otman.pro@gmail.com", 1L)).thenReturn(false);
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);
        when(userMapper.toResponse(sampleUser)).thenReturn(sampleResponse);

        UserResponse result = userService.updateUser(1L, updateRequest);

        assertNotNull(result);
        verify(userMapper, times(1)).updateEntity(sampleUser, updateRequest);
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    void updateUser_EmailAlreadyInUse_ThrowsException() {
        UserUpdateRequest updateRequest = new UserUpdateRequest("Otman Benani", "karim@gmail.com");

        when(userRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.existsByEmailAndIdNot("karim@gmail.com", 1L)).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.updateUser(1L, updateRequest));
        verify(userRepository, never()).save(sampleUser);
    }

    @Test
    void deactivateUser_Success() {
        when(userRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(sampleUser));

        userService.deactivateUser(1L);

        assertFalse(sampleUser.isActive());
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    void changeMyPassword_Success() {
        PasswordUpdateRequest request = new PasswordUpdateRequest("AncienPass123!", "NouveauPass2026!");

        when(userRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("AncienPass123!", "$2a$10$abcdefghijklmnopqrstuvwxyz123456")).thenReturn(true);
        when(passwordEncoder.encode("NouveauPass2026!")).thenReturn("$2a$10$nouvelHashEncodedPassword2026");

        userService.changeMyPassword(1L, request);

        assertEquals("$2a$10$nouvelHashEncodedPassword2026", sampleUser.getPassword());
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    void changeMyPassword_WrongPassword_ThrowsException() {
        PasswordUpdateRequest request = new PasswordUpdateRequest("FauxMotDePasse123!", "NouveauPass2026!");

        when(userRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("FauxMotDePasse123!", "$2a$10$abcdefghijklmnopqrstuvwxyz123456")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> userService.changeMyPassword(1L, request));
        verify(userRepository, never()).save(sampleUser);
    }
}
