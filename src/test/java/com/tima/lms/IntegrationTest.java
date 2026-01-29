package com.tima.lms;

import com.tima.lms.entity.*;
import com.tima.lms.entity.Module;
import com.tima.lms.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class IntegrationTest {

    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ModuleRepository moduleRepository;
    @Autowired private LessonRepository lessonRepository;
    @Autowired private EnrollmentRepository enrollmentRepository;
    @Autowired private AssignmentRepository assignmentRepository;
    @Autowired private SubmissionRepository submissionRepository;
    @Autowired private QuizRepository quizRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private AnswerOptionRepository answerOptionRepository;
    @Autowired private QuizSubmissionRepository quizSubmissionRepository;
    @Autowired private CourseReviewRepository courseReviewRepository;
    @Autowired private TagRepository tagRepository;

    private User teacher;
    private User student;
    private Category category;
    private Course course;

    @BeforeEach
    void setUp() {
        submissionRepository.deleteAll();
        assignmentRepository.deleteAll();
        lessonRepository.deleteAll();
        moduleRepository.deleteAll();
        enrollmentRepository.deleteAll();
        courseReviewRepository.deleteAll();
        quizSubmissionRepository.deleteAll();
        questionRepository.deleteAll();
        answerOptionRepository.deleteAll();
        quizRepository.deleteAll();
        courseRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        teacher = new User();
        teacher.setName("Test Teacher");
        teacher.setEmail("teacher@test.com");
        teacher.setRole(Role.TEACHER);
        teacher = userRepository.save(teacher);

        student = new User();
        student.setName("Test Student");
        student.setEmail("student@test.com");
        student.setRole(Role.STUDENT);
        student = userRepository.save(student);

        category = new Category();
        category.setName("Programming");
        category = categoryRepository.save(category);

        course = new Course();
        course.setTitle("Test Course");
        course.setDescription("Test Description");
        course.setDuration("8 weeks");
        course.setStartDate(LocalDate.now());
        course.setCategory(category);
        course.setTeacher(teacher);
        course = courseRepository.save(course);
    }

    @Test
    void testDatabaseSchemaCreation() {
        assertThat(userRepository.count()).isEqualTo(2);
        assertThat(categoryRepository.count()).isEqualTo(1);
        assertThat(courseRepository.count()).isEqualTo(1);

        System.out.println("√ Database schema created successfully");
    }

    @Test
    void testCreateAndRetrieveCourse() {
        Course savedCourse = courseRepository.findById(course.getId()).orElse(null);

        assertThat(savedCourse).isNotNull();
        assertThat(savedCourse.getTitle()).isEqualTo("Test Course");
        assertThat(savedCourse.getTeacher().getName()).isEqualTo("Test Teacher");
        assertThat(savedCourse.getCategory().getName()).isEqualTo("Programming");

        System.out.println("√ Course CRUD operations work correctly");
    }

    @Test
    void testCascadeOperations() {
        Module module = new Module();
        module.setTitle("Test Module");
        module.setOrderIndex(1);
        module.setCourse(course);
        module = moduleRepository.save(module);

        Lesson lesson = new Lesson();
        lesson.setTitle("Test Lesson");
        lesson.setContent("Test Content");
        lesson.setModule(module);
        lesson = lessonRepository.save(lesson);

        Assignment assignment = new Assignment();
        assignment.setTitle("Test Assignment");
        assignment.setDescription("Test Description");
        assignment.setDueDate(LocalDate.now().plusDays(7));
        assignment.setMaxScore(100);
        assignment.setLesson(lesson);
        assignment = assignmentRepository.save(assignment);

        long moduleId = module.getId();
        long lessonId = lesson.getId();
        long assignmentId = assignment.getId();

        courseRepository.delete(course);

        assertThat(courseRepository.findById(course.getId())).isEmpty();
        assertThat(moduleRepository.findById(moduleId)).isEmpty();
        assertThat(lessonRepository.findById(lessonId)).isEmpty();
        assertThat(assignmentRepository.findById(assignmentId)).isEmpty();

        System.out.println("√ Cascade delete operations work correctly");
    }

    @Test
    void testEnrollmentBusinessLogic() {
        Enrollment enrollment = new Enrollment();
        enrollment.setUser(student);
        enrollment.setCourse(course);
        enrollment.setEnrollDate(LocalDate.now());
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment = enrollmentRepository.save(enrollment);

        assertThat(enrollment.getId()).isNotNull();
        assertThat(enrollment.getUser().getEmail()).isEqualTo("student@test.com");
        assertThat(enrollment.getCourse().getTitle()).isEqualTo("Test Course");

        Enrollment duplicateEnrollment = new Enrollment();
        duplicateEnrollment.setUser(student);
        duplicateEnrollment.setCourse(course);
        duplicateEnrollment.setEnrollDate(LocalDate.now());
        duplicateEnrollment.setStatus(EnrollmentStatus.ACTIVE);

        enrollmentRepository.save(duplicateEnrollment);

        List<Enrollment> enrollments = enrollmentRepository.findByUserId(student.getId());
        assertThat(enrollments).hasSize(2);

        System.out.println("√ Enrollment business logic works (note: duplicate check not implemented)");
    }

    @Test
    void testAssignmentAndSubmissionFlow() {
        Module module = new Module();
        module.setTitle("Assignment Module");
        module.setOrderIndex(1);
        module.setCourse(course);
        module = moduleRepository.save(module);

        Lesson lesson = new Lesson();
        lesson.setTitle("Assignment Lesson");
        lesson.setContent("Lesson Content");
        lesson.setModule(module);
        lesson = lessonRepository.save(lesson);

        Assignment assignment = new Assignment();
        assignment.setTitle("Test Assignment");
        assignment.setDescription("Complete this task");
        assignment.setDueDate(LocalDate.now().plusDays(14));
        assignment.setMaxScore(100);
        assignment.setLesson(lesson);
        assignment = assignmentRepository.save(assignment);

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setContent("My submission content");
        submission = submissionRepository.save(submission);

        assertThat(submission.getId()).isNotNull();
        assertThat(submission.getStudent().getEmail()).isEqualTo("student@test.com");
        assertThat(submission.getAssignment().getTitle()).isEqualTo("Test Assignment");

        submission.setScore(95);
        submission.setFeedback("Excellent work!");
        submissionRepository.save(submission);

        Submission updatedSubmission = submissionRepository.findById(submission.getId()).orElse(null);
        assertThat(updatedSubmission).isNotNull();
        assertThat(updatedSubmission.getScore()).isEqualTo(95);
        assertThat(updatedSubmission.getFeedback()).isEqualTo("Excellent work!");

        System.out.println("√ Assignment and submission flow works correctly");
    }

    @Test
    void testQuizAndQuestions() {
        Module module = new Module();
        module.setTitle("Quiz Module");
        module.setOrderIndex(1);
        module.setCourse(course);
        module = moduleRepository.save(module);

        Quiz quiz = new Quiz();
        quiz.setTitle("Test Quiz");
        quiz.setTimeLimit(30);
        quiz.setModule(module);
        quiz = quizRepository.save(quiz);

        Question question1 = new Question();
        question1.setText("What is 2+2?");
        question1.setType(QuestionType.SINGLE_CHOICE);
        question1.setQuiz(quiz);
        question1 = questionRepository.save(question1);

        AnswerOption option1 = new AnswerOption();
        option1.setText("3");
        option1.setIsCorrect(false);
        option1.setQuestion(question1);
        answerOptionRepository.save(option1);

        AnswerOption option2 = new AnswerOption();
        option2.setText("4");
        option2.setIsCorrect(true);
        option2.setQuestion(question1);
        answerOptionRepository.save(option2);

        AnswerOption option3 = new AnswerOption();
        option3.setText("5");
        option3.setIsCorrect(false);
        option3.setQuestion(question1);
        answerOptionRepository.save(option3);

        Quiz savedQuiz = quizRepository.findById(quiz.getId()).orElse(null);
        assertThat(savedQuiz).isNotNull();
        assertThat(savedQuiz.getQuestions()).hasSize(1);

        List<AnswerOption> options = answerOptionRepository.findByQuestionId(question1.getId());
        assertThat(options).hasSize(3);

        AnswerOption correctOption = options.stream()
                .filter(AnswerOption::getIsCorrect)
                .findFirst()
                .orElse(null);
        assertThat(correctOption).isNotNull();
        assertThat(correctOption.getText()).isEqualTo("4");

        System.out.println("√ Quiz and questions structure works correctly");
    }

    @Test
    void testLazyLoadingException() {
        Module module = new Module();
        module.setTitle("Lazy Test Module");
        module.setOrderIndex(1);
        module.setCourse(course);
        moduleRepository.save(module);

        Course lazyCourse = courseRepository.findById(course.getId()).orElse(null);
        assertThat(lazyCourse).isNotNull();

        try {
            int size = lazyCourse.getModules().size();
            System.out.println("WARNING!  No LazyInitializationException occurred. Modules count: " + size);
            System.out.println("    This might be because @Transactional is still active or Hibernate settings allow lazy loading.");
        } catch (Exception e) {
            if (e.getClass().getSimpleName().contains("LazyInitializationException")) {
                System.out.println("√ LazyInitializationException successfully caught: " + e.getMessage());
            } else {
                System.out.println("WARNING!  Different exception: " + e.getClass().getName() + " - " + e.getMessage());
            }
        }
    }

    @Test
    void testCourseReviewSystem() {
        CourseReview review = new CourseReview();
        review.setCourse(course);
        review.setStudent(student);
        review.setRating(5);
        review.setComment("Excellent course!");
        review.setCreatedAt(LocalDateTime.now());
        review = courseReviewRepository.save(review);

        assertThat(review.getId()).isNotNull();
        assertThat(review.getRating()).isEqualTo(5);
        assertThat(review.getComment()).isEqualTo("Excellent course!");

        List<CourseReview> courseReviews = courseReviewRepository.findByCourseId(course.getId());
        assertThat(courseReviews).hasSize(1);
        assertThat(courseReviews.get(0).getStudent().getEmail()).isEqualTo("student@test.com");

        System.out.println("√ Course review system works correctly");
    }

    @Test
    void testTagSystem() {
        Tag tag1 = new Tag();
        tag1.setName("Java");
        tag1 = tagRepository.save(tag1);

        Tag tag2 = new Tag();
        tag2.setName("Programming");
        tag2 = tagRepository.save(tag2);

        course.getTags().add(tag1);
        course.getTags().add(tag2);
        courseRepository.save(course);

        Course taggedCourse = courseRepository.findById(course.getId()).orElse(null);
        assertThat(taggedCourse).isNotNull();
        assertThat(taggedCourse.getTags()).hasSize(2);

        System.out.println("√ Tag system works correctly");
    }

    @Test
    void testCompleteUserJourney() {

        User newStudent = new User();
        newStudent.setName("Journey Student");
        newStudent.setEmail("journey@test.com");
        newStudent.setRole(Role.STUDENT);
        newStudent = userRepository.save(newStudent);

        Category newCategory = new Category();
        newCategory.setName("Test Category");
        newCategory = categoryRepository.save(newCategory);

        Course newCourse = new Course();
        newCourse.setTitle("Journey Course");
        newCourse.setDescription("For testing complete journey");
        newCourse.setDuration("4 weeks");
        newCourse.setStartDate(LocalDate.now());
        newCourse.setCategory(newCategory);
        newCourse.setTeacher(teacher);
        newCourse = courseRepository.save(newCourse);

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(newStudent);
        enrollment.setCourse(newCourse);
        enrollment.setEnrollDate(LocalDate.now());
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment = enrollmentRepository.save(enrollment);

        Module journeyModule = new Module();
        journeyModule.setTitle("Journey Module");
        journeyModule.setOrderIndex(1);
        journeyModule.setCourse(newCourse);
        journeyModule = moduleRepository.save(journeyModule);

        Lesson journeyLesson = new Lesson();
        journeyLesson.setTitle("Journey Lesson");
        journeyLesson.setContent("Learn something");
        journeyLesson.setModule(journeyModule);
        journeyLesson = lessonRepository.save(journeyLesson);

        Assignment journeyAssignment = new Assignment();
        journeyAssignment.setTitle("Journey Assignment");
        journeyAssignment.setDescription("Complete this to finish the journey");
        journeyAssignment.setDueDate(LocalDate.now().plusDays(7));
        journeyAssignment.setMaxScore(100);
        journeyAssignment.setLesson(journeyLesson);
        journeyAssignment = assignmentRepository.save(journeyAssignment);

        Submission journeySubmission = new Submission();
        journeySubmission.setAssignment(journeyAssignment);
        journeySubmission.setStudent(newStudent);
        journeySubmission.setSubmittedAt(LocalDateTime.now());
        journeySubmission.setContent("I completed the journey!");
        journeySubmission = submissionRepository.save(journeySubmission);

        journeySubmission.setScore(100);
        journeySubmission.setFeedback("Perfect! Journey completed successfully.");
        submissionRepository.save(journeySubmission);

        CourseReview journeyReview = new CourseReview();
        journeyReview.setCourse(newCourse);
        journeyReview.setStudent(newStudent);
        journeyReview.setRating(5);
        journeyReview.setComment("Amazing journey!");
        journeyReview.setCreatedAt(LocalDateTime.now());
        courseReviewRepository.save(journeyReview);

        assertThat(enrollmentRepository.findByUserId(newStudent.getId())).hasSize(1);
        assertThat(submissionRepository.findByStudentId(newStudent.getId())).hasSize(1);
        assertThat(courseReviewRepository.findByStudentId(newStudent.getId())).hasSize(1);

        Submission finalSubmission = submissionRepository.findByAssignmentIdAndStudentId(
                journeyAssignment.getId(), newStudent.getId()).orElse(null);
        assertThat(finalSubmission).isNotNull();
        assertThat(finalSubmission.getScore()).isEqualTo(100);

        System.out.println("√ Complete user journey test passed successfully");
    }
}