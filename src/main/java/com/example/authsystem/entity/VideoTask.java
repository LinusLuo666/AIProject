package com.example.authsystem.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "video_tasks")
public class VideoTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_file_name", nullable = false)
    private String sourceFileName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "video_task_segments", joinColumns = @JoinColumn(name = "video_task_id"))
    @OrderColumn(name = "segment_index")
    private List<VideoSegment> segments = new ArrayList<>();

    @Column(name = "quality_profile")
    private String qualityProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VideoTaskStatus status = VideoTaskStatus.PENDING;

    @Column(name = "command_preview", length = 1000)
    private String commandPreview;

    @Column(name = "last_message", length = 500)
    private String lastMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public VideoTask() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public List<VideoSegment> getSegments() {
        return segments;
    }

    public void setSegments(List<VideoSegment> segments) {
        this.segments = segments != null ? new ArrayList<>(segments) : new ArrayList<>();
    }

    public String getQualityProfile() {
        return qualityProfile;
    }

    public void setQualityProfile(String qualityProfile) {
        this.qualityProfile = qualityProfile;
    }

    public VideoTaskStatus getStatus() {
        return status;
    }

    public void setStatus(VideoTaskStatus status) {
        this.status = status;
    }

    public String getCommandPreview() {
        return commandPreview;
    }

    public void setCommandPreview(String commandPreview) {
        this.commandPreview = commandPreview;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
