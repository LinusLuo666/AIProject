package com.example.authsystem.video;

import java.time.Instant;

public class VideoTask {
    private final long id;
    private final String requestedFile;
    private final Instant createdAt;
    private Instant updatedAt;
    private String inputFile;
    private String outputFile;
    private VideoTaskStatus status;
    private String errorMessage;

    public VideoTask(long id, String requestedFile) {
        this.id = id;
        this.requestedFile = requestedFile;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.status = VideoTaskStatus.PENDING;
    }

    public long getId() {
        return id;
    }

    public String getRequestedFile() {
        return requestedFile;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void touch() {
        this.updatedAt = Instant.now();
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

    public VideoTaskStatus getStatus() {
        return status;
    }

    public void setStatus(VideoTaskStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
