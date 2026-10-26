package ma.usermanagement.service;

import ma.usermanagement.dto.PasswordUpdateRequest;
import ma.usermanagement.dto.UserCreateRequest;
import ma.usermanagement.dto.UserResponse;
import ma.usermanagement.dto.UserUpdateRequest;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    void deactivateUser(Long id);

    UserResponse getMyProfile(Long currentUserId);

    UserResponse updateMyProfile(Long currentUserId, UserUpdateRequest request);

    void changeMyPassword(Long currentUserId, PasswordUpdateRequest request);
}
