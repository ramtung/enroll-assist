package ir.proprog.enrollassist.domain.entity;

import ir.proprog.enrollassist.domain.valueobject.schedule.ExamSchedule;
import ir.proprog.enrollassist.domain.violation.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static ir.proprog.enrollassist.builder.CourseBuilder.aCourse;
import static ir.proprog.enrollassist.builder.EnrollmentListBuilder.anEnrollmentList;
import static ir.proprog.enrollassist.builder.ExamScheduleBuilder.anExamSchedule;
import static ir.proprog.enrollassist.builder.SectionBuilder.aSection;
import static ir.proprog.enrollassist.builder.StudentBuilder.aStudent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EnrollmentListTest {
    @Test
    void enrollment_list_checks_prerequisites_correctly() {
        Student student = aStudent().build();

        Course course1 = mock(Course.class);
        Course course2 = mock(Course.class);
        Course pre1 = aCourse().build();
        when(course1.prerequisitesNotPassedBy(student)).thenReturn(Collections.emptyList());
        when(course2.prerequisitesNotPassedBy(student)).thenReturn(List.of(new PrerequisiteNotTaken(course2, pre1)));

        Section section1 = aSection().withCourse(course1).build();
        Section section2 = aSection().withCourse(course2).build();

        EnrollmentList enrollmentList = anEnrollmentList().withOwner(student).build();
        enrollmentList.addSections(section1, section2);

        assertThat(enrollmentList.checkHasPassedAllPrerequisites())
                .hasOnlyElementsOfType(PrerequisiteNotTaken.class);
    }

    @Test
    void enrollment_list_checks_duplicate_sections_correctly() {
        Student student = aStudent().build();

        Course course1 = mock(Course.class);
        Section section1 = aSection().withCourse(course1).build();
        Section section2 = aSection().withCourse(course1).build();

        EnrollmentList enrollmentList = anEnrollmentList().withOwner(student).build();
        enrollmentList.addSections(section1, section2);

        assertThat(enrollmentList.checkNoCourseHasRequestedTwice())
                .hasOnlyElementsOfType(CourseRequestedTwice.class);
    }

    @Test
    void enrollment_list_checks_courses_have_not_already_passed_correctly() {
        Student student = mock(Student.class);
        Course course = aCourse().build();
        Section section = aSection().withCourse(course).build();
        when(student.hasPassed(course)).thenReturn(true);

        EnrollmentList enrollmentList = anEnrollmentList().withOwner(student).build();
        enrollmentList.addSections(section);

        assertThat(enrollmentList.checkHasNotAlreadyPassedCourses())
                .hasOnlyElementsOfType(RequestedCourseAlreadyPassed.class);
    }

    @Test
    void enrollment_list_checks_GPA_limit_correctly() {
        Student student = spy(aStudent().build());

        EnrollmentList enrollmentList = anEnrollmentList().withOwner(student).build();
        when(student.isNumberOfUnitsAllowed(any(Integer.class))).thenReturn(false);

        assertThat(enrollmentList.checkGpaLimit())
                .hasOnlyElementsOfType(GpaLimitViolation.class);
    }

    @Test
    void enrollment_list_checks_section_schedule_conflicts_correctly() {
        Student student = aStudent().build();
        Section section1 = mock(Section.class);
        Section section2 = mock(Section.class);
        when(section1.hasClassScheduleConflict(section2)).thenReturn(true);
        when(section2.hasClassScheduleConflict(section1)).thenReturn(true);

        EnrollmentList enrollmentList = anEnrollmentList().withOwner(student).build();
        enrollmentList.addSections(section1, section2);

        assertThat(enrollmentList.checkConflictForSectionSchedule())
                .hasOnlyElementsOfType(SectionConflict.class);
    }

    @Test
    void enrollment_list_checks_section_exam_conflicts_correctly() {
        Student student = aStudent().build();
        ExamSchedule examSchedule = anExamSchedule();
        Section section1 = aSection().withExamSchedule(examSchedule).build();
        Section section2 = aSection().withExamSchedule(examSchedule).build();

        EnrollmentList enrollmentList = anEnrollmentList().withOwner(student).build();
        enrollmentList.addSections(section1, section2);

        assertThat(enrollmentList.checkConflictOnExamSchedule())
                .hasOnlyElementsOfType(SectionConflict.class);
    }

    @Test
    void enrollment_list_calculates_sum_of_credits_correctly() {
        EnrollmentList enrollmentList = anEnrollmentList().build();
        enrollmentList.addSections(
                aSection().withCourse(aCourse().withCredits(3).build()).build(),
                aSection().withCourse(aCourse().withCredits(2).build()).build(),
                aSection().withCourse(aCourse().withCredits(0).build()).build()
        );

        assertThat(enrollmentList.sumOfCredits()).isEqualTo(5);
    }

}
