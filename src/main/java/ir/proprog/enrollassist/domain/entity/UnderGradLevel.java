package ir.proprog.enrollassist.domain.entity;

import ir.proprog.enrollassist.domain.valueobject.Grade;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "UNDERGRAD")
public class UnderGradLevel extends Level {
    @Override
    Grade minimumGrade() {
        return new Grade(10);
    }

    @Override
    int maximumUnit() {
        return 20;
    }

    @Override
    Grade limitedGPA() {
        return new Grade(12);
    }

    @Override
    Grade exceptionalGPA() {
        return new Grade(18);
    }

    @Override
    int maximumUnitForLimitedGrade() {
        return 14;
    }

    @Override
    int maximumUnitForExceptionalGrade() {
        return 24;
    }
}
