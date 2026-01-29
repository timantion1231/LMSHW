package com.tima.lms.service;

import com.tima.lms.dto.request.EnrollmentRequest;
import com.tima.lms.dto.response.EnrollmentResponse;
import com.tima.lms.entity.Enrollment;
import com.tima.lms.entity.EnrollmentStatus;
import com.tima.lms.entity.User;
import com.tima.lms.entity.Course;
import com.tima.lms.repository.EnrollmentRepository;
import com.tima.lms.repository.UserRepository;
import com.tima.lms.repository.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public EnrollmentResponse createEnrollment(EnrollmentRequest request) {
        // Проверяем, не записан ли уже студент на этот курс
        if (enrollmentRepository.existsByUserIdAndCourseId(request.getUserId(), request.getCourseId())) {
            throw new RuntimeException("User is already enrolled in this course");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + request.getCourseId()));

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollDate(LocalDate.now());
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setUser(user);
        enrollment.setCourse(course);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return toResponse(savedEnrollment);
    }

    public EnrollmentResponse getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + id));
        return toResponse(enrollment);
    }

    public List<EnrollmentResponse> getAllEnrollments() {
        return enrollmentRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EnrollmentResponse updateEnrollmentStatus(Long id, String status) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + id));

        enrollment.setStatus(EnrollmentStatus.valueOf(status.toUpperCase()));
        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return toResponse(updatedEnrollment);
    }

    public void deleteEnrollment(Long id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new RuntimeException("Enrollment not found with id: " + id);
        }
        enrollmentRepository.deleteById(id);
    }

    private EnrollmentResponse toResponse(Enrollment enrollment) {
        EnrollmentResponse response = new EnrollmentResponse();
        response.setId(enrollment.getId());
        response.setEnrollDate(enrollment.getEnrollDate() != null ? enrollment.getEnrollDate().toString() : null);
        response.setStatus(enrollment.getStatus() != null ? enrollment.getStatus().name() : null);
        response.setUserName(enrollment.getUser() != null ? enrollment.getUser().getName() : null);
        response.setCourseTitle(enrollment.getCourse() != null ? enrollment.getCourse().getTitle() : null);
        return response;
    }
}