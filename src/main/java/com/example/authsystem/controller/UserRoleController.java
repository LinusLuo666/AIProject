package com.example.authsystem.controller;

import com.example.authsystem.dto.UserResponse;
import com.example.authsystem.entity.Role;
import com.example.authsystem.entity.User;
import com.example.authsystem.repository.RoleRepository;
import com.example.authsystem.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/{userId}/roles")
@Tag(name = "User-Role Management", description = "User role assignment APIs")
public class UserRoleController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user roles", description = "Get all roles assigned to a user")
    public ResponseEntity<List<String>> getUserRoles(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    List<String> roles = user.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(roles);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign roles to user", description = "Assign multiple roles to a user")
    public ResponseEntity<UserResponse> assignRolesToUser(
            @PathVariable Long userId,
            @RequestBody List<Long> roleIds) {
        
        return userRepository.findById(userId)
                .map(user -> {
                    List<Role> roles = roleRepository.findAllById(roleIds);
                    user.getRoles().clear();
                    user.getRoles().addAll(roles);
                    User savedUser = userRepository.save(user);
                    return ResponseEntity.ok(new UserResponse(savedUser));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign single role to user", description = "Assign a single role to a user")
    public ResponseEntity<UserResponse> assignRoleToUser(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        
        return userRepository.findById(userId)
                .map(user -> {
                    Role role = roleRepository.findById(roleId).orElse(null);
                    if (role == null) {
                        return ResponseEntity.notFound().build();
                    }
                    
                    user.getRoles().add(role);
                    User savedUser = userRepository.save(user);
                    return ResponseEntity.ok(new UserResponse(savedUser));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove role from user", description = "Remove a role from a user")
    public ResponseEntity<UserResponse> removeRoleFromUser(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        
        return userRepository.findById(userId)
                .map(user -> {
                    Role role = roleRepository.findById(roleId).orElse(null);
                    if (role == null) {
                        return ResponseEntity.notFound().build();
                    }
                    
                    user.getRoles().remove(role);
                    User savedUser = userRepository.save(user);
                    return ResponseEntity.ok(new UserResponse(savedUser));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}