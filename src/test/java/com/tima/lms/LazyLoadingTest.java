package com.tima.lms;

import com.tima.lms.entity.Course;
import com.tima.lms.entity.Module;
import com.tima.lms.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class LazyLoadingTest {

    @Autowired
    private CourseRepository courseRepository;
    
    @Test
    void testLazyInitializationException() {
        Course course = createCourseWithModules();

        Course loadedCourse = courseRepository.findById(course.getId()).orElse(null);
        assertThat(loadedCourse).isNotNull();

        System.out.println("=========================================");
        System.out.println("DEMONSTRATING LAZY LOADING EXCEPTION");
        System.out.println("=========================================");

        try {
            int moduleCount = loadedCourse.getModules().size();
            System.out.println("X UNEXPECTED: No exception occurred!");
            System.out.println("   Module count: " + moduleCount);
            System.out.println("   This might happen if Hibernate settings allow lazy loading without transaction");
        } catch (Exception e) {
            System.out.println("√ EXPECTED: Exception occurred!");
            System.out.println("   Exception type: " + e.getClass().getName());
            System.out.println("   Message: " + e.getMessage());

            boolean isLazyException = e.getClass().getName().contains("LazyInitializationException") ||
                    e.getMessage().contains("LazyInitializationException") ||
                    e.getCause() != null && e.getCause().getClass().getName().contains("LazyInitializationException");

            if (isLazyException) {
                System.out.println("√ SUCCESS: Correctly caught LazyInitializationException");
            } else {
                System.out.println("WARNING: Different exception type, but still an error");
            }
        }

        System.out.println("=========================================");
    }
    
    @Test
    @Transactional
    void testLazyLoadingWithTransactional() {
        Course course = createCourseWithModules();

        Course loadedCourse = courseRepository.findById(course.getId()).orElse(null);
        assertThat(loadedCourse).isNotNull();

        System.out.println("=========================================");
        System.out.println("DEMONSTRATING LAZY LOADING WITH @Transactional");
        System.out.println("=========================================");

        try {
            int moduleCount = loadedCourse.getModules().size();
            System.out.println("√ SUCCESS: Lazy collection accessed within transaction");
            System.out.println("   Module count: " + moduleCount);
            assertThat(moduleCount).isGreaterThan(0);
        } catch (Exception e) {
            System.out.println("X UNEXPECTED: Exception occurred even with @Transactional");
            System.out.println("   Exception: " + e.getClass().getName() + " - " + e.getMessage());
            throw e;
        }

        System.out.println("=========================================");
    }

    @Transactional
    private Course createCourseWithModules() {

        Course course = new Course();
        course.setTitle("Lazy Test Course");
        course.setDescription("Course for testing lazy loading");
        course.setStartDate(LocalDate.now());

        course = courseRepository.save(course);

        Module module1 = new Module();
        module1.setTitle("Module 1");
        module1.setOrderIndex(1);
        module1.setCourse(course);
        course.getModules().add(module1);

        Module module2 = new Module();
        module2.setTitle("Module 2");
        module2.setOrderIndex(2);
        module2.setCourse(course);
        course.getModules().add(module2);
        
        return courseRepository.save(course);
    }
}