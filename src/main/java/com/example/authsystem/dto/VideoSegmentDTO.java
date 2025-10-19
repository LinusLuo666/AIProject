package com.example.authsystem.dto;

import com.example.authsystem.entity.VideoSegment;

public class VideoSegmentDTO {

    private Integer startSecond;
    private Integer endSecond;
    private Integer sortOrder;

    public VideoSegmentDTO() {
    }

    public VideoSegmentDTO(VideoSegment segment) {
        this.startSecond = segment.getStartSecond();
        this.endSecond = segment.getEndSecond();
        this.sortOrder = segment.getSortOrder();
    }

    public Integer getStartSecond() {
        return startSecond;
    }

    public void setStartSecond(Integer startSecond) {
        this.startSecond = startSecond;
    }

    public Integer getEndSecond() {
        return endSecond;
    }

    public void setEndSecond(Integer endSecond) {
        this.endSecond = endSecond;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
