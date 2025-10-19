package com.example.authsystem.service;

import com.example.authsystem.config.VideoEditorProperties;
import com.example.authsystem.entity.VideoTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FfmpegExecutionServiceTest {

    private VideoEditorProperties properties;

    @Mock
    private ProcessBuilderFactory processBuilderFactory;

    @Mock
    private ProcessBuilder processBuilder;

    @Mock
    private Process process;

    private FfmpegExecutionService executionService;

    @BeforeEach
    void setUp() {
        properties = new VideoEditorProperties();
        properties.setFfmpegPath("ffmpeg");
        properties.setWorkspacePath("/tmp");
        properties.setOutputPrefix("output/");
        executionService = new FfmpegExecutionService(properties, processBuilderFactory);
    }

    @Test
    void executeShouldCaptureOutputAndReturnSuccess() throws Exception {
        List<String> command = List.of("ffmpeg", "-i", "input.mp4", "output/result.mp4");
        VideoTask task = new VideoTask();
        task.setOutputFile("output/result.mp4");

        when(processBuilderFactory.create(command)).thenReturn(processBuilder);
        when(processBuilder.directory(any(File.class))).thenReturn(processBuilder);
        when(processBuilder.start()).thenReturn(process);
        when(process.waitFor()).thenReturn(0);
        when(process.getInputStream()).thenReturn(stream("success"));
        when(process.getErrorStream()).thenReturn(stream(""));

        boolean success = executionService.execute(task, command);

        assertTrue(success);
        assertNotNull(task.getExecutionLog());
        assertTrue(task.getExecutionLog().contains("success"));
        ArgumentCaptor<File> directoryCaptor = ArgumentCaptor.forClass(File.class);
        verify(processBuilder).directory(directoryCaptor.capture());
        assertEquals(new File("/tmp"), directoryCaptor.getValue());
    }

    @Test
    void executeShouldReturnFalseOnFailure() throws Exception {
        List<String> command = List.of("ffmpeg", "-i", "input.mp4", "output/result.mp4");
        VideoTask task = new VideoTask();
        task.setOutputFile("output/result.mp4");

        when(processBuilderFactory.create(command)).thenReturn(processBuilder);
        when(processBuilder.directory(any(File.class))).thenReturn(processBuilder);
        when(processBuilder.start()).thenReturn(process);
        when(process.waitFor()).thenReturn(1);
        when(process.getInputStream()).thenReturn(stream(""));
        when(process.getErrorStream()).thenReturn(stream("error"));

        boolean success = executionService.execute(task, command);

        assertFalse(success);
        assertNotNull(task.getExecutionLog());
        assertTrue(task.getExecutionLog().contains("error"));
    }

    @Test
    void executeShouldValidateOutputPrefix() {
        List<String> command = List.of("ffmpeg", "-i", "input.mp4", "invalid/result.mp4");
        VideoTask task = new VideoTask();
        task.setOutputFile("invalid/result.mp4");

        assertThrows(IllegalArgumentException.class, () -> executionService.execute(task, command));
        verifyNoInteractions(processBuilderFactory);
    }

    private InputStream stream(String value) {
        return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
    }
}
