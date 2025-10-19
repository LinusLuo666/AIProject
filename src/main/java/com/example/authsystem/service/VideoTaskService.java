package com.example.authsystem.service;

import com.example.authsystem.entity.VideoSegment;
import com.example.authsystem.entity.VideoTask;
import com.example.authsystem.entity.VideoTaskStatus;
import com.example.authsystem.repository.VideoTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VideoTaskService {

    @Autowired
    private VideoTaskRepository videoTaskRepository;

    public VideoTask createTask(VideoTask task) {
        if (task.getStatus() == null) {
            task.setStatus(VideoTaskStatus.PENDING);
        }
        if (task.getCommandPreview() == null || task.getCommandPreview().isBlank()) {
            task.setCommandPreview(generateCommandPreview(task));
        }
        return videoTaskRepository.save(task);
    }

    public List<VideoTask> findAll() {
        return videoTaskRepository.findAll();
    }

    public Optional<VideoTask> findById(Long id) {
        return videoTaskRepository.findById(id);
    }

    public List<VideoTask> findByStatus(VideoTaskStatus status) {
        return videoTaskRepository.findByStatus(status);
    }

    public List<VideoTask> findByStatusesAndCreatedRange(Collection<VideoTaskStatus> statuses, LocalDateTime start, LocalDateTime end) {
        return videoTaskRepository.findByStatusInAndCreatedAtBetween(statuses, start, end);
    }

    public List<VideoTask> findByCreatedRange(LocalDateTime start, LocalDateTime end) {
        return videoTaskRepository.findByCreatedAtBetween(start, end);
    }

    public VideoTask approveTask(Long id, String message) {
        return updateStatus(getTaskOrThrow(id), VideoTaskStatus.APPROVED, message);
    }

    public VideoTask rejectTask(Long id, String message) {
        return updateStatus(getTaskOrThrow(id), VideoTaskStatus.CANCELLED, message);
    }

    public VideoTask pauseTask(Long id, String message) {
        return updateStatus(getTaskOrThrow(id), VideoTaskStatus.PAUSED, message);
    }

    public VideoTask resumeTask(Long id, String message) {
        return updateStatus(getTaskOrThrow(id), VideoTaskStatus.RUNNING, message);
    }

    public VideoTask cancelTask(Long id, String message) {
        return updateStatus(getTaskOrThrow(id), VideoTaskStatus.CANCELLED, message);
    }

    public VideoTask updateStatus(Long id, VideoTaskStatus status, String message) {
        return updateStatus(getTaskOrThrow(id), status, message);
    }

    public VideoTask updateCommandPreview(Long id, String commandPreview) {
        VideoTask task = getTaskOrThrow(id);
        task.setCommandPreview(commandPreview);
        return videoTaskRepository.save(task);
    }

    public VideoTask recordExecutionLog(Long id, String message, VideoTaskStatus status) {
        VideoTask task = getTaskOrThrow(id);
        if (status != null) {
            task.setStatus(status);
        }
        if (message != null && !message.isBlank()) {
            task.setLastMessage(message);
        }
        return videoTaskRepository.save(task);
    }

    public String generateCommandPreview(VideoTask task) {
        StringBuilder builder = new StringBuilder("ffmpeg -i \"");
        builder.append(task.getSourceFileName() != null ? task.getSourceFileName() : "input.mp4");
        builder.append("\"");

        List<VideoSegment> segments = task.getSegments();
        if (segments != null && !segments.isEmpty()) {
            String segmentPreview = segments.stream()
                    .map(segment -> buildSegmentCommand(segment))
                    .collect(Collectors.joining(" "));
            if (!segmentPreview.isBlank()) {
                builder.append(' ').append(segmentPreview);
            }
        }

        if (task.getQualityProfile() != null && !task.getQualityProfile().isBlank()) {
            builder.append(" -profile:v ").append(task.getQualityProfile());
        }

        builder.append(" \"output_");
        builder.append(task.getQualityProfile() != null && !task.getQualityProfile().isBlank() ? task.getQualityProfile() : "processed");
        builder.append(".mp4\"");

        return builder.toString();
    }

    private String buildSegmentCommand(VideoSegment segment) {
        StringBuilder builder = new StringBuilder();
        if (segment.getStartSecond() != null) {
            builder.append("-ss ").append(segment.getStartSecond()).append(' ');
        }
        if (segment.getEndSecond() != null) {
            builder.append("-to ").append(segment.getEndSecond()).append(' ');
        }
        builder.append("-c copy");
        return builder.toString().trim();
    }

    private VideoTask updateStatus(VideoTask task, VideoTaskStatus status, String message) {
        task.setStatus(status);
        if (message != null && !message.isBlank()) {
            task.setLastMessage(message);
        }
        return videoTaskRepository.save(task);
    }

    private VideoTask getTaskOrThrow(Long id) {
        return videoTaskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Video task not found: " + id));
    }
}
