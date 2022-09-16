package ir.proprog.enrollassist.domain.entity;

import ir.proprog.enrollassist.builder.CourseBuilder;
import ir.proprog.enrollassist.domain.entity.Course;
import ir.proprog.enrollassist.domain.entity.Major;
import ir.proprog.enrollassist.domain.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CourseTest {
    @Test
    void students_can_take_a_course_without_prerequisites() {
        Course math1 = CourseBuilder.someCourse();

        Student bebe = mock(Student.class);
        Major major = mock(Major.class);
        when(major.getListOfPrerequisiteOfCourse(math1)).thenReturn(Collections.emptyList());
        when(bebe.getMajor()).thenReturn(major);
        when(bebe.hasPassed(any(Course.class))).thenReturn(false);
        assertThat(math1.prerequisitesNotPassedBy(bebe))
                .isNotNull()
                .isEmpty();
    }

    @Test
    void students_can_take_a_course_with_one_prerequisite_if_has_passed_prerequisite() {
        Course math1 = CourseBuilder.someCourse();
        Course math2 = CourseBuilder.someCourse();

        Student bebe = mock(Student.class);
        Major major = mock(Major.class);

        when(major.getListOfPrerequisiteOfCourse(math2)).thenReturn(List.of(math1));
        when(bebe.getMajor()).thenReturn(major);
        when(bebe.hasPassed(math1)).thenReturn(true);
        assertThat(math2.prerequisitesNotPassedBy(bebe))
                .isNotNull()
                .isEmpty();
    }

    @Test
    void students_can_take_a_course_with_two_prerequisites_if_has_passed_both() {
        Course math1 = CourseBuilder.someCourse();
        Course phys1 = CourseBuilder.someCourse();
        Course phys2 = CourseBuilder.someCourse();

        Student bebe = mock(Student.class);
        Major major = mock(Major.class);

        when(major.getListOfPrerequisiteOfCourse(phys2)).thenReturn(List.of(math1, phys1));
        when(bebe.getMajor()).thenReturn(major);
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.hasPassed(phys1)).thenReturn(true);
        assertThat(phys2.prerequisitesNotPassedBy(bebe))
                .isNotNull()
                .isEmpty();
    }

    @Test
    void students_cannot_take_a_course_if_has_not_passed_one_prerequisite() {
        Course math1 = CourseBuilder.someCourse();
        Course phys1 = CourseBuilder.someCourse();
        Course phys2 = CourseBuilder.someCourse();

        Student bebe = mock(Student.class);
        Major major = mock(Major.class);

        when(major.getListOfPrerequisiteOfCourse(phys2)).thenReturn(List.of(math1, phys1));
        when(bebe.getMajor()).thenReturn(major);
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.hasPassed(phys1)).thenReturn(false);
        assertThat(phys2.prerequisitesNotPassedBy(bebe))
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void students_cannot_take_a_course_if_has_not_passed_none_of_the_prerequisites() {
        Course math1 = CourseBuilder.someCourse();
        Course phys1 = CourseBuilder.someCourse();
        Course phys2 = CourseBuilder.someCourse();

        Student bebe = mock(Student.class);
        Major major = mock(Major.class);

        when(major.getListOfPrerequisiteOfCourse(phys2)).thenReturn(List.of(math1, phys1));
        when(bebe.getMajor()).thenReturn(major);

        when(bebe.hasPassed(math1)).thenReturn(false);
        when(bebe.hasPassed(phys1)).thenReturn(false);
        assertThat(phys2.prerequisitesNotPassedBy(bebe))
                .isNotNull()
                .hasSize(2);
    }
}