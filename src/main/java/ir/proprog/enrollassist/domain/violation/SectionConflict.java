package ir.proprog.enrollassist.domain.violation;

import ir.proprog.enrollassist.domain.entity.Section;
import ir.proprog.enrollassist.domain.violation.EnrollmentRuleViolation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionConflict extends EnrollmentRuleViolation {
    private Section section1;
    private Section section2;

    public SectionConflict(Section section1, Section section2) {
        this.section1 = section1;
        this.section2 = section2;
    }
}
