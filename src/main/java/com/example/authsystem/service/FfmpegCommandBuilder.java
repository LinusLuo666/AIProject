package com.example.authsystem.service;

import com.example.authsystem.config.VideoEditorProperties;
import com.example.authsystem.entity.VideoSegment;
import com.example.authsystem.entity.VideoTask;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FfmpegCommandBuilder {

    private final VideoEditorProperties properties;

    public FfmpegCommandBuilder(VideoEditorProperties properties) {
        this.properties = properties;
    }

    public FfmpegCommand build(VideoTask task) {
        List<String> arguments = new ArrayList<>();
        arguments.add(properties.getFfmpegPath());
        addSegmentArguments(task, arguments);
        addVideoFilters(task, arguments);
        arguments.add(task.getOutputFile());
        return new FfmpegCommand(arguments);
    }

    private void addSegmentArguments(VideoTask task, List<String> arguments) {
        List<VideoSegment> segments = task.getSegments();
        if (segments == null || segments.isEmpty()) {
            arguments.add("-i");
            arguments.add(task.getInputFile());
            return;
        }

        for (VideoSegment segment : segments) {
            if (StringUtils.hasText(segment.getStartTime())) {
                arguments.add("-ss");
                arguments.add(segment.getStartTime());
            }
            if (StringUtils.hasText(segment.getEndTime())) {
                arguments.add("-to");
                arguments.add(segment.getEndTime());
            }
            arguments.add("-i");
            arguments.add(task.getInputFile());
        }
    }

    private void addVideoFilters(VideoTask task, List<String> arguments) {
        if (StringUtils.hasText(task.getVideoBitrate())) {
            arguments.add("-b:v");
            arguments.add(task.getVideoBitrate());
        }

        Integer width = task.getScaleWidth();
        Integer height = task.getScaleHeight();
        if (width != null || height != null) {
            int w = width != null ? width : -1;
            int h = height != null ? height : -1;
            arguments.add("-vf");
            arguments.add("scale=" + w + ":" + h);
        }
    }

    public static class FfmpegCommand {
        private final List<String> arguments;
        private final String preview;

        private FfmpegCommand(List<String> arguments) {
            this.arguments = arguments;
            this.preview = arguments.stream()
                    .map(FfmpegCommandBuilder::quoteIfNeeded)
                    .collect(Collectors.joining(" "));
        }

        public List<String> getArguments() {
            return arguments;
        }

        public String getPreview() {
            return preview;
        }
    }

    private static String quoteIfNeeded(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        if (value.contains(" ") || value.contains("\t")) {
            return '"' + value + '"';
        }
        return value;
    }
}
