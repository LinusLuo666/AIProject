package com.example.authsystem.dto;

import jakarta.validation.constraints.NotBlank;

public class AiApiKeyValidationRequest {

    private String provider;

    @NotBlank(message = "API key must not be empty")
    private String apiKey;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
