package ir.proprog.enrollassist.controller.section;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SectionEnrollmentResultView {
    private String sectionNo;
    private List<String> studentNumber;

    public SectionEnrollmentResultView(String sectionNo, List<String> studentNumber) {
        this.sectionNo = sectionNo;
        this.studentNumber = studentNumber;
    }
}
