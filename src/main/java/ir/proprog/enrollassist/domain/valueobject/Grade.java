package ir.proprog.enrollassist.domain.valueobject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Grade {
    @Transient
    public static Grade ZERO = new Grade(0);

    private float grade;

    public Grade(Number grade) {
        this.grade = grade.floatValue();
        this.grade = Math.round(this.grade * 100) / 100.0f;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "grade=" + grade +
                '}';
    }

    public int compareTo(Grade otherGrade) {
        return Float.compare(this.getGrade(), otherGrade.getGrade());
    }

    public boolean greaterThan(Grade otherGrade) {
        return this.compareTo(otherGrade) > 0;
    }

    public boolean greaterThanOrEqualTo(Grade otherGrade) {
        return this.compareTo(otherGrade) >= 0;
    }

    public boolean lessThan(Grade otherGrade) {
        return this.compareTo(otherGrade) < 0;
    }

    public boolean lessThanOrEqualTo(Grade otherGrade) {
        return this.compareTo(otherGrade) <= 0;
    }

    public Grade add(Grade otherGrade) {
        return new Grade(this.getGrade() + otherGrade.getGrade());
    }

    public Grade multiply(int number) {
        return new Grade(this.getGrade() * number);
    }

    public Grade divide(int number) {
        return new Grade(this.getGrade() / number);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grade that = (Grade) o;
        return this.compareTo(that) == 0;
    }

    public int hashCode() {
        return Objects.hash(grade);
    }
}
