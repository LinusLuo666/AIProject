package com.example.authsystem.service;

import com.example.authsystem.config.VideoEditorProperties;
import org.springframework.stereotype.Service;

import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class WorkspaceService {

    private final VideoEditorProperties properties;
    private final Set<String> allowedExtensions;

    public WorkspaceService(VideoEditorProperties properties) {
        this.properties = properties;
        this.allowedExtensions = normalizeExtensions(properties.getAllowedExtensions());
    }

    public Path resolveInputFile(String userProvidedPath) {
        if (userProvidedPath == null || userProvidedPath.isBlank()) {
            throw new IllegalArgumentException("Input filename must not be blank");
        }

        Path workspaceRoot = getWorkspaceRoot();
        Path resolvedPath = workspaceRoot.resolve(userProvidedPath).normalize();

        if (!resolvedPath.startsWith(workspaceRoot)) {
            throw new IllegalArgumentException("File must be located inside the configured workspace");
        }

        if (!Files.exists(resolvedPath) || !Files.isRegularFile(resolvedPath)) {
            throw new IllegalArgumentException("File does not exist inside workspace: " + userProvidedPath);
        }

        String fileName = resolvedPath.getFileName().toString();
        if (fileName.startsWith(getOutputPrefix())) {
            throw new IllegalArgumentException("File already contains the output prefix: " + getOutputPrefix());
        }

        if (!isExtensionAllowed(fileName)) {
            throw new IllegalArgumentException("File extension is not allowed: " + fileName);
        }

        return resolvedPath.toAbsolutePath();
    }

    public Path generateOutputPath(String userProvidedPath) {
        Path inputPath = resolveInputFile(userProvidedPath);
        return generateOutputPath(inputPath);
    }

    public Path generateOutputPath(Path inputFile) {
        Path workspaceRoot = getWorkspaceRoot();
        Path normalizedInput = inputFile.toAbsolutePath().normalize();
        if (!normalizedInput.startsWith(workspaceRoot)) {
            throw new IllegalArgumentException("Input file must be located inside the workspace");
        }

        String fileName = normalizedInput.getFileName().toString();
        Path relative = workspaceRoot.relativize(normalizedInput);
        Path parent = relative.getParent();
        String outputFileName = getOutputPrefix() + fileName;

        Path outputRelative = parent == null ? Paths.get(outputFileName) : parent.resolve(outputFileName);
        Path outputPath = workspaceRoot.resolve(outputRelative).normalize();

        if (!outputPath.startsWith(workspaceRoot)) {
            throw new IllegalArgumentException("Output path must remain inside the workspace");
        }

        return outputPath;
    }

    public String generateOutputFilename(String userProvidedPath) {
        Path outputPath = generateOutputPath(userProvidedPath);
        return toWorkspaceRelativeString(outputPath);
    }

    public List<String> listWorkspaceContents() {
        Path workspaceRoot = getWorkspaceRoot();
        if (!Files.exists(workspaceRoot)) {
            return Collections.emptyList();
        }

        try (Stream<Path> stream = Files.walk(workspaceRoot)) {
            return stream.filter(Files::isRegularFile)
                    .filter(this::isValidInputFile)
                    .map(this::toWorkspaceRelativeString)
                    .sorted()
                    .collect(Collectors.toList());
        } catch (java.io.IOException ex) {
            throw new UncheckedIOException("Unable to read workspace contents", ex);
        }
    }

    public String toWorkspaceRelativeString(Path absolutePath) {
        Path workspaceRoot = getWorkspaceRoot();
        Path relative = workspaceRoot.relativize(absolutePath.toAbsolutePath().normalize());
        return relative.toString().replace('\', '/');
    }

    private boolean isValidInputFile(Path path) {
        String fileName = path.getFileName().toString();
        return !fileName.startsWith(getOutputPrefix()) && isExtensionAllowed(fileName);
    }

    private boolean isExtensionAllowed(String fileName) {
        String extension = extractExtension(fileName);
        if (extension.isEmpty()) {
            return false;
        }
        return allowedExtensions.contains(extension);
    }

    private String extractExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index < 0 || index == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(index + 1).toLowerCase();
    }

    private Path getWorkspaceRoot() {
        Path configuredPath = properties.getWorkspacePath();
        if (configuredPath == null) {
            throw new IllegalStateException("Workspace path is not configured");
        }
        Path workspace = configuredPath.toAbsolutePath().normalize();
        try {
            Files.createDirectories(workspace);
        } catch (java.io.IOException ex) {
            throw new UncheckedIOException("Unable to create workspace directory", ex);
        }
        return workspace;
    }

    private String getOutputPrefix() {
        String prefix = properties.getOutputPrefix();
        return prefix == null ? "" : prefix;
    }

    private Set<String> normalizeExtensions(List<String> extensions) {
        if (extensions == null) {
            return Collections.emptySet();
        }
        Set<String> normalized = new HashSet<>();
        for (String extension : extensions) {
            if (extension == null) {
                continue;
            }
            String trimmed = extension.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (trimmed.startsWith(".")) {
                trimmed = trimmed.substring(1);
            }
            normalized.add(trimmed.toLowerCase());
        }
        return Collections.unmodifiableSet(normalized);
    }
}
