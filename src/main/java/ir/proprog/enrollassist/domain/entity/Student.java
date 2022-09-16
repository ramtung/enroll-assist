package ir.proprog.enrollassist.domain.entity;

import ir.proprog.enrollassist.domain.valueobject.Grade;
import ir.proprog.enrollassist.domain.valueobject.Name;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // as required by JPA, don't use it in your code
@Getter
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String studentNumber;
    @Embedded
    private Name studentName;
    @OneToMany(cascade = CascadeType.ALL)
    private Set<StudyRecord> grades = new HashSet<>();
    @ManyToOne
    private Major major;

    public Student(@NonNull String studentNumber, @NonNull Name studentName, @NonNull Major major) {
        checkFields(studentNumber, studentName);
        this.studentNumber = studentNumber;
        this.studentName = studentName;
        this.major = major;
    }

    private void checkFields(String studentNumber, Name name) {
        if (studentNumber.equals(""))
            throw new IllegalArgumentException("Student number cannot be empty");
        else if (!studentNumber.matches("[0-9]+"))
            throw new IllegalArgumentException("Student number must contain only numbers");
        else if (BigDecimal.ZERO.equals(new BigDecimal(studentNumber)))
            throw new IllegalArgumentException("Student number can not be zero");
        if (name.getName().equals(""))
            throw new IllegalArgumentException("Student must have a name");
        else if (!name.getName().matches(".*[a-zA-Z].*"))
            throw new IllegalArgumentException("Student name must contain at least one character");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return studentNumber.equals(student.studentNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentNumber);
    }

    public Student setGrade(String term, Course course, Grade grade) {
        grades.add(new StudyRecord(term, course, grade));
        return this;
    }

    @Override
    public String toString() {
        return !grades.isEmpty() ? ", grades=" + grades : ", major=" + major;
    }

    public boolean isCoursePassed(Grade grade) {
        return getMajor().getLevel().isCoursePassed(grade);
    }

    public boolean hasPassed(Course course) {
        return getGrades().stream()
                .anyMatch(sr -> sr.getCourse().equals(course)
                        && isCoursePassed(sr.getGrade()));
    }

    public Grade calculateGPA() {
        int sumCredit = getGrades().stream().mapToInt(e -> e.getCourse().getCredits()).sum();
        Grade sumGrade = Grade.ZERO;
        for (StudyRecord grade : getGrades()) {
            sumGrade = sumGrade.add(grade.getGrade().multiply(grade.getCourse().getCredits()));
        }
        return sumGrade.equals(Grade.ZERO) ? Grade.ZERO : sumGrade.divide(sumCredit);
    }

    public boolean isNumberOfUnitsAllowed(int sumOfCredits) {
        Grade gpa = calculateGPA();
        return getMajor().getLevel().isNumberOfUnitsAllowed(sumOfCredits, gpa);
    }

    public boolean hasPassedPrerequisites(Course course) {
        List<Course> prerequisiteList = getMajor().getListOfPrerequisiteOfCourse(course);
        for (Course pre : prerequisiteList) {
            if (!hasPassed(pre))
                return false;
        }
        return true;
    }

    public List<Course> getListOfTakeableCourses() {
        try {
            List<Course> courseList = getMajor().findTakeableCourses();
            List<Course> takeableCourseList = new ArrayList<>();
            for (Course course : courseList) {
                if (!hasPassed(course) && hasPassedPrerequisites(course)) {
                    takeableCourseList.add(course);
                }
            }
            return takeableCourseList;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    public List<Section> sectionsThatCanTake(Iterable<Section> allSections) {
        try {
            List<Course> takeableCourseByMajor = getListOfTakeableCourses();
            List<Section> takeableSectionList = new ArrayList<>();
            for (Section section : allSections) {
                if (takeableCourseByMajor.stream().anyMatch(course -> course.equals(section.getCourse()))) {
                    takeableSectionList.add(section);
                }
            }
            return takeableSectionList;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
