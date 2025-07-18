package com.example.authsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "menus")
public class Menu {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Menu name is required")
    @Size(max = 50, message = "Menu name must not exceed 50 characters")
    @Column(nullable = false)
    private String name;
    
    @Column
    private String path;
    
    @Column
    private String component;
    
    @Column
    private String icon;
    
    @Column(name = "parent_id", nullable = false)
    private Long parentId = 0L; // 0 for root menu
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @Column
    private String permission; // permission identifier
    
    @Column(name = "menu_type", nullable = false)
    private Integer menuType = 1; // 1: menu, 2: button
    
    @Column(nullable = false)
    private Integer status = 1; // 1: active, 0: inactive
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToMany(mappedBy = "menus", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Menu() {}
    
    public Menu(String name, String path, String component, String permission) {
        this.name = name;
        this.path = path;
        this.component = component;
        this.permission = permission;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public String getComponent() {
        return component;
    }
    
    public void setComponent(String component) {
        this.component = component;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public Long getParentId() {
        return parentId;
    }
    
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public String getPermission() {
        return permission;
    }
    
    public void setPermission(String permission) {
        this.permission = permission;
    }
    
    public Integer getMenuType() {
        return menuType;
    }
    
    public void setMenuType(Integer menuType) {
        this.menuType = menuType;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Set<Role> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    
    public boolean isActive() {
        return status == 1;
    }
    
    public boolean isMenu() {
        return menuType == 1;
    }
    
    public boolean isButton() {
        return menuType == 2;
    }
}