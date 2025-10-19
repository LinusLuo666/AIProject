package com.example.authsystem.dto;

import com.example.authsystem.entity.VideoTask;
import com.example.authsystem.entity.VideoTaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class VideoTaskResponse {

    private Long id;
    private String sourceFileName;
    private String qualityProfile;
    private VideoTaskStatus status;
    private String commandPreview;
    private String lastMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<VideoSegmentDTO> segments;

    public VideoTaskResponse(VideoTask task) {
        this.id = task.getId();
        this.sourceFileName = task.getSourceFileName();
        this.qualityProfile = task.getQualityProfile();
        this.status = task.getStatus();
        this.commandPreview = task.getCommandPreview();
        this.lastMessage = task.getLastMessage();
        this.createdAt = task.getCreatedAt();
        this.updatedAt = task.getUpdatedAt();
        this.segments = task.getSegments().stream()
                .map(VideoSegmentDTO::new)
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public String getQualityProfile() {
        return qualityProfile;
    }

    public VideoTaskStatus getStatus() {
        return status;
    }

    public String getCommandPreview() {
        return commandPreview;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<VideoSegmentDTO> getSegments() {
        return segments;
    }
}
