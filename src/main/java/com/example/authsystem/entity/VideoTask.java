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

    @Column(name = "input_file", nullable = false)
    private String inputFile;

    @Column(name = "output_file", nullable = false)
    private String outputFile;

    @Column(name = "video_bitrate")
    private String videoBitrate;

    @Column(name = "scale_width")
    private Integer scaleWidth;

    @Column(name = "scale_height")
    private Integer scaleHeight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VideoTaskStatus status = VideoTaskStatus.PENDING_APPROVAL;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "video_task_segments", joinColumns = @JoinColumn(name = "task_id"))
    private List<VideoSegment> segments = new ArrayList<>();

    @Column(name = "execution_log", length = 4000)
    private String executionLog;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getVideoBitrate() {
        return videoBitrate;
    }

    public void setVideoBitrate(String videoBitrate) {
        this.videoBitrate = videoBitrate;
    }

    public Integer getScaleWidth() {
        return scaleWidth;
    }

    public void setScaleWidth(Integer scaleWidth) {
        this.scaleWidth = scaleWidth;
    }

    public Integer getScaleHeight() {
        return scaleHeight;
    }

    public void setScaleHeight(Integer scaleHeight) {
        this.scaleHeight = scaleHeight;
    }

    public VideoTaskStatus getStatus() {
        return status;
    }

    public void setStatus(VideoTaskStatus status) {
        this.status = status;
    }

    public List<VideoSegment> getSegments() {
        return segments;
    }

    public void setSegments(List<VideoSegment> segments) {
        this.segments = segments;
    }

    public String getExecutionLog() {
        return executionLog;
    }

    public void setExecutionLog(String executionLog) {
        this.executionLog = executionLog;
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

    public void appendLog(String message) {
        if (message == null || message.isBlank()) {
            return;
        }
        if (executionLog == null || executionLog.isBlank()) {
            executionLog = message;
        } else {
            executionLog = executionLog + System.lineSeparator() + message;
        }
    }
}
