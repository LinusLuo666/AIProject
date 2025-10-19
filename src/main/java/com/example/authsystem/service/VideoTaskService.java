package com.example.authsystem.service;

import com.example.authsystem.dto.VideoTaskResponse;
import com.example.authsystem.dto.video.VideoSegmentDraft;
import com.example.authsystem.dto.video.VideoTaskDraft;
import com.example.authsystem.entity.VideoTask;
import com.example.authsystem.repository.VideoTaskRepository;
import com.example.authsystem.video.VideoQualityTier;
import com.example.authsystem.video.VideoTaskStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VideoTaskService {

    private static final Logger log = LoggerFactory.getLogger(VideoTaskService.class);

    private static final TypeReference<List<VideoSegmentDraft>> SEGMENT_TYPE = new TypeReference<>() {
    };

    private final VideoTaskRepository videoTaskRepository;
    private final ObjectMapper objectMapper;

    public VideoTaskService(VideoTaskRepository videoTaskRepository, ObjectMapper objectMapper) {
        this.videoTaskRepository = videoTaskRepository;
        this.objectMapper = objectMapper;
    }

    public VideoTask createPendingTask(String instruction, VideoTaskDraft draft) {
        VideoQualityTier tier = VideoQualityTier.fromValue(draft.getQualityTier());
        String segmentsJson = serializeSegments(draft.getSegments());
        VideoTask task = new VideoTask(instruction, draft.getFileName(), tier, segmentsJson);
        task.setStatus(VideoTaskStatus.PENDING_APPROVAL);
        return videoTaskRepository.save(task);
    }

    public VideoTaskResponse toResponse(VideoTask task) {
        VideoTaskResponse response = new VideoTaskResponse();
        response.setId(task.getId());
        response.setFileName(task.getFileName());
        response.setQualityTier(task.getQualityTier());
        response.setStatus(task.getStatus());
        response.setOriginalInstruction(task.getOriginalInstruction());
        response.setCreatedAt(task.getCreatedAt());
        response.setSegments(deserializeSegments(task.getSegmentsJson()));
        return response;
    }

    private String serializeSegments(List<VideoSegmentDraft> segments) {
        try {
            return objectMapper.writeValueAsString(segments);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize video segments, storing empty list", e);
            return "[]";
        }
    }

    private List<VideoSegmentDraft> deserializeSegments(String json) {
        try {
            return objectMapper.readValue(json, SEGMENT_TYPE);
        } catch (Exception e) {
            log.warn("Failed to deserialize video segments", e);
            return Collections.emptyList();
        }
    }
}
