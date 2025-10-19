package com.example.authsystem.dto;

import com.example.authsystem.entity.VideoTaskStatus;
import jakarta.validation.constraints.NotNull;

public class VideoTaskStatusUpdateRequest {

    @NotNull
    private VideoTaskStatus status;

    private String message;

    public VideoTaskStatus getStatus() {
        return status;
    }

    public void setStatus(VideoTaskStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
