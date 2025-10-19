package com.example.authsystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "ai")
public class AiProviderProperties {

    private String provider = "openai";

    private String apiKey;

    private String model = "gpt-3.5-turbo";

    private String baseUrl;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        if (StringUtils.hasText(provider)) {
            this.provider = provider;
        }
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        if (StringUtils.hasText(model)) {
            this.model = model;
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
