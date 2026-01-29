package com.tima.lms.service;

import com.tima.lms.dto.request.ModuleRequest;
import com.tima.lms.dto.response.ModuleResponse;
import com.tima.lms.entity.Module;
import com.tima.lms.entity.Course;
import com.tima.lms.repository.ModuleRepository;
import com.tima.lms.repository.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ModuleService {
    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;

    public ModuleResponse createModule(ModuleRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + request.getCourseId()));

        Module module = new Module();
        module.setTitle(request.getTitle());
        module.setOrderIndex(request.getOrderIndex());
        module.setDescription(request.getDescription());
        module.setCourse(course);

        Module savedModule = moduleRepository.save(module);
        return toResponse(savedModule);
    }

    public ModuleResponse getModuleById(Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Module not found with id: " + id));
        return toResponse(module);
    }

    public List<ModuleResponse> getAllModules() {
        return moduleRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ModuleResponse> getModulesByCourseId(Long courseId) {
        return moduleRepository.findByCourseId(courseId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ModuleResponse updateModule(Long id, ModuleRequest request) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Module not found with id: " + id));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + request.getCourseId()));

        module.setTitle(request.getTitle());
        module.setOrderIndex(request.getOrderIndex());
        module.setDescription(request.getDescription());
        module.setCourse(course);

        Module updatedModule = moduleRepository.save(module);
        return toResponse(updatedModule);
    }

    public void deleteModule(Long id) {
        if (!moduleRepository.existsById(id)) {
            throw new RuntimeException("Module not found with id: " + id);
        }
        moduleRepository.deleteById(id);
    }

    private ModuleResponse toResponse(Module module) {
        ModuleResponse response = new ModuleResponse();
        response.setId(module.getId());
        response.setTitle(module.getTitle());
        response.setOrderIndex(module.getOrderIndex());
        response.setDescription(module.getDescription());
        response.setCourseTitle(module.getCourse() != null ? module.getCourse().getTitle() : null);
        return response;
    }
}