package ir.proprog.enrollassist.builder;

import ir.proprog.enrollassist.domain.entity.ClassSchedule;
import ir.proprog.enrollassist.domain.entity.Course;
import ir.proprog.enrollassist.domain.entity.Section;
import ir.proprog.enrollassist.domain.entity.Student;
import ir.proprog.enrollassist.domain.valueobject.schedule.ExamSchedule;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static ir.proprog.enrollassist.builder.ExamScheduleBuilder.anExamSchedule;

public class SectionBuilder {

    private String sectionNo;
    private Course course = CourseBuilder.aCourse().build();
    private ExamSchedule examSchedule = anExamSchedule();
    private ClassSchedule[] classScheduleList = {};
    private int capacity = 50;
    private Student[] students = {};

    private static int lastSectionNo = 1;
    private String getNextSectionNo() {
        return Integer.toString(++lastSectionNo);
    }

    private SectionBuilder() {
        this.sectionNo = getNextSectionNo();
    }

    public static SectionBuilder aSection() {
        return new SectionBuilder();
    }

    public SectionBuilder withCourse(Course course) {
        this.course = course;
        return this;
    }

    public SectionBuilder withSectionNo(String sectionNo) {
        this.sectionNo = sectionNo;
        return this;
    }

    public SectionBuilder withExamSchedule(ExamSchedule examSchedule) {
        this.examSchedule = examSchedule;
        return this;
    }

    public SectionBuilder withClassScheduleList(ClassSchedule... classScheduleList) {
        this.classScheduleList = classScheduleList;
        return this;
    }

    public SectionBuilder withCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public SectionBuilder withStudent(Student... studentList) {
        this.students = studentList;
        return this;
    }

    public Section build() {
        Section section = new Section(this.course, this.sectionNo, this.capacity);
        section.setExamSchedule(this.examSchedule);
        section.addClassSchedules(this.classScheduleList);
        section.addStudents(this.students);
        return section;
    }

    public static Section createSomeSection() {
        return new SectionBuilder().build();
    }

    public List<Section> createSectionList() {
        List<Section> sections = new ArrayList<>();
        sections.add(createSomeSection());

        ExamSchedule examSchedule = new ExamScheduleBuilder().withExamDate(Date.valueOf("2022-01-09")).build();
        Section section2 = new SectionBuilder().withSectionNo("02").withExamSchedule(examSchedule).build();
        sections.add(section2);
        return sections;
    }
}
