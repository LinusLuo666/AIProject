package com.example.authsystem.repository;

import com.example.authsystem.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Role findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT r FROM Role r WHERE (:name IS NULL OR r.name LIKE %:name%) AND (:status IS NULL OR r.status = :status)")
    Page<Role> findByNameContainingAndStatus(
            @Param("name") String name, 
            @Param("status") Integer status, 
            Pageable pageable);
    
    List<Role> findByStatus(Integer status);
}