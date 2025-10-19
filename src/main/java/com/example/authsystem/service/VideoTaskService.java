package com.example.authsystem.service;

import com.example.authsystem.entity.VideoTask;
import com.example.authsystem.entity.VideoTaskStatus;
import com.example.authsystem.repository.VideoTaskRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class VideoTaskService {

    private final VideoTaskRepository videoTaskRepository;
    private final FfmpegCommandBuilder commandBuilder;
    private final FfmpegExecutionService executionService;
    private final TaskExecutor taskExecutor;

    public VideoTaskService(VideoTaskRepository videoTaskRepository,
                            FfmpegCommandBuilder commandBuilder,
                            FfmpegExecutionService executionService,
                            @Qualifier("videoTaskExecutor") TaskExecutor taskExecutor) {
        this.videoTaskRepository = videoTaskRepository;
        this.commandBuilder = commandBuilder;
        this.executionService = executionService;
        this.taskExecutor = taskExecutor != null ? taskExecutor : Runnable::run;
    }

    public String getCommandPreview(Long taskId) {
        VideoTask task = loadTask(taskId);
        ensureApproved(task);
        return commandBuilder.build(task).getPreview();
    }

    public void confirmExecution(Long taskId) {
        VideoTask task = loadTask(taskId);
        ensureApproved(task);
        taskExecutor.execute(() -> runTask(taskId));
    }

    public VideoTask approveTask(Long taskId) {
        VideoTask task = loadTask(taskId);
        task.setStatus(VideoTaskStatus.APPROVED);
        return videoTaskRepository.save(task);
    }

    private void runTask(Long taskId) {
        VideoTask task = loadTask(taskId);
        task.setStatus(VideoTaskStatus.RUNNING);
        videoTaskRepository.save(task);

        FfmpegCommandBuilder.FfmpegCommand command = commandBuilder.build(task);
        boolean success;
        try {
            success = executionService.execute(task, command.getArguments());
        } catch (RuntimeException ex) {
            task.appendLog("Execution raised exception: " + ex.getMessage());
            success = false;
        }

        task.setStatus(success ? VideoTaskStatus.COMPLETED : VideoTaskStatus.FAILED);
        videoTaskRepository.save(task);
    }

    private VideoTask loadTask(Long taskId) {
        Assert.notNull(taskId, "Task id must not be null");
        Optional<VideoTask> optionalTask = videoTaskRepository.findById(taskId);
        return optionalTask.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video task not found"));
    }

    private void ensureApproved(VideoTask task) {
        if (task.getStatus() != VideoTaskStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Video task must be approved before execution");
        }
    }
}
