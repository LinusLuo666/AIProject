package com.example.authsystem.dto;

import java.util.List;

public class MenuDTO {
    private Long id;
    private String name;
    private String path;
    private String component;
    private String icon;
    private Long parentId;
    private Integer sortOrder;
    private String permission;
    private Integer menuType;
    private List<MenuDTO> children;

    public MenuDTO() {}

    public MenuDTO(Long id, String name, String path, String component, String icon, 
                   Long parentId, Integer sortOrder, String permission, Integer menuType) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.component = component;
        this.icon = icon;
        this.parentId = parentId;
        this.sortOrder = sortOrder;
        this.permission = permission;
        this.menuType = menuType;
    }

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

    public List<MenuDTO> getChildren() {
        return children;
    }

    public void setChildren(List<MenuDTO> children) {
        this.children = children;
    }
}