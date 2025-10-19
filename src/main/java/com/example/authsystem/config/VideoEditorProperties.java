package com.example.authsystem.config;

import com.example.authsystem.dto.VideoQualityProfile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.LinkedHashMap;
import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "video-editor")
public class VideoEditorProperties {

    @NotBlank
    private String ffmpegPath;

    @NotBlank
    private String workspacePath;

    @NotBlank
    private String outputPrefix;

    @NotEmpty
    private Map<String, @Valid VideoQualityProfile> qualityProfiles = new LinkedHashMap<>();

    public String getFfmpegPath() {
        return ffmpegPath;
    }

    public void setFfmpegPath(String ffmpegPath) {
        this.ffmpegPath = ffmpegPath;
    }

    public String getWorkspacePath() {
        return workspacePath;
    }

    public void setWorkspacePath(String workspacePath) {
        this.workspacePath = workspacePath;
    }

    public String getOutputPrefix() {
        return outputPrefix;
    }

    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    public Map<String, VideoQualityProfile> getQualityProfiles() {
        return Map.copyOf(qualityProfiles);
    }

    public void setQualityProfiles(Map<String, VideoQualityProfile> qualityProfiles) {
        if (qualityProfiles == null) {
            this.qualityProfiles = new LinkedHashMap<>();
        } else {
            this.qualityProfiles = new LinkedHashMap<>(qualityProfiles);
        }
    }
}
