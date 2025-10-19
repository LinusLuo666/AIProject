package com.example.authsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class VideoSegment {

    @Column(name = "start_second")
    private Integer startSecond;

    @Column(name = "end_second")
    private Integer endSecond;

    @Column(name = "sort_order")
    private Integer sortOrder;

    public VideoSegment() {
    }

    public VideoSegment(Integer startSecond, Integer endSecond, Integer sortOrder) {
        this.startSecond = startSecond;
        this.endSecond = endSecond;
        this.sortOrder = sortOrder;
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
