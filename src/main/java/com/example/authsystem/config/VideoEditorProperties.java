package com.example.authsystem.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "video-editor")
public class VideoEditorProperties {

    @NotNull
    private Path workspacePath;

    @NotEmpty
    private List<String> allowedExtensions = new ArrayList<>();

    @NotBlank
    private String outputPrefix;

    public Path getWorkspacePath() {
        return workspacePath;
    }

    public void setWorkspacePath(Path workspacePath) {
        this.workspacePath = workspacePath;
    }

    public List<String> getAllowedExtensions() {
        return allowedExtensions;
    }

    public void setAllowedExtensions(List<String> allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
    }

    public String getOutputPrefix() {
        return outputPrefix;
    }

    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }
}
