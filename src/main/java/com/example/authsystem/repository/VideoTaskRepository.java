package com.example.authsystem.repository;

import com.example.authsystem.entity.VideoTask;
import com.example.authsystem.entity.VideoTaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface VideoTaskRepository extends JpaRepository<VideoTask, Long> {

    List<VideoTask> findByStatus(VideoTaskStatus status);

    List<VideoTask> findByStatusInAndCreatedAtBetween(Collection<VideoTaskStatus> statuses, LocalDateTime start, LocalDateTime end);

    List<VideoTask> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
