package com.example.authsystem.repository;

import com.example.authsystem.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    
    List<Menu> findByParentIdOrderBySortOrderAsc(Long parentId);
    
    List<Menu> findByStatusOrderBySortOrderAsc(Integer status);
    
    List<Menu> findByMenuTypeOrderBySortOrderAsc(Integer menuType);
    
    @Query("SELECT DISTINCT m FROM Menu m JOIN m.roles r JOIN r.users u WHERE u.username = :username AND m.status = 1 ORDER BY m.parentId, m.sortOrder")
    List<Menu> findByUsername(@Param("username") String username);
    
    @Query("SELECT DISTINCT m FROM Menu m JOIN m.roles r WHERE r.id IN :roleIds AND m.status = 1 ORDER BY m.parentId, m.sortOrder")
    List<Menu> findByRoleIds(@Param("roleIds") List<Long> roleIds);
    
    boolean existsByPermission(String permission);
}