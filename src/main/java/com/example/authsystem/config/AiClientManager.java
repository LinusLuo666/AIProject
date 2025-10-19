package com.example.authsystem.config;

import com.example.authsystem.entity.AiApiCredential;
import com.example.authsystem.repository.AiApiCredentialRepository;
import jakarta.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AiClientManager {

    private static final Logger log = LoggerFactory.getLogger(AiClientManager.class);

    private final AiProviderProperties properties;

    private final AiApiCredentialRepository credentialRepository;

    private final AtomicReference<ChatClient> chatClientRef = new AtomicReference<>();

    public AiClientManager(AiProviderProperties properties,
            AiApiCredentialRepository credentialRepository) {
        this.properties = properties;
        this.credentialRepository = credentialRepository;
    }

    @PostConstruct
    public void initialize() {
        refresh();
    }

    public ChatClient currentClient() {
        ChatClient client = chatClientRef.get();
        if (client == null) {
            refresh();
            client = chatClientRef.get();
        }
        return client;
    }

    public synchronized void refresh() {
        String apiKey = resolveApiKey();
        if (!StringUtils.hasText(apiKey)) {
            chatClientRef.set(null);
            log.warn("AI API key is not configured; ChatClient is disabled until a key is provided.");
            return;
        }
        try {
            OpenAiApi api = new OpenAiApi(apiKey);
            OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .withModel(properties.getModel())
                    .build();
            OpenAiChatModel model = new OpenAiChatModel(api, options);
            chatClientRef.set(ChatClient.create(model));
            log.info("ChatClient refreshed for provider {} using model {}", properties.getProvider(), properties.getModel());
        } catch (Exception ex) {
            chatClientRef.set(null);
            log.error("Failed to initialize ChatClient: {}", ex.getMessage(), ex);
        }
    }

    private String resolveApiKey() {
        String provider = StringUtils.hasText(properties.getProvider()) ? properties.getProvider() : "openai";
        Optional<AiApiCredential> credential = credentialRepository.findByProvider(provider);
        if (credential.isPresent()) {
            return credential.get().decodedKey();
        }
        return properties.getApiKey();
    }
}
