package com.example.authsystem.dto;

import com.example.authsystem.dto.video.VideoSegmentDraft;
import com.example.authsystem.video.VideoQualityTier;
import com.example.authsystem.video.VideoTaskStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VideoTaskResponse {

    private Long id;

    private String fileName;

    private VideoQualityTier qualityTier;

    private VideoTaskStatus status;

    private List<VideoSegmentDraft> segments = new ArrayList<>();

    private String originalInstruction;

    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public VideoQualityTier getQualityTier() {
        return qualityTier;
    }

    public void setQualityTier(VideoQualityTier qualityTier) {
        this.qualityTier = qualityTier;
    }

    public VideoTaskStatus getStatus() {
        return status;
    }

    public void setStatus(VideoTaskStatus status) {
        this.status = status;
    }

    public List<VideoSegmentDraft> getSegments() {
        return segments;
    }

    public void setSegments(List<VideoSegmentDraft> segments) {
        this.segments = segments;
    }

    public String getOriginalInstruction() {
        return originalInstruction;
    }

    public void setOriginalInstruction(String originalInstruction) {
        this.originalInstruction = originalInstruction;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
