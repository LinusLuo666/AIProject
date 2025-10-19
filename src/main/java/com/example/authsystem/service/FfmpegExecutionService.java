package com.example.authsystem.service;

import com.example.authsystem.config.VideoEditorProperties;
import com.example.authsystem.entity.VideoTask;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class FfmpegExecutionService {

    private final VideoEditorProperties properties;
    private final ProcessBuilderFactory processBuilderFactory;

    public FfmpegExecutionService(VideoEditorProperties properties, ProcessBuilderFactory processBuilderFactory) {
        this.properties = properties;
        this.processBuilderFactory = processBuilderFactory;
    }

    public boolean execute(VideoTask task, List<String> command) {
        validateOutputFile(task.getOutputFile());
        ProcessBuilder processBuilder = processBuilderFactory.create(command);
        processBuilder.directory(new File(properties.getWorkspacePath()));

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            String stdout;
            try (InputStream inputStream = process.getInputStream()) {
                stdout = readStream(inputStream);
            }
            String stderr;
            try (InputStream errorStream = process.getErrorStream()) {
                stderr = readStream(errorStream);
            }
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append("Command: ").append(String.join(" ", command)).append(System.lineSeparator());
            if (StringUtils.hasText(stdout)) {
                logBuilder.append("[STDOUT]").append(System.lineSeparator()).append(stdout.trim()).append(System.lineSeparator());
            }
            if (StringUtils.hasText(stderr)) {
                logBuilder.append("[STDERR]").append(System.lineSeparator()).append(stderr.trim()).append(System.lineSeparator());
            }
            logBuilder.append("Exit code: ").append(exitCode);
            task.appendLog(logBuilder.toString());
            return exitCode == 0;
        } catch (IOException e) {
            task.appendLog("Execution failed: " + e.getMessage());
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            task.appendLog("Execution interrupted: " + e.getMessage());
            return false;
        }
    }

    private void validateOutputFile(String outputFile) {
        String prefix = properties.getOutputPrefix();
        if (StringUtils.hasText(prefix) && (outputFile == null || !outputFile.startsWith(prefix))) {
            throw new IllegalArgumentException("Output file must start with configured prefix: " + prefix);
        }
    }

    private String readStream(InputStream inputStream) throws IOException {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
}
