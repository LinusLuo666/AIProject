package com.example.authsystem.dto.video;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoTaskDraft {

    private String fileName;

    private String qualityTier;

    private List<VideoSegmentDraft> segments = new ArrayList<>();

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getQualityTier() {
        return qualityTier;
    }

    public void setQualityTier(String qualityTier) {
        this.qualityTier = qualityTier;
    }

    public List<VideoSegmentDraft> getSegments() {
        return segments;
    }

    public void setSegments(List<VideoSegmentDraft> segments) {
        this.segments = segments;
    }
}
