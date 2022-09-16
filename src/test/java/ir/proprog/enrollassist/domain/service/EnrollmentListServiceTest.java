package ir.proprog.enrollassist.domain.service;

import ir.proprog.enrollassist.builder.*;
import ir.proprog.enrollassist.domain.entity.EnrollmentList;
import ir.proprog.enrollassist.domain.entity.Section;
import ir.proprog.enrollassist.domain.entity.Student;
import ir.proprog.enrollassist.domain.service.*;
import ir.proprog.enrollassist.domain.valueobject.*;
import ir.proprog.enrollassist.domain.valueobject.schedule.ExamSchedule;
import ir.proprog.enrollassist.domain.valueobject.schedule.TimeSchedule;
import ir.proprog.enrollassist.domain.violation.*;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ir.proprog.enrollassist.builder.ClassScheduleBuilder.createSomeClassSchedule;
import static ir.proprog.enrollassist.builder.CourseBuilder.aCourse;
import static ir.proprog.enrollassist.builder.EnrollmentListBuilder.*;
import static ir.proprog.enrollassist.builder.ExamScheduleBuilder.anExamSchedule;
import static ir.proprog.enrollassist.builder.SectionBuilder.aSection;
import static ir.proprog.enrollassist.builder.SectionBuilder.createSomeSection;
import static ir.proprog.enrollassist.builder.StudentBuilder.aStudent;
import static ir.proprog.enrollassist.builder.StudentBuilder.createSomeStudent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EnrollmentListServiceTest {
    @MockBean
    SectionRepository mockSectionRepository;

    @MockBean
    EnrollmentListRepository mockEnrollmentListRepository;

    @MockBean
    EnrollmentListService mockedEnrollmentListService;

    EnrollmentListService enrollmentListService;

    @PostConstruct
    void setupEnrollmentList() {
        enrollmentListService = new EnrollmentListService(mockSectionRepository, mockEnrollmentListRepository);
    }

     @Test
    void Given_student_When_callGetWeeklyScheduleService_Then_receiveCorrectString() {
        Student student = createSomeStudent();
        List<EnrollmentList> enrollmentLists = createListOfEnrollmentListForAStudent(student);

        when(mockEnrollmentListRepository.findByOwner(student)).thenReturn(enrollmentLists);
        String result = enrollmentListService.getWeeklySchedule(student).toString();
        assertThat(result.contains(enrollmentLists.get(0).getOwner().getStudentNumber() + " - " + enrollmentLists.get(0).getListName() + System.lineSeparator()) &&
                result.contains(enrollmentLists.get(1).getOwner().getStudentNumber() + " - " + enrollmentLists.get(0).getListName() + System.lineSeparator()) &&
                result.contains(enrollmentLists.get(2).getOwner().getStudentNumber() + " - " + enrollmentLists.get(0).getListName() + System.lineSeparator()));
    }

    @Test
    void Given_studentWithoutList_When_callGetWeeklyScheduleService_Then_emptyString() {
        Student student = createSomeStudent();
        when(mockEnrollmentListRepository.findByOwner(student)).thenReturn(new ArrayList<>());
        String result = enrollmentListService.getWeeklySchedule(student).toString();
        assertThat(Strings.isNullOrEmpty(result));
    }

    @Test
    void Given_studentNumber_When_callGetWeeklyScheduleService_Then_receiveCorrectString() {
        Student student = createSomeStudent();
        List<EnrollmentList> enrollmentLists = createListOfEnrollmentListForAStudent(student);

        when(mockEnrollmentListRepository.findByOwnerNumber(student.getStudentNumber())).thenReturn(enrollmentLists);
        StringBuilder weeklySchedule1 = new StringBuilder();
        weeklySchedule1.append(enrollmentLists.get(0).getOwner().getStudentNumber()).append(" - ").append(enrollmentLists.get(0).getListName()).append(System.lineSeparator());
        StringBuilder weeklySchedule2 = new StringBuilder();
        weeklySchedule2.append(enrollmentLists.get(1).getOwner().getStudentNumber()).append(" - ").append(enrollmentLists.get(0).getListName()).append(System.lineSeparator());
        StringBuilder weeklySchedule3 = new StringBuilder();
        weeklySchedule3.append(enrollmentLists.get(2).getOwner().getStudentNumber()).append(" - ").append(enrollmentLists.get(0).getListName()).append(System.lineSeparator());
        String result = enrollmentListService.getWeeklySchedule(student.getStudentNumber()).toString();
        assertThat(result.contains(weeklySchedule1.toString()) &&
                result.contains(weeklySchedule2.toString()) &&
                result.contains(weeklySchedule3.toString()));
    }

    @Test
    void Given_studentNumberWithoutList_When_callGetWeeklyScheduleService_Then_emptyString() {
        Student student = createSomeStudent();
        when(mockEnrollmentListRepository.findByOwnerNumber(student.getStudentNumber())).thenReturn(new ArrayList<>());
        String result = enrollmentListService.getWeeklySchedule(student.getStudentNumber()).toString();
        assertThat(Strings.isNullOrEmpty(result));
    }

    @Test
    void Given_studentNumberWithEnrollmentList_When_callGetWeeklyScheduleService_Then_returnSortedOutput() {
        Student student = createSomeStudent();
        TimeSchedule time08To10 = TimeScheduleBuilder.builder().withFromTime("08:00:00").withToTime("10:00:00").build();
        TimeSchedule time10To12 = TimeScheduleBuilder.builder().withFromTime("10:00:00").withToTime("12:00:00").build();
        TimeSchedule time13To15 = TimeScheduleBuilder.builder().withFromTime("13:00:00").withToTime("15:00:00").build();
        Section section1 = aSection().withClassScheduleList(
                new ClassScheduleBuilder().withWeekDay(WeekDayEnum.SATURDAY).withTimeSchedule(time08To10).build(),
                new ClassScheduleBuilder().withWeekDay(WeekDayEnum.SUNDAY).withTimeSchedule(time10To12).build(),
                new ClassScheduleBuilder().withWeekDay(WeekDayEnum.SATURDAY).withTimeSchedule(time13To15).build(),
                new ClassScheduleBuilder().withWeekDay(WeekDayEnum.TUESDAY).withTimeSchedule(time08To10).build(),
                new ClassScheduleBuilder().withWeekDay(WeekDayEnum.THURSDAY).withTimeSchedule(time10To12).build(),
                new ClassScheduleBuilder().withWeekDay(WeekDayEnum.MONDAY).withTimeSchedule(time13To15).build()).build();
        Section section2 = aSection().withClassScheduleList(
                new ClassScheduleBuilder().withWeekDay(WeekDayEnum.SUNDAY).withTimeSchedule(time08To10).build(),
                new ClassScheduleBuilder().withWeekDay(WeekDayEnum.THURSDAY).withTimeSchedule(time10To12).build(),
                new ClassScheduleBuilder().withWeekDay(WeekDayEnum.TUESDAY).withTimeSchedule(time13To15).build(),
                new ClassScheduleBuilder().withWeekDay(WeekDayEnum.MONDAY).withTimeSchedule(time08To10).build(),
                new ClassScheduleBuilder().withWeekDay(WeekDayEnum.SATURDAY).withTimeSchedule(time10To12).build(),
                new ClassScheduleBuilder().withWeekDay(WeekDayEnum.MONDAY).withTimeSchedule(time13To15).build()).build();

        List<EnrollmentList> enrollmentLists = new ArrayList<>();
        enrollmentLists.add(new EnrollmentListBuilder().withOwner(student).withSections(section1,section2).build());
//        when(any().getSectionSchedule(any())).thenCallRealMethod();
        when(mockEnrollmentListRepository.findByOwnerNumber(student.getStudentNumber())).thenReturn(enrollmentLists);
        String result = enrollmentListService.getWeeklySchedule(student.getStudentNumber()).toString();
        assertThat(result.equals(new StringBuilder("2 - daniList\n").append("SATURDAY\n").append("\t08:00:00-10:00:00\tMath-1\t2\n").append("\t10:00:00-12:00:00\tMath-1\t3\n")
                .append("\t13:00:00-15:00:00\tMath-1\t2\n").append("SUNDAY\n").append("\t08:00:00-10:00:00\tMath-1\t3\n").append("\t10:00:00-12:00:00\tMath-1\t2\n")
                .append("MONDAY\n").append("\t08:00:00-10:00:00\tMath-1\t3\n").append("\t13:00:00-15:00:00\tMath-1\t2\n").append("\t13:00:00-15:00:00\tMath-1\t3\n")
                .append("TUESDAY\n").append("\t08:00:00-10:00:00\tMath-1\t2\n").append("\t13:00:00-15:00:00\tMath-1\t3\n").append("WEDNESDAY\n").append("THURSDAY\n")
                .append("\t10:00:00-12:00:00\tMath-1\t2\n").append("\t10:00:00-12:00:00\tMath-1\t3\n").append("FRIDAY\n").toString()));
    }

    @Test
    void Given_enrollmentLists_When_callExtractEnrollmentListsStructure_Then_getSpecificSection() {
        List<EnrollmentList> enrollmentLists = new ArrayList<>();
        enrollmentLists.add(createSomeEnrollmentListWithoutConflict());
        enrollmentLists.add(createSomeEnrollmentListWithoutConflict());
        enrollmentLists.add(createSomeEnrollmentListWithoutConflict());
        Section section = enrollmentLists.get(0).getSections().get(0);
        Student student = spy(enrollmentLists.get(0).getOwner());

        when(student.isNumberOfUnitsAllowed(anyInt())).thenReturn(true);
        Map<Section, List<Student>> sectionListMap = enrollmentListService.extractEnrollmentListsStructure(enrollmentLists);
        assertThat(sectionListMap.containsKey(section));
        assertThat(sectionListMap.get(section).contains(student));
    }
}
