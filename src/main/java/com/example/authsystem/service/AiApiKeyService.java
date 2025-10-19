package com.example.authsystem.service;

import com.example.authsystem.config.AiClientManager;
import com.example.authsystem.config.AiProviderProperties;
import com.example.authsystem.dto.AiApiKeyRequest;
import com.example.authsystem.dto.AiApiKeyValidationRequest;
import com.example.authsystem.entity.AiApiCredential;
import com.example.authsystem.repository.AiApiCredentialRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AiApiKeyService {

    private final AiApiCredentialRepository credentialRepository;
    private final AiProviderProperties properties;
    private final AiClientManager aiClientManager;

    public AiApiKeyService(AiApiCredentialRepository credentialRepository,
            AiProviderProperties properties,
            AiClientManager aiClientManager) {
        this.credentialRepository = credentialRepository;
        this.properties = properties;
        this.aiClientManager = aiClientManager;
    }

    public Optional<String> currentApiKey() {
        String provider = defaultProvider();
        return credentialRepository.findByProvider(provider)
                .map(AiApiCredential::decodedKey)
                .filter(StringUtils::hasText)
                .or(() -> Optional.ofNullable(properties.getApiKey()).filter(StringUtils::hasText));
    }

    @Transactional
    public void updateApiKey(AiApiKeyRequest request) {
        String provider = resolveProvider(request.getProvider());
        String encoded = AiApiCredential.encode(request.getApiKey());
        AiApiCredential credential = credentialRepository.findByProvider(provider)
                .orElse(new AiApiCredential(provider, encoded));
        credential.setEncodedKey(encoded);
        credentialRepository.save(credential);
        properties.setApiKey(request.getApiKey());
        aiClientManager.refresh();
    }

    public boolean validate(AiApiKeyValidationRequest request) {
        String provider = resolveProvider(request.getProvider());
        return credentialRepository.findByProvider(provider)
                .map(AiApiCredential::decodedKey)
                .map(stored -> stored.equals(request.getApiKey()))
                .orElseGet(() -> request.getApiKey().equals(properties.getApiKey()));
    }

    private String resolveProvider(String provider) {
        return StringUtils.hasText(provider) ? provider : defaultProvider();
    }

    private String defaultProvider() {
        return StringUtils.hasText(properties.getProvider()) ? properties.getProvider() : "openai";
    }
}
