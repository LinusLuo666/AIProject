package com.example.authsystem.service;

import com.example.authsystem.video.VideoTask;
import com.example.authsystem.video.VideoTaskStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class VideoTaskService {

    private final WorkspaceService workspaceService;
    private final AtomicLong idGenerator = new AtomicLong();
    private final ConcurrentMap<Long, VideoTask> tasks = new ConcurrentHashMap<>();

    public VideoTaskService(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    public VideoTask createTask(String userProvidedFile) {
        long id = idGenerator.incrementAndGet();
        VideoTask task = new VideoTask(id, userProvidedFile);
        try {
            Path inputPath = workspaceService.resolveInputFile(userProvidedFile);
            task.setInputFile(workspaceService.toWorkspaceRelativeString(inputPath));
            Path outputPath = workspaceService.generateOutputPath(inputPath);
            task.setOutputFile(workspaceService.toWorkspaceRelativeString(outputPath));
            task.setStatus(VideoTaskStatus.PENDING);
            task.setErrorMessage(null);
        } catch (IllegalArgumentException ex) {
            task.setStatus(VideoTaskStatus.ERROR);
            task.setErrorMessage(ex.getMessage());
        }
        task.touch();
        tasks.put(id, task);
        return task;
    }

    public VideoTask executeTask(long taskId) {
        VideoTask task = Optional.ofNullable(tasks.get(taskId))
                .orElseThrow(() -> new NoSuchElementException("Task not found: " + taskId));

        if (task.getStatus() == VideoTaskStatus.ERROR) {
            return task;
        }

        try {
            Path inputPath = workspaceService.resolveInputFile(task.getInputFile());
            Path outputPath = workspaceService.generateOutputPath(inputPath);
            task.setStatus(VideoTaskStatus.RUNNING);
            task.touch();

            if (outputPath.getParent() != null) {
                Files.createDirectories(outputPath.getParent());
            }
            Files.copy(inputPath, outputPath, StandardCopyOption.REPLACE_EXISTING);

            task.setOutputFile(workspaceService.toWorkspaceRelativeString(outputPath));
            task.setStatus(VideoTaskStatus.COMPLETED);
            task.setErrorMessage(null);
        } catch (IllegalArgumentException | IOException ex) {
            task.setStatus(VideoTaskStatus.ERROR);
            task.setErrorMessage(ex.getMessage());
        }

        task.touch();
        return task;
    }

    public Optional<VideoTask> getTask(long taskId) {
        return Optional.ofNullable(tasks.get(taskId));
    }

    public List<VideoTask> getAllTasks() {
        return tasks.values().stream()
                .sorted(Comparator.comparingLong(VideoTask::getId))
                .toList();
    }
}
