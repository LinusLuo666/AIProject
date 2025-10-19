package com.example.authsystem.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public class VideoTaskRequest {

    @NotBlank
    private String sourceFileName;

    private String qualityProfile;

    @Valid
    private List<VideoSegmentDTO> segments = new ArrayList<>();

    private String commandPreview;

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public String getQualityProfile() {
        return qualityProfile;
    }

    public void setQualityProfile(String qualityProfile) {
        this.qualityProfile = qualityProfile;
    }

    public List<VideoSegmentDTO> getSegments() {
        return segments;
    }

    public void setSegments(List<VideoSegmentDTO> segments) {
        this.segments = segments != null ? segments : new ArrayList<>();
    }

    public String getCommandPreview() {
        return commandPreview;
    }

    public void setCommandPreview(String commandPreview) {
        this.commandPreview = commandPreview;
    }
}
