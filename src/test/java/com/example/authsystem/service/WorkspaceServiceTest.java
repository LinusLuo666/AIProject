package com.example.authsystem.service;

import com.example.authsystem.config.VideoEditorProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkspaceServiceTest {

    private WorkspaceService workspaceService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        VideoEditorProperties properties = new VideoEditorProperties();
        properties.setWorkspacePath(tempDir);
        properties.setAllowedExtensions(List.of("mp4", ".mov"));
        properties.setOutputPrefix("edited_");
        workspaceService = new WorkspaceService(properties);
    }

    @Test
    void resolveInputFileReturnsAbsolutePathForValidInput() throws IOException {
        Path videoFile = Files.createFile(tempDir.resolve("sample.mp4"));

        Path resolved = workspaceService.resolveInputFile("sample.mp4");
        assertEquals(videoFile.toAbsolutePath(), resolved);
        assertTrue(resolved.isAbsolute());

        Path generatedOutputPath = workspaceService.generateOutputPath(resolved);
        assertEquals(tempDir.resolve("edited_sample.mp4"), generatedOutputPath);

        String outputFilename = workspaceService.generateOutputFilename("sample.mp4");
        assertEquals("edited_sample.mp4", outputFilename);
    }

    @Test
    void resolveInputFileRejectsPathTraversal() {
        assertThrows(IllegalArgumentException.class, () -> workspaceService.resolveInputFile("../secret.mp4"));
    }

    @Test
    void resolveInputFileRejectsFilesWithOutputPrefix() throws IOException {
        Files.createFile(tempDir.resolve("edited_existing.mp4"));
        assertThrows(IllegalArgumentException.class, () -> workspaceService.resolveInputFile("edited_existing.mp4"));
    }
}
