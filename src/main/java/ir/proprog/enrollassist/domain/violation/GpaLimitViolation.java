package ir.proprog.enrollassist.domain.violation;

import lombok.Getter;

@Getter
public class GpaLimitViolation extends EnrollmentRuleViolation {
    private String studentName;
    private int sumOfCredits;

    public GpaLimitViolation(String studentName, int courseCanBeTakenByStudent) {
        this.studentName = studentName;
        this.sumOfCredits = courseCanBeTakenByStudent;
    }

    @Override
    public String toString() {
        return "StudentCourse{" +
                "studentName='" + studentName + '\'' +
                ", sumOfCredits=" + sumOfCredits +
                '}';
    }
}
