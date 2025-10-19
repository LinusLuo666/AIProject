package com.example.authsystem.repository;

import com.example.authsystem.entity.VideoTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoTaskRepository extends JpaRepository<VideoTask, Long> {
}
