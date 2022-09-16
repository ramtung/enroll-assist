package ir.proprog.enrollassist.domain.entity;

import ir.proprog.enrollassist.domain.valueobject.Name;
import ir.proprog.enrollassist.domain.violation.EnrollmentRuleViolation;
import ir.proprog.enrollassist.domain.violation.PrerequisiteNotTaken;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // as required by JPA, don't use it in your code
@Getter
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String courseNumber;
    @Embedded
    private Name title;
    private int credits;

    public Course(@NonNull String courseNumber, @NonNull Name title, int credits) {
        if (courseNumber.equals(""))
            throw new IllegalArgumentException("Course number cannot be empty");
        if (title.getName().equals(""))
            throw new IllegalArgumentException("Course must have a name");
        if (credits < 0)
            throw new IllegalArgumentException("Course credit units cannot be negative");
        this.courseNumber = courseNumber;
        this.title = title;
        this.credits = credits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return courseNumber.equals(course.courseNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseNumber);
    }

    @Override
    public String toString() {
        return courseNumber + " " + title.getName();
    }

    public List<EnrollmentRuleViolation> prerequisitesNotPassedBy(Student student) {
        List<EnrollmentRuleViolation> violations = new ArrayList<>();
        List<Course> prerequisites = student.getMajor().getListOfPrerequisiteOfCourse(this);
        for (Course pre : prerequisites) {
            if (!student.hasPassed(pre))
                violations.add(new PrerequisiteNotTaken(this, pre));
        }
        return violations;
    }
}
