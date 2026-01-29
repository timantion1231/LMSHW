package com.tima.lms.service;

import com.tima.lms.dto.request.LessonRequest;
import com.tima.lms.dto.response.LessonResponse;
import com.tima.lms.entity.Lesson;
import com.tima.lms.entity.Module;
import com.tima.lms.repository.LessonRepository;
import com.tima.lms.repository.ModuleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    public LessonResponse createLesson(LessonRequest request) {
        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new RuntimeException("Module not found with id: " + request.getModuleId()));

        Lesson lesson = new Lesson();
        lesson.setTitle(request.getTitle());
        lesson.setContent(request.getContent());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setModule(module);

        Lesson savedLesson = lessonRepository.save(lesson);
        return toResponse(savedLesson);
    }

    public LessonResponse getLessonById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id));
        return toResponse(lesson);
    }

    public List<LessonResponse> getAllLessons() {
        return lessonRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<LessonResponse> getLessonsByModuleId(Long moduleId) {
        return lessonRepository.findByModuleId(moduleId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public LessonResponse updateLesson(Long id, LessonRequest request) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id));

        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new RuntimeException("Module not found with id: " + request.getModuleId()));

        lesson.setTitle(request.getTitle());
        lesson.setContent(request.getContent());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setModule(module);

        Lesson updatedLesson = lessonRepository.save(lesson);
        return toResponse(updatedLesson);
    }

    public void deleteLesson(Long id) {
        if (!lessonRepository.existsById(id)) {
            throw new RuntimeException("Lesson not found with id: " + id);
        }
        lessonRepository.deleteById(id);
    }

    private LessonResponse toResponse(Lesson lesson) {
        LessonResponse response = new LessonResponse();
        response.setId(lesson.getId());
        response.setTitle(lesson.getTitle());
        response.setContent(lesson.getContent());
        response.setVideoUrl(lesson.getVideoUrl());
        response.setModuleTitle(lesson.getModule() != null ? lesson.getModule().getTitle() : null);
        return response;
    }
}