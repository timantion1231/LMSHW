package com.tima.lms.controller;

import com.tima.lms.entity.Course;
import com.tima.lms.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
public class DemoController {
    private final CourseRepository courseRepository;

    @GetMapping("/lazy-exception/{courseId}")
    public ResponseEntity<String> demonstrateLazyException(@PathVariable Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);

        if (course == null) {
            return ResponseEntity.badRequest().body("Course not found");
        }
        try {

            int moduleCount = course.getModules().size();
            return ResponseEntity.ok("Module count: " + moduleCount + " (No exception occurred - check @Transactional)");
        } catch (Exception e) {
            return ResponseEntity.ok("LazyInitializationException caught: " + e.getMessage());
        }
    }

    @GetMapping("/lazy-fixed/{courseId}")
    public ResponseEntity<String> demonstrateLazyFixed(@PathVariable Long courseId) {
        return ResponseEntity.ok("Для избежания LazyInitializationException используйте:\n" +
                "1. @Transactional в сервисе\n" +
                "2. @EntityGraph в репозитории\n" +
                "3. JOIN FETCH в JPQL запросе");
    }
}