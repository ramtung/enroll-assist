package ir.proprog.enrollassist.domain.entity;

import ir.proprog.enrollassist.builder.*;
import ir.proprog.enrollassist.domain.valueobject.WeekDayEnum;
import ir.proprog.enrollassist.domain.valueobject.schedule.SectionSchedule;
import ir.proprog.enrollassist.domain.valueobject.schedule.TimeSchedule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SectionTest {
    @Test
    void sections_with_same_class_schedule_has_conflict() {
        Section math1_1 = SectionBuilder.aSection().build();
        math1_1.addClassSchedules(new ClassSchedule(WeekDayEnum.SATURDAY, new TimeSchedule(Time.valueOf("08:00:00"), Time.valueOf("10:00:00"))));
        Section math2_1 = SectionBuilder.aSection().build();
        math2_1.addClassSchedules(new ClassSchedule(WeekDayEnum.SATURDAY, new TimeSchedule(Time.valueOf("08:00:00"), Time.valueOf("10:00:00"))));

        Assertions.assertTrue(math1_1.hasClassScheduleConflict(math2_1));
    }

    @Test
    void sections_with_overlapping_class_schedule_has_conflict() {
        Section math1_1 = SectionBuilder.aSection().build();
        math1_1.addClassSchedules(new ClassSchedule(WeekDayEnum.SATURDAY, new TimeSchedule(Time.valueOf("08:00:00"), Time.valueOf("10:00:00"))));
        Section math2_1 = SectionBuilder.aSection().build();
        math2_1.addClassSchedules(new ClassSchedule(WeekDayEnum.SATURDAY, new TimeSchedule(Time.valueOf("09:00:00"), Time.valueOf("11:00:00"))));

        Assertions.assertTrue(math1_1.hasClassScheduleConflict(math2_1));
    }

    @Test
    void sections_with_one_overlapping_slot_in_class_schedule_has_conflict() {
        Section math1_1 = SectionBuilder.aSection().build();
        math1_1.addClassSchedules(new ClassSchedule(WeekDayEnum.SATURDAY, new TimeSchedule(Time.valueOf("08:00:00"), Time.valueOf("10:00:00"))),
                new ClassSchedule(WeekDayEnum.MONDAY, new TimeSchedule(Time.valueOf("09:00:00"), Time.valueOf("11:00:00"))));
        Section math2_1 = SectionBuilder.aSection().build();
        math2_1.addClassSchedules(new ClassSchedule(WeekDayEnum.SUNDAY, new TimeSchedule(Time.valueOf("08:00:00"), Time.valueOf("10:00:00"))),
                new ClassSchedule(WeekDayEnum.MONDAY, new TimeSchedule(Time.valueOf("08:00:00"), Time.valueOf("10:00:00"))));

        Assertions.assertTrue(math1_1.hasClassScheduleConflict(math2_1));
    }

    @Test
    void sections_with_different_class_schedules_does_not_have_conflict() {
        Section math1_1 = SectionBuilder.aSection().build();
        math1_1.addClassSchedules(new ClassSchedule(WeekDayEnum.SATURDAY, new TimeSchedule(Time.valueOf("08:00:00"), Time.valueOf("10:00:00"))),
                new ClassSchedule(WeekDayEnum.MONDAY, new TimeSchedule(Time.valueOf("08:00:00"), Time.valueOf("10:00:00"))));
        Section math2_1 = SectionBuilder.aSection().build();
        math2_1.addClassSchedules(new ClassSchedule(WeekDayEnum.SATURDAY, new TimeSchedule(Time.valueOf("10:00:00"), Time.valueOf("12:00:00"))),
                new ClassSchedule(WeekDayEnum.TUESDAY, new TimeSchedule(Time.valueOf("08:00:00"), Time.valueOf("10:00:00"))));

        Assertions.assertFalse(math1_1.hasClassScheduleConflict(math2_1));
    }

    @Test
    public void get_section_schedule_works() {
        Section section = SectionBuilder.aSection()
                .withSectionNo("5")
                .withCourse(CourseBuilder.aCourse()
                        .withTitle("Math1")
                        .build())
                .withClassScheduleList(ClassScheduleBuilder.builder()
                        .withTimeSchedule(TimeScheduleBuilder.builder()
                                .withFromTime("08:00:00")
                                .withToTime("12:00:00")
                                .build())
                        .build())
                .build();

        assertThat(section.getSectionSchedule(section.getClassScheduleList().get(0)))
                .isNotNull()
                .isInstanceOf(SectionSchedule.class)
                .hasToString("\t08:00:00-12:00:00\tMath1\t5");
    }

    @Test
    public void section_schedule_comparison_works() {
        Section section = SectionBuilder.aSection()
                .withSectionNo("5")
                .withCourse(CourseBuilder.aCourse().withTitle("Math1").build())
                .withClassScheduleList(
                        ClassScheduleBuilder.builder().withWeekDay(WeekDayEnum.SATURDAY)
                                .withTimeSchedule(TimeScheduleBuilder.builder().withFromTime("08:00:00").withToTime("12:00:00")
                                        .build()).build(),
                        ClassScheduleBuilder.builder().withWeekDay(WeekDayEnum.SUNDAY)
                                .withTimeSchedule(TimeScheduleBuilder.builder().withFromTime("12:00:00").withToTime("14:00:00")
                                        .build()).build(),
                        ClassScheduleBuilder.builder().withWeekDay(WeekDayEnum.MONDAY)
                                .withTimeSchedule(TimeScheduleBuilder.builder().withFromTime("08:00:00").withToTime("12:00:00")
                                        .build()).build()).build();

        assertThat(section.getSectionSchedule(section.getClassScheduleList().get(0))
                .compareTo(section.getSectionSchedule(section.getClassScheduleList().get(1))))
                .isEqualTo(-1);
        assertThat(section.getSectionSchedule(section.getClassScheduleList().get(1))
                .compareTo(section.getSectionSchedule(section.getClassScheduleList().get(2))))
                .isEqualTo(1);
        assertThat(section.getSectionSchedule(section.getClassScheduleList().get(0))
                .compareTo(section.getSectionSchedule(section.getClassScheduleList().get(2))))
                .isEqualTo(0);
    }

    @Test
    public void section_enrolls_no_more_than_its_capacity() {
        Section section = SectionBuilder.createSomeSection();
        List<Student> studentList = new ArrayList<>();
        for (int i = 0; i < section.getCapacity() + 5; i++) {
            studentList.add(StudentBuilder.createSomeStudent());
        }
        section.enrollStudents(studentList);
        assertThat(section.getStudents()).hasSize(section.getCapacity());
    }

    @Test
    public void section_enrolls_all_students_if_has_enough_capacity() {
        Section section = SectionBuilder.createSomeSection();
        List<Student> studentList = new ArrayList<>();
        for (int i = 0; i < section.getCapacity() - 5; i++) {
            studentList.add(StudentBuilder.createSomeStudent());
        }
        section.enrollStudents(studentList);
        assertThat(section.getStudents()).hasSize(studentList.size());
    }

    @Test
    public void section_enrolls_all_students_if_its_capacity_equals_the_number_of_students() {
        Section section = SectionBuilder.createSomeSection();
        List<Student> studentList = new ArrayList<>();
        for (int i = 0; i < section.getCapacity(); i++) {
            studentList.add(StudentBuilder.createSomeStudent());
        }
        section.enrollStudents(studentList);
        assertThat(section.getStudents()).hasSize(section.getCapacity());
    }
}
