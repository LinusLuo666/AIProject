package com.example.authsystem.service;

import com.example.authsystem.entity.VideoTask;
import com.example.authsystem.entity.VideoTaskStatus;
import com.example.authsystem.repository.VideoTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VideoTaskServiceTest {

    @Mock
    private VideoTaskRepository videoTaskRepository;

    @Mock
    private FfmpegCommandBuilder commandBuilder;

    @Mock
    private FfmpegExecutionService executionService;

    private TaskExecutor taskExecutor;

    private VideoTaskService videoTaskService;

    private VideoTask task;

    @BeforeEach
    void setUp() {
        taskExecutor = Runnable::run;
        MockitoAnnotations.openMocks(this);
        videoTaskService = new VideoTaskService(videoTaskRepository, commandBuilder, executionService, taskExecutor);
        task = new VideoTask();
        task.setId(1L);
        task.setStatus(VideoTaskStatus.APPROVED);
        task.setInputFile("input.mp4");
        task.setOutputFile("output/result.mp4");
    }

    @Test
    void confirmExecutionShouldUpdateStatuses() {
        when(videoTaskRepository.findById(1L)).thenReturn(Optional.of(task), Optional.of(task));
        FfmpegCommandBuilder.FfmpegCommand command = mock(FfmpegCommandBuilder.FfmpegCommand.class);
        when(command.getArguments()).thenReturn(List.of("ffmpeg", "-i", "input.mp4", "output/result.mp4"));
        when(commandBuilder.build(any(VideoTask.class))).thenReturn(command);
        when(executionService.execute(any(VideoTask.class), eq(command.getArguments()))).thenReturn(true);

        videoTaskService.confirmExecution(1L);

        ArgumentCaptor<VideoTask> captor = ArgumentCaptor.forClass(VideoTask.class);
        verify(videoTaskRepository, times(2)).save(captor.capture());
        List<VideoTask> savedTasks = captor.getAllValues();
        assertEquals(VideoTaskStatus.RUNNING, savedTasks.get(0).getStatus());
        assertEquals(VideoTaskStatus.COMPLETED, savedTasks.get(1).getStatus());
    }

    @Test
    void confirmExecutionShouldMarkFailedWhenExecutionFails() {
        when(videoTaskRepository.findById(1L)).thenReturn(Optional.of(task), Optional.of(task));
        FfmpegCommandBuilder.FfmpegCommand command = mock(FfmpegCommandBuilder.FfmpegCommand.class);
        when(command.getArguments()).thenReturn(List.of("ffmpeg", "-i", "input.mp4", "output/result.mp4"));
        when(commandBuilder.build(any(VideoTask.class))).thenReturn(command);
        when(executionService.execute(any(VideoTask.class), eq(command.getArguments()))).thenReturn(false);

        videoTaskService.confirmExecution(1L);

        ArgumentCaptor<VideoTask> captor = ArgumentCaptor.forClass(VideoTask.class);
        verify(videoTaskRepository, times(2)).save(captor.capture());
        List<VideoTask> savedTasks = captor.getAllValues();
        assertEquals(VideoTaskStatus.RUNNING, savedTasks.get(0).getStatus());
        assertEquals(VideoTaskStatus.FAILED, savedTasks.get(1).getStatus());
    }

    @Test
    void getCommandPreviewShouldThrowWhenNotApproved() {
        task.setStatus(VideoTaskStatus.PENDING_APPROVAL);
        when(videoTaskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(ResponseStatusException.class, () -> videoTaskService.getCommandPreview(1L));
    }

    @Test
    void confirmExecutionShouldThrowWhenNotApproved() {
        task.setStatus(VideoTaskStatus.PENDING_APPROVAL);
        when(videoTaskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(ResponseStatusException.class, () -> videoTaskService.confirmExecution(1L));
    }
}
