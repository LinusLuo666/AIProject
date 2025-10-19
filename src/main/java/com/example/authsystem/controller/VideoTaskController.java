package com.example.authsystem.controller;

import com.example.authsystem.dto.VideoInstructionRequest;
import com.example.authsystem.dto.VideoTaskResponse;
import com.example.authsystem.dto.video.VideoTaskDraft;
import com.example.authsystem.entity.VideoTask;
import com.example.authsystem.service.VideoInstructionParser;
import com.example.authsystem.service.VideoTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/video-tasks")
@Tag(name = "Video Tasks", description = "Video task drafting and workflow APIs")
public class VideoTaskController {

    private final VideoInstructionParser videoInstructionParser;
    private final VideoTaskService videoTaskService;

    public VideoTaskController(VideoInstructionParser videoInstructionParser, VideoTaskService videoTaskService) {
        this.videoInstructionParser = videoInstructionParser;
        this.videoTaskService = videoTaskService;
    }

    @PostMapping("/from-instruction")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a draft video task", description = "Convert free text instructions into a structured video editing task and store it for approval.")
    public ResponseEntity<VideoTaskResponse> createTaskFromInstruction(@Valid @RequestBody VideoInstructionRequest request) {
        VideoTaskDraft draft = videoInstructionParser.parse(request.getInstruction());
        VideoTask task = videoTaskService.createPendingTask(request.getInstruction(), draft);
        VideoTaskResponse response = videoTaskService.toResponse(task);
        return ResponseEntity.ok(response);
    }
}
