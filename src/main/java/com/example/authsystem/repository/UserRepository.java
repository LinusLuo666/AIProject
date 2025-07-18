package com.example.authsystem.repository;

import com.example.authsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE (:username IS NULL OR u.username LIKE %:username%) AND (:status IS NULL OR u.status = :status)")
    Page<User> findByUsernameContainingAndStatus(
            @Param("username") String username, 
            @Param("status") Integer status, 
            Pageable pageable);
}