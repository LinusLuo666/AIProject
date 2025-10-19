package com.example.authsystem.dto.video;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoSegmentDraft {

    private String title;

    private String description;

    private String startTimecode;

    private String endTimecode;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTimecode() {
        return startTimecode;
    }

    public void setStartTimecode(String startTimecode) {
        this.startTimecode = startTimecode;
    }

    public String getEndTimecode() {
        return endTimecode;
    }

    public void setEndTimecode(String endTimecode) {
        this.endTimecode = endTimecode;
    }
}
