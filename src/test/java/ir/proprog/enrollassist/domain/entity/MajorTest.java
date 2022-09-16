package ir.proprog.enrollassist.domain.entity;

import ir.proprog.enrollassist.builder.SectionBuilder;
import ir.proprog.enrollassist.builder.StudentBuilder;
import ir.proprog.enrollassist.domain.exception.BusinessException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Set;

import static ir.proprog.enrollassist.builder.CourseBuilder.aCourse;
import static ir.proprog.enrollassist.builder.CourseBuilder.someCourse;
import static ir.proprog.enrollassist.builder.MajorBuilder.aMajor;
import static ir.proprog.enrollassist.builder.MajorBuilder.createSomeMajorWithChart;
import static ir.proprog.enrollassist.builder.PrerequisiteRelationBuilder.aPrereqRel;
import static ir.proprog.enrollassist.builder.SectionBuilder.aSection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class MajorTest {
    @Test
    void returns_list_of_prerequisites_of_a_course_correctly() {
        Course course1 = aCourse().build();
        Course course2 = aCourse().build();
        Course pre1 = aCourse().build();
        Course pre2 = aCourse().build();
        Major major = aMajor();
        major.addToChart(new PrerequisiteRelation(major, course2, Set.of(pre1, pre2)));
        assertThat(major.getListOfPrerequisiteOfCourse(course1)).isEmpty();
        assertThat(major.getListOfPrerequisiteOfCourse(course2)).containsExactlyInAnyOrder(pre1, pre2);
        assertThat(major.getListOfPrerequisiteOfCourse(aCourse().build())).isEmpty();
    }

    @Test
    void major_detects_cycle_in_prerequisites() {
        Course course = someCourse();
        Major major = createSomeMajorWithChart();

        PrerequisiteRelation prerequisiteRelation = aPrereqRel()
                .withMajor(major)
                .withMainCourse(someCourse())
                .withPrerequisites(someCourse()).build();
        PrerequisiteRelation firstNode = aPrereqRel()
                .withMainCourse(course)
                .withMajor(major)
                .withPrerequisites(prerequisiteRelation.getMainCourse()).build();
        Course loopCourse = prerequisiteRelation.getPrerequisites().iterator().next();
        PrerequisiteRelation cycleNode = aPrereqRel()
                .withMainCourse(loopCourse)
                .withMajor(major)
                .withPrerequisites(course).build();
        major.addToChart(firstNode);
        major.addToChart(prerequisiteRelation);
        major.addToChart(cycleNode);

        assertThrows(BusinessException.class, () -> major.findCourseMakingCycleInChart(course), "There was a loop between courses, please check course number: " + loopCourse.getCourseNumber());
    }

    @Test
    void course_with_no_cycle_in_prerequisites_works_without_exception() throws BusinessException {
        Major major = createSomeMajorWithChart();
        Course course = major.getChart().get(0).getMainCourse();
        major.findCourseMakingCycleInChart(course);
        assertThatNoException();
    }

    @Test
    void course_not_in_chart_throws_exception_when_checked_for_cycles() {
        Course course = someCourse();
        Major major = createSomeMajorWithChart();
        assertThrows(BusinessException.class, () -> major.findCourseMakingCycleInChart(course), "In this major there wasn't any mainCourse with this number: " + course.getCourseNumber());
    }

    @Test
    public void major_returns_takeable_courses_correctly() {
        Course course1 = aCourse().build();
        Course course2 = aCourse().build();
        Course course3 = aCourse().build();
        Course course4 = aCourse().build();
        Major major = aMajor();
        major.addToChart(new PrerequisiteRelation(major, course1, Set.of(course2, course3)));
        major.addToChart(new PrerequisiteRelation(major, course3, Set.of(course2)));
        major.addToChart(new PrerequisiteRelation(major, course2, Collections.emptySet()));
        major.addToChart(new PrerequisiteRelation(major, course4, Collections.emptySet()));
        assertThat(major.findTakeableCourses()).containsExactlyInAnyOrder(course1, course2, course3, course4);
    }

}
