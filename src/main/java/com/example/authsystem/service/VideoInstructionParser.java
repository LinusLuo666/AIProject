package com.example.authsystem.service;

import com.example.authsystem.config.AiClientManager;
import com.example.authsystem.dto.video.VideoSegmentDraft;
import com.example.authsystem.dto.video.VideoTaskDraft;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class VideoInstructionParser {

    private static final Logger log = LoggerFactory.getLogger(VideoInstructionParser.class);

    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final AiClientManager aiClientManager;
    private final ObjectMapper objectMapper;

    public VideoInstructionParser(AiClientManager aiClientManager, ObjectMapper objectMapper) {
        this.aiClientManager = aiClientManager;
        this.objectMapper = objectMapper;
    }

    public VideoTaskDraft parse(String instructionText) {
        if (!StringUtils.hasText(instructionText)) {
            throw new IllegalArgumentException("Instruction text must not be empty");
        }
        ChatClient client = aiClientManager.currentClient();
        if (client == null) {
            log.warn("ChatClient is not available, falling back to rule-based parsing");
            return fallbackDraft(instructionText);
        }

        String systemPrompt = "You are an expert video post-production assistant. "
                + "Read the user's instructions and respond with JSON that matches the schema: {\n"
                + "  \"fileName\": string,\n"
                + "  \"qualityTier\": one of [LOW, MEDIUM, HIGH, ULTRA],\n"
                + "  \"segments\": [\n"
                + "    {\"title\": string, \"description\": string, \"startTimecode\": string, \"endTimecode\": string}\n"
                + "  ]\n"
                + "}. Do not include markdown. Use \"MEDIUM\" when unsure about quality.";

        try {
            String response = client.prompt()
                    .system(systemPrompt)
                    .user(instructionText)
                    .call()
                    .content();
            if (!StringUtils.hasText(response)) {
                log.warn("Empty response returned from ChatClient, using fallback parser");
                return fallbackDraft(instructionText);
            }
            VideoTaskDraft draft = objectMapper.readValue(response, VideoTaskDraft.class);
            ensureDraftDefaults(draft, instructionText);
            return draft;
        } catch (Exception ex) {
            log.warn("Failed to parse instruction using AI model, using fallback parser", ex);
            return fallbackDraft(instructionText);
        }
    }

    private void ensureDraftDefaults(VideoTaskDraft draft, String instructionText) {
        if (!StringUtils.hasText(draft.getFileName())) {
            draft.setFileName(generateFileName());
        }
        if (draft.getSegments() == null || draft.getSegments().isEmpty()) {
            draft.setSegments(defaultSegments(instructionText));
        }
        if (!StringUtils.hasText(draft.getQualityTier())) {
            draft.setQualityTier("MEDIUM");
        }
    }

    private VideoTaskDraft fallbackDraft(String instructionText) {
        VideoTaskDraft draft = new VideoTaskDraft();
        draft.setFileName(generateFileName());
        draft.setQualityTier("MEDIUM");
        draft.setSegments(defaultSegments(instructionText));
        return draft;
    }

    private List<VideoSegmentDraft> defaultSegments(String instructionText) {
        List<VideoSegmentDraft> segments = new ArrayList<>();
        VideoSegmentDraft segment = new VideoSegmentDraft();
        segment.setTitle("Initial Cut");
        segment.setDescription(instructionText);
        segment.setStartTimecode("00:00:00:00");
        segment.setEndTimecode("00:01:00:00");
        segments.add(segment);
        return segments;
    }

    private String generateFileName() {
        return "video-" + FILE_DATE_FORMAT.format(LocalDate.now()) + "-" + UUID.randomUUID().toString().substring(0, 8) + ".mp4";
    }
}
