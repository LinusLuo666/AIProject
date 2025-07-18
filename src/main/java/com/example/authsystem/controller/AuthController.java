package com.example.authsystem.controller;

import com.example.authsystem.dto.JwtAuthenticationResponse;
import com.example.authsystem.dto.LoginRequest;
import com.example.authsystem.entity.Menu;
import com.example.authsystem.entity.User;
import com.example.authsystem.repository.MenuRepository;
import com.example.authsystem.repository.UserRepository;
import com.example.authsystem.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuRepository menuRepository;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<? extends Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }

            List<String> roles = user.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toList());

            List<Menu> userMenus = menuRepository.findByUsername(user.getUsername());
            List<JwtAuthenticationResponse.MenuDTO> menuDTOs = buildMenuTree(userMenus);

            JwtAuthenticationResponse response = new JwtAuthenticationResponse(jwt, user.getUsername(), roles, menuDTOs);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
    }

    private List<JwtAuthenticationResponse.MenuDTO> buildMenuTree(List<Menu> menus) {
        List<JwtAuthenticationResponse.MenuDTO> rootMenus = menus.stream()
                .filter(menu -> menu.getParentId() == 0)
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        rootMenus.forEach(menu -> menu.setChildren(getChildrenMenus(menus, menu.getId())));

        return rootMenus;
    }

    private List<JwtAuthenticationResponse.MenuDTO> getChildrenMenus(List<Menu> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private JwtAuthenticationResponse.MenuDTO convertToDTO(Menu menu) {
        return new JwtAuthenticationResponse.MenuDTO(
                menu.getId(),
                menu.getName(),
                menu.getPath(),
                menu.getComponent(),
                menu.getIcon(),
                menu.getParentId(),
                menu.getSortOrder(),
                menu.getPermission(),
                menu.getMenuType()
        );
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get current authenticated user information")
    public ResponseEntity<User> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElse(null);
        return ResponseEntity.ok(user);
    }
}