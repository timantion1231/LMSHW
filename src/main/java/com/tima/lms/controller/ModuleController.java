package com.tima.lms.controller;

import com.tima.lms.dto.request.ModuleRequest;
import com.tima.lms.dto.response.ModuleResponse;
import com.tima.lms.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class ModuleController {
    private final ModuleService moduleService;

    @PostMapping
    public ResponseEntity<ModuleResponse> createModule(@RequestBody ModuleRequest request) {
        ModuleResponse response = moduleService.createModule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuleResponse> getModule(@PathVariable Long id) {
        ModuleResponse response = moduleService.getModuleById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ModuleResponse>> getAllModules() {
        List<ModuleResponse> responses = moduleService.getAllModules();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/by-course/{courseId}")
    public ResponseEntity<List<ModuleResponse>> getModulesByCourse(@PathVariable Long courseId) {
        List<ModuleResponse> responses = moduleService.getModulesByCourseId(courseId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModuleResponse> updateModule(
            @PathVariable Long id,
            @RequestBody ModuleRequest request) {
        ModuleResponse response = moduleService.updateModule(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.noContent().build();
    }
}