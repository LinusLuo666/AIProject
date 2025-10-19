package com.example.authsystem.controller;

import com.example.authsystem.dto.CommandPreviewResponse;
import com.example.authsystem.service.VideoTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/video-tasks")
@Tag(name = "Video Task", description = "Video editing task APIs")
public class VideoTaskController {

    private final VideoTaskService videoTaskService;

    public VideoTaskController(VideoTaskService videoTaskService) {
        this.videoTaskService = videoTaskService;
    }

    @GetMapping("/{id}/command-preview")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get FFmpeg command preview", description = "Preview the FFmpeg command for an approved task")
    public ResponseEntity<CommandPreviewResponse> getCommandPreview(@PathVariable("id") Long id) {
        String command = videoTaskService.getCommandPreview(id);
        return ResponseEntity.ok(new CommandPreviewResponse(command));
    }

    @PostMapping("/{id}/execute")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Confirm execution", description = "Trigger FFmpeg execution for an approved task")
    public ResponseEntity<Void> confirmExecution(@PathVariable("id") Long id) {
        videoTaskService.confirmExecution(id);
        return ResponseEntity.accepted().build();
    }
}
