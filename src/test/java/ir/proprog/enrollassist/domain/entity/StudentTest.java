package ir.proprog.enrollassist.domain.entity;

import ir.proprog.enrollassist.builder.SectionBuilder;
import ir.proprog.enrollassist.domain.entity.*;
import ir.proprog.enrollassist.domain.valueobject.Grade;
import ir.proprog.enrollassist.repository.MajorRepository;
import ir.proprog.enrollassist.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static ir.proprog.enrollassist.builder.CourseBuilder.aCourse;
import static ir.proprog.enrollassist.builder.CourseBuilder.someCourse;
import static ir.proprog.enrollassist.builder.MajorBuilder.aMajor;
import static ir.proprog.enrollassist.builder.SectionBuilder.aSection;
import static ir.proprog.enrollassist.builder.StudentBuilder.aStudent;
import static ir.proprog.enrollassist.builder.StudentBuilder.record;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class StudentTest {
    @MockBean
    StudentRepository mockStudentRepository;
    @MockBean
    MajorRepository mockMajorRepository;

    @Test
    public void student_with_no_study_records_has_passed_nothing() {
        Course course = someCourse();
        Student bebe = aStudent().build();
        assertFalse(bebe.hasPassed(course));
    }

    @Test
    public void student_has_not_passed_a_course_if_has_not_taken_it() {
        Course takenCourse = someCourse();
        Course notTakenCourse = someCourse();
        Student bebe = aStudent().
                withTranscript(
                    record("t1", takenCourse, 10)
                ).build();
        assertFalse(bebe.hasPassed(notTakenCourse));
    }

    @Test
    public void student_has_passed_a_course_if_has_a_passing_record() {
        Course passedCourse = someCourse();
        Course failedCourse = someCourse();
        Student bebe = aStudent().
                withTranscript(
                        record("t1", passedCourse, 10),
                        record("t1", failedCourse, 2)
                ).build();
        assertTrue(bebe.hasPassed(passedCourse));
    }

    @Test
    public void student_has_passed_a_course_if_got_pass_grade_second_time() {
        Course passedCourse = someCourse();
        Course failedCourse = someCourse();
        Student bebe = aStudent().
                withTranscript(
                        record("t1", passedCourse, 10),
                        record("t1", failedCourse, 2),
                        record("t2", failedCourse, 10.1)
                ).build();
        assertTrue(bebe.hasPassed(passedCourse));
    }

    @Test
    public void student_with_no_study_records_has_zero_GPA() {
        Student bebe = aStudent().build();
        assertEquals(bebe.calculateGPA(), Grade.ZERO);
    }

    @Test
    public void student_with_study_records_in_two_terms_has_correct_GPA() {
        Course course = someCourse();
        Student bebe = aStudent().
                withTranscript(
                        record("t1", someCourse(), 10),
                        record("t1", course, 5.6),
                        record("t2", course, 12.1),
                        record("t2", aCourse().withCredits(2).build(), 15)
                ).build();
        assertEquals(bebe.calculateGPA(), new Grade(10.28));
    }

    @Test
    public void student_has_passed_the_prerequisites_of_a_course_with_no_prerequisites() {
        Student bebe = aStudent().build();
        Course math1 = aCourse().build();
        assertTrue(bebe.hasPassedPrerequisites(math1));
    }

    @Test
    public void student_has_passed_prerequisites_of_a_course_with_one_prerequisite() {
        Course pre1 = aCourse().build();
        Course math1 = aCourse().build();

        Major major = mock(Major.class);
        when(major.getListOfPrerequisiteOfCourse(math1)).thenReturn(List.of(pre1));
        Student bebe = spy(aStudent().withMajor(major).build());

        when(bebe.hasPassed(pre1)).thenReturn(true);
        assertTrue(bebe.hasPassedPrerequisites(math1));
    }

    @Test
    public void student_has_not_passed_all_prerequisites_of_a_course_with_three_prerequisite() {
        Course pre1 = aCourse().build();
        Course pre2 = aCourse().build();
        Course pre3 = aCourse().build();
        Course math1 = aCourse().build();

        Major major = mock(Major.class);
        when(major.getListOfPrerequisiteOfCourse(math1)).thenReturn(List.of(pre1, pre2, pre3));
        Student bebe = spy(aStudent().withMajor(major).build());

        when(bebe.hasPassed(pre1)).thenReturn(true);
        when(bebe.hasPassed(pre2)).thenReturn(true);
        when(bebe.hasPassed(pre3)).thenReturn(false);
        assertFalse(bebe.hasPassedPrerequisites(math1));
    }

    @Test
    public void student_correctly_returns_list_of_takeable_courses() {
        Course course1 = aCourse().build();
        Course course2 = aCourse().build();
        Course course3 = aCourse().build();
        Course course4 = aCourse().build();
        Major major = aMajor();
        major.addToChart(new PrerequisiteRelation(major, course1, Set.of(course2, course3)));
        major.addToChart(new PrerequisiteRelation(major, course3, Set.of(course2)));
        major.addToChart(new PrerequisiteRelation(major, course2, Collections.emptySet()));
        major.addToChart(new PrerequisiteRelation(major, course4, Collections.emptySet()));

        Student student = spy(aStudent().withMajor(major).build());
        when(student.hasPassed(course1)).thenReturn(false);
        when(student.hasPassed(course2)).thenReturn(true);
        when(student.hasPassed(course3)).thenReturn(false);
        when(student.hasPassed(course4)).thenReturn(false);

        assertThat(student.getListOfTakeableCourses()).containsExactlyInAnyOrder(course3, course4);
    }

    @Test
    public void student_correctly_returns_list_of_takeable_section() {
        Course course1 = aCourse().build();
        Course course2 = aCourse().build();
        Course course3 = aCourse().build();
        Course course4 = aCourse().build();
        Major major = aMajor();
        major.addToChart(new PrerequisiteRelation(major, course1, Set.of(course2, course3)));
        major.addToChart(new PrerequisiteRelation(major, course3, Set.of(course2)));
        major.addToChart(new PrerequisiteRelation(major, course2, Collections.emptySet()));
        major.addToChart(new PrerequisiteRelation(major, course4, Collections.emptySet()));
        Section section1_1 = aSection().withCourse(course1).build();
        Section section1_2 = aSection().withCourse(course1).build();
        Section section2_1 = aSection().withCourse(course2).build();
        Section section2_2 = aSection().withCourse(course2).build();
        Section section3_1 = aSection().withCourse(course3).build();
        Section section3_2 = aSection().withCourse(course3).build();

        Student student = spy(aStudent().withMajor(major).build());
        when(student.hasPassed(course1)).thenReturn(false);
        when(student.hasPassed(course2)).thenReturn(true);
        when(student.hasPassed(course3)).thenReturn(false);
        when(student.hasPassed(course4)).thenReturn(false);

        assertThat(student.sectionsThatCanTake(Set.of(section1_1, section1_2, section2_1, section2_2, section3_1, section3_2)))
                .containsExactlyInAnyOrder(section3_1, section3_2);
    }
}
