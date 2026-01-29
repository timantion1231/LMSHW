package com.tima.lms.dto.response;

import lombok.Data;

@Data
public class ModuleResponse {
    private Long id;
    private String title;
    private Integer orderIndex;
    private String description;
    private String courseTitle;
}