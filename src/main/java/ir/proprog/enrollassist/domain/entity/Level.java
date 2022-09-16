package ir.proprog.enrollassist.domain.entity;

import ir.proprog.enrollassist.domain.valueobject.Grade;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class Level {
    @Id
    @GeneratedValue
    private long id;

    abstract Grade minimumGrade();

    abstract int maximumUnit();

    abstract Grade limitedGPA();

    abstract Grade exceptionalGPA();

    abstract int maximumUnitForLimitedGrade();

    abstract int maximumUnitForExceptionalGrade();

    public boolean isCoursePassed(Grade score) {
        return score.greaterThanOrEqualTo(minimumGrade());
    }

    public boolean isNumberOfUnitsAllowed(int numberOfUnits, Grade gpa) {
        return (gpa.lessThan(exceptionalGPA()) && (gpa.equals(Grade.ZERO) || gpa.greaterThan(limitedGPA())) && numberOfUnits <= maximumUnit())
                || (gpa.greaterThanOrEqualTo(exceptionalGPA()) && numberOfUnits <= maximumUnitForExceptionalGrade())
                || (gpa.lessThanOrEqualTo(limitedGPA()) && numberOfUnits <= maximumUnitForLimitedGrade());
    }
}
