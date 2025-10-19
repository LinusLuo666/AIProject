package com.example.authsystem.controller;

import com.example.authsystem.dto.AiApiKeyRequest;
import com.example.authsystem.dto.AiApiKeyValidationRequest;
import com.example.authsystem.dto.ApiKeyValidationResponse;
import com.example.authsystem.service.AiApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI Provider", description = "AI provider configuration APIs")
public class AiProviderController {

    private final AiApiKeyService aiApiKeyService;

    public AiProviderController(AiApiKeyService aiApiKeyService) {
        this.aiApiKeyService = aiApiKeyService;
    }

    @PostMapping("/api-key")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update AI API key", description = "Persist a new API key for the configured AI provider and refresh the runtime client.")
    public ResponseEntity<Void> updateApiKey(@Valid @RequestBody AiApiKeyRequest request) {
        aiApiKeyService.updateApiKey(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api-key/validate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Validate AI API key", description = "Check whether the provided API key matches the currently stored secret.")
    public ResponseEntity<ApiKeyValidationResponse> validateApiKey(@Valid @RequestBody AiApiKeyValidationRequest request) {
        boolean valid = aiApiKeyService.validate(request);
        return ResponseEntity.ok(new ApiKeyValidationResponse(valid));
    }
}
