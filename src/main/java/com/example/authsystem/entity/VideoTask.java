package com.example.authsystem.entity;

import com.example.authsystem.video.VideoQualityTier;
import com.example.authsystem.video.VideoTaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "video_tasks")
public class VideoTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "original_instruction", nullable = false)
    private String originalInstruction;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "quality_tier", nullable = false)
    private VideoQualityTier qualityTier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VideoTaskStatus status = VideoTaskStatus.PENDING_APPROVAL;

    @Lob
    @Column(name = "segments_json", nullable = false)
    private String segmentsJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public VideoTask() {
    }

    public VideoTask(String originalInstruction, String fileName, VideoQualityTier qualityTier, String segmentsJson) {
        this.originalInstruction = originalInstruction;
        this.fileName = fileName;
        this.qualityTier = qualityTier;
        this.segmentsJson = segmentsJson;
        this.status = VideoTaskStatus.PENDING_APPROVAL;
    }

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getOriginalInstruction() {
        return originalInstruction;
    }

    public void setOriginalInstruction(String originalInstruction) {
        this.originalInstruction = originalInstruction;
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

    public String getSegmentsJson() {
        return segmentsJson;
    }

    public void setSegmentsJson(String segmentsJson) {
        this.segmentsJson = segmentsJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
