package com.example.authsystem.controller;

import com.example.authsystem.config.VideoEditorProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/video-editor")
public class VideoEditorConfigController {

    private final VideoEditorProperties properties;

    public VideoEditorConfigController(VideoEditorProperties properties) {
        this.properties = properties;
    }

    @GetMapping("/config")
    public VideoEditorProperties getConfiguration() {
        return properties;
    }
}
