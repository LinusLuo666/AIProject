package com.example.authsystem.dto;

import jakarta.validation.constraints.NotBlank;

public record VideoQualityProfile(@NotBlank String resolution, @NotBlank String bitrate) {
}
