package com.example.authsystem.controller;

import com.example.authsystem.dto.PaginatedResponse;
import com.example.authsystem.dto.RoleRequest;
import com.example.authsystem.dto.RoleResponse;
import com.example.authsystem.entity.Role;
import com.example.authsystem.repository.RoleRepository;
import com.example.authsystem.service.RoleService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Role Management", description = "Role management APIs")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all roles", description = "Get paginated list of all roles")
    public ResponseEntity<PaginatedResponse<RoleResponse>> getAllRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<Role> rolePage = roleRepository.findByNameContainingAndStatus(name, status, pageable);
        
        PaginatedResponse<RoleResponse> response = new PaginatedResponse<>(
                rolePage.getContent().stream().map(RoleResponse::new).collect(java.util.stream.Collectors.toList()),
                rolePage.getNumber(),
                rolePage.getSize(),
                rolePage.getTotalElements(),
                rolePage.getTotalPages(),
                rolePage.isLast(),
                rolePage.isFirst()
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get role by ID", description = "Get role details by role ID")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable Long id) {
        return roleRepository.findById(id)
                .map(role -> ResponseEntity.ok(new RoleResponse(role)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create role", description = "Create a new role")
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleRequest roleRequest) {
        if (roleRepository.existsByName(roleRequest.getName())) {
            return ResponseEntity.badRequest().body(null);
        }

        Role role = new Role();
        role.setName(roleRequest.getName());
        role.setDescription(roleRequest.getDescription());
        role.setStatus(roleRequest.getStatus());

        Role savedRole = roleRepository.save(role);
        return ResponseEntity.ok(new RoleResponse(savedRole));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update role", description = "Update existing role")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequest roleRequest) {
        return roleRepository.findById(id)
                .map(role -> {
                    if (!role.getName().equals(roleRequest.getName()) && roleRepository.existsByName(roleRequest.getName())) {
                        return ResponseEntity.badRequest().body(null);
                    }

                    role.setName(roleRequest.getName());
                    role.setDescription(roleRequest.getDescription());
                    role.setStatus(roleRequest.getStatus());

                    Role updatedRole = roleRepository.save(role);
                    return ResponseEntity.ok(new RoleResponse(updatedRole));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete role", description = "Delete role by ID")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        return roleRepository.findById(id)
                .map(role -> {
                    roleRepository.delete(role);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Get active roles", description = "Get all active roles")
    public ResponseEntity<List<RoleResponse>> getActiveRoles() {
        List<Role> roles = roleRepository.findByStatus(1);
        List<RoleResponse> roleResponses = roles.stream()
                .map(RoleResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roleResponses);
    }
}