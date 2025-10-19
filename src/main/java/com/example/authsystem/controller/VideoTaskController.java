package com.example.authsystem.controller;

import com.example.authsystem.dto.VideoSegmentDTO;
import com.example.authsystem.dto.VideoTaskLogRequest;
import com.example.authsystem.dto.VideoTaskRequest;
import com.example.authsystem.dto.VideoTaskResponse;
import com.example.authsystem.dto.VideoTaskStatusUpdateRequest;
import com.example.authsystem.entity.VideoSegment;
import com.example.authsystem.entity.VideoTask;
import com.example.authsystem.entity.VideoTaskStatus;
import com.example.authsystem.service.VideoTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/video-tasks")
@Tag(name = "Video Task Management", description = "APIs for managing video processing tasks")
public class VideoTaskController {

    @Autowired
    private VideoTaskService videoTaskService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Create video task", description = "Create a new video processing task and generate command preview")
    public ResponseEntity<VideoTaskResponse> createTask(@Valid @RequestBody VideoTaskRequest request) {
        VideoTask task = new VideoTask();
        task.setSourceFileName(request.getSourceFileName());
        task.setQualityProfile(request.getQualityProfile());
        task.setSegments(mapSegments(request.getSegments()));
        task.setCommandPreview(request.getCommandPreview());
        VideoTask created = videoTaskService.createTask(task);
        return ResponseEntity.ok(new VideoTaskResponse(created));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "List video tasks", description = "Query video tasks by status and creation date range")
    public ResponseEntity<List<VideoTaskResponse>> listTasks(
            @RequestParam(required = false) VideoTaskStatus status,
            @RequestParam(required = false) List<VideoTaskStatus> statuses,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        List<VideoTask> tasks;
        if (statuses != null && !statuses.isEmpty() && from != null && to != null) {
            tasks = videoTaskService.findByStatusesAndCreatedRange(statuses, from, to);
        } else if (status != null && from != null && to != null) {
            tasks = videoTaskService.findByStatusesAndCreatedRange(List.of(status), from, to);
        } else if (status != null) {
            tasks = videoTaskService.findByStatus(status);
        } else if (from != null && to != null) {
            tasks = videoTaskService.findByCreatedRange(from, to);
        } else {
            tasks = videoTaskService.findAll();
        }

        List<VideoTaskResponse> responses = tasks.stream()
                .map(VideoTaskResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Get video task", description = "Retrieve a video task by id")
    public ResponseEntity<VideoTaskResponse> getTask(@PathVariable Long id) {
        return videoTaskService.findById(id)
                .map(task -> ResponseEntity.ok(new VideoTaskResponse(task)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/command-preview")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Get command preview", description = "Retrieve generated command preview for a task")
    public ResponseEntity<String> getCommandPreview(@PathVariable Long id) {
        return videoTaskService.findById(id)
                .map(task -> ResponseEntity.ok(task.getCommandPreview()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Update status", description = "Update task status and append message")
    public ResponseEntity<VideoTaskResponse> updateStatus(@PathVariable Long id,
                                                         @Valid @RequestBody VideoTaskStatusUpdateRequest request) {
        try {
            VideoTask updated = videoTaskService.updateStatus(id, request.getStatus(), request.getMessage());
            return ResponseEntity.ok(new VideoTaskResponse(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Approve task", description = "Approve pending video task")
    public ResponseEntity<VideoTaskResponse> approveTask(@PathVariable Long id,
                                                         @RequestBody(required = false) VideoTaskLogRequest request) {
        try {
            VideoTask updated = videoTaskService.approveTask(id, extractMessage(request));
            return ResponseEntity.ok(new VideoTaskResponse(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Reject task", description = "Reject a video task during approval phase")
    public ResponseEntity<VideoTaskResponse> rejectTask(@PathVariable Long id,
                                                        @RequestBody(required = false) VideoTaskLogRequest request) {
        try {
            VideoTask updated = videoTaskService.rejectTask(id, extractMessage(request));
            return ResponseEntity.ok(new VideoTaskResponse(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/pause")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Pause task", description = "Pause an in-progress video task")
    public ResponseEntity<VideoTaskResponse> pauseTask(@PathVariable Long id,
                                                       @RequestBody(required = false) VideoTaskLogRequest request) {
        try {
            VideoTask updated = videoTaskService.pauseTask(id, extractMessage(request));
            return ResponseEntity.ok(new VideoTaskResponse(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/resume")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Resume task", description = "Resume a paused video task")
    public ResponseEntity<VideoTaskResponse> resumeTask(@PathVariable Long id,
                                                        @RequestBody(required = false) VideoTaskLogRequest request) {
        try {
            VideoTask updated = videoTaskService.resumeTask(id, extractMessage(request));
            return ResponseEntity.ok(new VideoTaskResponse(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Cancel task", description = "Cancel video task execution")
    public ResponseEntity<VideoTaskResponse> cancelTask(@PathVariable Long id,
                                                        @RequestBody(required = false) VideoTaskLogRequest request) {
        try {
            VideoTask updated = videoTaskService.cancelTask(id, extractMessage(request));
            return ResponseEntity.ok(new VideoTaskResponse(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/logs")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Record execution log", description = "Record execution message for a video task")
    public ResponseEntity<VideoTaskResponse> logTaskProgress(@PathVariable Long id,
                                                             @RequestBody(required = false) VideoTaskLogRequest request) {
        try {
            String message = extractMessage(request);
            VideoTaskStatus status = request != null ? request.getStatus() : null;
            VideoTask updated = videoTaskService.recordExecutionLog(id, message, status);
            return ResponseEntity.ok(new VideoTaskResponse(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    private List<VideoSegment> mapSegments(List<VideoSegmentDTO> segmentDTOs) {
        if (segmentDTOs == null) {
            return new ArrayList<>();
        }
        return segmentDTOs.stream()
                .map(dto -> new VideoSegment(dto.getStartSecond(), dto.getEndSecond(), dto.getSortOrder()))
                .collect(Collectors.toList());
    }

    private String extractMessage(VideoTaskLogRequest request) {
        return request != null ? request.getMessage() : null;
    }
}
