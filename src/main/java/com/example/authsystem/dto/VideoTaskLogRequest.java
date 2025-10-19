package com.example.authsystem.dto;

import com.example.authsystem.entity.VideoTaskStatus;

public class VideoTaskLogRequest {

    private String message;
    private VideoTaskStatus status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public VideoTaskStatus getStatus() {
        return status;
    }

    public void setStatus(VideoTaskStatus status) {
        this.status = status;
    }
}
