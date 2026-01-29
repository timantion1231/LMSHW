package com.tima.lms.dto.request;

import lombok.Data;

@Data
public class LessonRequest {
    private String title;
    private String content;
    private String videoUrl;
    private Long moduleId;
}