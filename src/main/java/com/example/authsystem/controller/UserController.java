package com.example.authsystem.controller;

import com.example.authsystem.dto.PaginatedResponse;
import com.example.authsystem.dto.UserRequest;
import com.example.authsystem.dto.UserResponse;
import com.example.authsystem.entity.User;
import com.example.authsystem.repository.UserRepository;
import com.example.authsystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User management APIs")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Get all users", description = "Get paginated list of all users")
    public ResponseEntity<PaginatedResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<User> userPage = userRepository.findByUsernameContainingAndStatus(username, status, pageable);
        
        PaginatedResponse<UserResponse> response = new PaginatedResponse<>(
                userPage.getContent().stream().map(UserResponse::new).collect(java.util.stream.Collectors.toList()),
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast(),
                userPage.isFirst()
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Get user by ID", description = "Get user details by user ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(new UserResponse(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create user", description = "Create a new user")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            return ResponseEntity.badRequest().build();
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        user.setStatus(userRequest.getStatus());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(new UserResponse(savedUser));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user", description = "Update existing user")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
        return userRepository.findById(id)
                .map(user -> {
                    if (!user.getUsername().equals(userRequest.getUsername()) && userRepository.existsByUsername(userRequest.getUsername())) {
                        return null;
                    }

                    user.setUsername(userRequest.getUsername());
                    user.setEmail(userRequest.getEmail());
                    user.setPhone(userRequest.getPhone());
                    user.setStatus(userRequest.getStatus());
                    
                    if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
                        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
                    }

                    User updatedUser = userRepository.save(user);
                    return new UserResponse(updatedUser);
                }).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Delete user by ID")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        java.util.Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            userRepository.delete(optionalUser.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user status", description = "Enable or disable user")
    public ResponseEntity<UserResponse> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setStatus(status);
                    User updatedUser = userRepository.save(user);
                    return ResponseEntity.ok(new UserResponse(updatedUser));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}