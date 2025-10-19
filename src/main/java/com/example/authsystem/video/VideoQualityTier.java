package com.example.authsystem.video;

import java.util.Locale;
import org.springframework.util.StringUtils;

public enum VideoQualityTier {
    LOW,
    MEDIUM,
    HIGH,
    ULTRA;

    public static VideoQualityTier fromValue(String value) {
        if (!StringUtils.hasText(value)) {
            return MEDIUM;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        for (VideoQualityTier tier : values()) {
            if (tier.name().equals(normalized)) {
                return tier;
            }
        }
        return MEDIUM;
    }
}
