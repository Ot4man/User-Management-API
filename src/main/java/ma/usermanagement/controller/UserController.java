package ma.usermanagement.controller;

import jakarta.validation.Valid;
import ma.usermanagement.dto.PasswordUpdateRequest;
import ma.usermanagement.dto.UserCreateRequest;
import ma.usermanagement.dto.UserResponse;
import ma.usermanagement.dto.UserUpdateRequest;
import ma.usermanagement.exception.ForbiddenException;
import ma.usermanagement.exception.UnauthorizedException;
import ma.usermanagement.model.Role;
import ma.usermanagement.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // USER Endpoints (/api/users/me)

    @GetMapping("/me")
    public UserResponse getMyProfile(@RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        checkAuthenticated(currentUserId);
        return userService.getMyProfile(currentUserId);
    }

    @PutMapping("/me")
    public UserResponse updateMyProfile(@RequestHeader(value = "X-User-Id", required = false) Long currentUserId,
                                        @Valid @RequestBody UserUpdateRequest request) {
        checkAuthenticated(currentUserId);
        return userService.updateMyProfile(currentUserId, request);
    }

    @PatchMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeMyPassword(@RequestHeader(value = "X-User-Id", required = false) Long currentUserId,
                                 @Valid @RequestBody PasswordUpdateRequest request) {
        checkAuthenticated(currentUserId);
        userService.changeMyPassword(currentUserId, request);
    }

    // ADMIN Endpoints (/api/users)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestHeader(value = "X-User-Role", defaultValue = "USER") Role role,
                                   @Valid @RequestBody UserCreateRequest request) {
        checkAdmin(role);
        return userService.createUser(request);
    }

    @GetMapping
    public List<UserResponse> getAllUsers(@RequestHeader(value = "X-User-Role", defaultValue = "USER") Role role) {
        checkAdmin(role);
        return userService.getAllUsers();
    }

    @GetMapping("/{id:[0-9]+}")
    public UserResponse getUserById(@RequestHeader(value = "X-User-Role", defaultValue = "USER") Role role,
                                    @PathVariable("id") Long id) {
        checkAdmin(role);
        return userService.getUserById(id);
    }

    @PutMapping("/{id:[0-9]+}")
    public UserResponse updateUser(@RequestHeader(value = "X-User-Role", defaultValue = "USER") Role role,
                                   @PathVariable("id") Long id,
                                   @Valid @RequestBody UserUpdateRequest request) {
        checkAdmin(role);
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id:[0-9]+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateUser(@RequestHeader(value = "X-User-Role", defaultValue = "USER") Role role,
                               @PathVariable("id") Long id) {
        checkAdmin(role);
        userService.deactivateUser(id);
    }

    // Security Helper Methods

    private void checkAdmin(Role role) {
        if (role == null || role != Role.ADMIN) {
            throw new ForbiddenException("Access denied: ADMIN role required");
        }
    }

    private void checkAuthenticated(Long currentUserId) {
        if (currentUserId == null) {
            throw new UnauthorizedException("Authentication required: X-User-Id header is missing");
        }
    }
}
