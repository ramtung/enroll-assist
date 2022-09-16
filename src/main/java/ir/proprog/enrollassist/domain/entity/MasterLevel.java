package ir.proprog.enrollassist.domain.entity;

import ir.proprog.enrollassist.domain.valueobject.Grade;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "MASTER")
public class MasterLevel extends Level {
    @Override
    Grade minimumGrade() {
        return new Grade(12);
    }

    @Override
    int maximumUnit() {
        return 12;
    }

    @Override
    Grade limitedGPA() {
        return new Grade(14);
    }

    @Override
    Grade exceptionalGPA() {
        return new Grade(20);
    }

    @Override
    int maximumUnitForLimitedGrade() {
        return 10;
    }

    @Override
    int maximumUnitForExceptionalGrade() {
        return 14;
    }
}
