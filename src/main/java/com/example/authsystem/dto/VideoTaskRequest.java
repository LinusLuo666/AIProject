package com.example.authsystem.dto;

import jakarta.validation.constraints.NotBlank;

public class VideoTaskRequest {

    @NotBlank
    private String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
