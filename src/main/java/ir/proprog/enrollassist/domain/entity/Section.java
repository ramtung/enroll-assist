package ir.proprog.enrollassist.domain.entity;

import ir.proprog.enrollassist.domain.valueobject.schedule.ExamSchedule;
import ir.proprog.enrollassist.domain.valueobject.schedule.SectionSchedule;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // as required by JPA, don't use it in your code
@Getter
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String sectionNo;
    @ManyToOne
    private Course course;
    @ElementCollection
    private List<ClassSchedule> classScheduleList;
    @Embedded
    private ExamSchedule examSchedule;
    private int capacity;
    @ManyToMany
    private Set<Student> students;

    public Section(@NonNull Course course, String sectionNo, int capacity) {
        if (sectionNo.equals(""))
            throw new IllegalArgumentException("Section number cannot be empty");
        else if (BigDecimal.ZERO.equals(new BigDecimal(sectionNo)))
            throw new IllegalArgumentException("section number can not be zero");
        this.sectionNo = sectionNo;
        this.course = course;
        this.classScheduleList = new ArrayList<>();
        this.capacity = capacity;
        this.students = new HashSet<>();
    }

    public void addClassSchedule(ClassSchedule classSchedule) {
        if (classScheduleList.stream().anyMatch(schedule -> schedule.overlaps(classSchedule))) {
            throw new IllegalArgumentException("requested classSchedule conflicts with others");
        }
        classScheduleList.add(classSchedule);
    }

    public void addClassSchedules(ClassSchedule... new_classSchedules) {
        for (ClassSchedule classSchedule : new_classSchedules) {
            addClassSchedule(classSchedule);
        }
    }

    public void addStudents(Student... new_student) {
        if (!students.contains(new_student))
            Collections.addAll(students, new_student);
    }

    public void setExamSchedule(ExamSchedule examSchedule) {
        this.examSchedule = examSchedule;
    }

    public void enrollStudents(List<Student> requestedStudentList) {
        for (int i = 0; i < requestedStudentList.size() && this.students.size() < this.capacity; i++) {
            this.students.add(requestedStudentList.get(i));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return course.equals(section.course)
                && sectionNo.equals(section.sectionNo)
                && classScheduleList.size() == section.classScheduleList.size()
                && section.classScheduleList.containsAll(classScheduleList)
                && examSchedule.equals(section.getExamSchedule());
    }

    @Override
    public int hashCode() {
        return Objects.hash(course, sectionNo, classScheduleList);
    }

    public boolean overlaps(Object o) {
        if (this == o) return false;
        if (o == null || getClass() != o.getClass()) return false;
        Section that = (Section) o;
        return this.classScheduleList.stream().anyMatch(thisClassSchedule ->
                that.classScheduleList.stream().anyMatch(thisClassSchedule::overlaps));
    }

    @Override
    public String toString() {
        return "sectionNo=" + sectionNo +
                ", course=" + course.toString();
    }

    public boolean hasClassScheduleConflict(Section section2) {
        return overlaps(section2);
    }

    public SectionSchedule getSectionSchedule(ClassSchedule classSchedule) {
        return new SectionSchedule(getCourse().getTitle().getName(),
                getSectionNo(), classSchedule.getTimeSchedule());
    }
}
