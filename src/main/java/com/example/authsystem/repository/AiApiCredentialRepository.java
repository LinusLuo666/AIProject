package com.example.authsystem.repository;

import com.example.authsystem.entity.AiApiCredential;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiApiCredentialRepository extends JpaRepository<AiApiCredential, Long> {

    Optional<AiApiCredential> findByProvider(String provider);
}
