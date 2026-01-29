package com.tima.lms.service;

import com.tima.lms.dto.request.SubmissionRequest;
import com.tima.lms.dto.response.SubmissionResponse;
import com.tima.lms.entity.Submission;
import com.tima.lms.entity.Assignment;
import com.tima.lms.entity.User;
import com.tima.lms.repository.SubmissionRepository;
import com.tima.lms.repository.AssignmentRepository;
import com.tima.lms.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    public SubmissionResponse createSubmission(SubmissionRequest request) {
        if (submissionRepository.findByAssignmentIdAndStudentId(
                request.getAssignmentId(), request.getStudentId()).isPresent()) {
            throw new RuntimeException("Student already submitted this assignment");
        }

        Assignment assignment = assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new RuntimeException("Assignment not found with id: " + request.getAssignmentId()));

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + request.getStudentId()));

        Submission submission = new Submission();
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setContent(request.getContent());
        submission.setAssignment(assignment);
        submission.setStudent(student);

        Submission savedSubmission = submissionRepository.save(submission);
        return toResponse(savedSubmission);
    }

    public SubmissionResponse getSubmissionById(Long id) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found with id: " + id));
        return toResponse(submission);
    }

    public List<SubmissionResponse> getAllSubmissions() {
        return submissionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public SubmissionResponse updateSubmissionGrade(Long id, Integer score, String feedback) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found with id: " + id));

        submission.setScore(score);
        submission.setFeedback(feedback);

        Submission updatedSubmission = submissionRepository.save(submission);
        return toResponse(updatedSubmission);
    }

    public void deleteSubmission(Long id) {
        if (!submissionRepository.existsById(id)) {
            throw new RuntimeException("Submission not found with id: " + id);
        }
        submissionRepository.deleteById(id);
    }

    private SubmissionResponse toResponse(Submission submission) {
        SubmissionResponse response = new SubmissionResponse();
        response.setId(submission.getId());
        response.setSubmittedAt(submission.getSubmittedAt() != null ? submission.getSubmittedAt().toString() : null);
        response.setContent(submission.getContent());
        response.setScore(submission.getScore());
        response.setFeedback(submission.getFeedback());
        response.setAssignmentTitle(submission.getAssignment() != null ? submission.getAssignment().getTitle() : null);
        response.setStudentName(submission.getStudent() != null ? submission.getStudent().getName() : null);
        return response;
    }
}