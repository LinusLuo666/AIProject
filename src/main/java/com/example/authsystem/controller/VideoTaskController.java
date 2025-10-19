package com.example.authsystem.controller;

import com.example.authsystem.dto.VideoTaskRequest;
import com.example.authsystem.service.VideoTaskService;
import com.example.authsystem.service.WorkspaceService;
import com.example.authsystem.video.VideoTask;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/video")
public class VideoTaskController {

    private final VideoTaskService videoTaskService;
    private final WorkspaceService workspaceService;

    public VideoTaskController(VideoTaskService videoTaskService, WorkspaceService workspaceService) {
        this.videoTaskService = videoTaskService;
        this.workspaceService = workspaceService;
    }

    @PostMapping("/tasks")
    public ResponseEntity<VideoTask> createTask(@Valid @RequestBody VideoTaskRequest request) {
        VideoTask task = videoTaskService.createTask(request.getFilename());
        return ResponseEntity.ok(task);
    }

    @PostMapping("/tasks/{taskId}/execute")
    public ResponseEntity<VideoTask> executeTask(@PathVariable long taskId) {
        try {
            VideoTask task = videoTaskService.executeTask(taskId);
            return ResponseEntity.ok(task);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/tasks")
    public List<VideoTask> listTasks() {
        return videoTaskService.getAllTasks();
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<VideoTask> getTask(@PathVariable long taskId) {
        return videoTaskService.getTask(taskId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/workspace")
    public List<String> browseWorkspace() {
        return workspaceService.listWorkspaceContents();
    }
}
