package ir.proprog.enrollassist.domain.entity;

import ir.proprog.enrollassist.domain.violation.*;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // as required by JPA, don't use it in your code
@Getter
public class EnrollmentList {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String listName;
    @ManyToOne
    Student owner;
    @ManyToMany
    List<Section> sections;
    private boolean finalList;

    public EnrollmentList(@NonNull String listName, @NonNull Student owner) {
        if (listName.isEmpty())
            throw new IllegalArgumentException("Enrollment list must have a name");
        this.listName = listName;
        this.owner = owner;
        this.sections = new ArrayList<>();
        this.finalList = false;
    }

    public void setListAsFinal() {
        this.finalList = true;
    }

    public void addSections(Section... new_sections) {
        if(!sections.contains(new_sections))
            Collections.addAll(sections, new_sections);
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnrollmentList that = (EnrollmentList) o;
        return owner.equals(that.owner) && listName.equals(that.listName) && sections.containsAll(that.sections) && finalList == that.finalList;
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, listName, sections, finalList);
    }

    public int sumOfCredits() {
        return this.getSections().stream().mapToInt(e -> e.getCourse().getCredits()).sum();
    }

    public List<EnrollmentRuleViolation> checkEnrollmentRules() {
        List<EnrollmentRuleViolation> violations = new ArrayList<>();
        violations.addAll(checkHasPassedAllPrerequisites());
        violations.addAll(checkHasNotAlreadyPassedCourses());
        violations.addAll(checkNoCourseHasRequestedTwice());
        violations.addAll(checkGpaLimit());
        violations.addAll(checkConflictForSectionSchedule());
        violations.addAll(checkConflictOnExamSchedule());
        return violations;
    }

    List<EnrollmentRuleViolation> checkConflictForSectionSchedule() {
        List<EnrollmentRuleViolation> violations = new ArrayList<>();
        for (int i = 0; i < getSections().size(); i++)
            for (int j = i + 1; j < getSections().size(); j++)
                if (getSections().get(i).hasClassScheduleConflict(getSections().get(j)))
                    violations.add(new SectionConflict(getSections().get(i), getSections().get(j)));
        return violations;
    }

    List<EnrollmentRuleViolation> checkNoCourseHasRequestedTwice() {
        List<EnrollmentRuleViolation> violations = new ArrayList<>();
        for (int i = 0; i < getSections().size(); i++)
            for (int j = i + 1; j < getSections().size(); j++)
                if (getSections().get(i).getCourse().equals(getSections().get(j).getCourse()))
                    violations.add(new CourseRequestedTwice(getSections().get(i), getSections().get(j)));
        return violations;
    }

    List<EnrollmentRuleViolation> checkHasNotAlreadyPassedCourses() {
        List<EnrollmentRuleViolation> violations = new ArrayList<>();
        for (Section section : getSections())
            if (getOwner().hasPassed(section.getCourse()))
                violations.add(new RequestedCourseAlreadyPassed(section.getCourse()));
        return violations;
    }

    List<EnrollmentRuleViolation> checkHasPassedAllPrerequisites() {
        List<EnrollmentRuleViolation> violations = new ArrayList<>();
        for (Section section : getSections())
            violations.addAll(section.getCourse().prerequisitesNotPassedBy(getOwner()));
        return violations;
    }
    List<EnrollmentRuleViolation> checkGpaLimit() {
        List<EnrollmentRuleViolation> violations = new ArrayList<>();
        int creditOfThisTerm = sumOfCredits();
        if (!getOwner().isNumberOfUnitsAllowed(creditOfThisTerm))
            violations.add(new GpaLimitViolation(getOwner().getStudentName().getName(), creditOfThisTerm));
        return violations;
    }

    List<EnrollmentRuleViolation> checkConflictOnExamSchedule() {
        List<EnrollmentRuleViolation> violations = new ArrayList<>();
        for (int i = 0; i < getSections().size(); i++) {
            Section firstSection = getSections().get(i);
            for (int j = i + 1; j < getSections().size(); j++) {
                Section secondSection = getSections().get(j);
                if (firstSection.getExamSchedule().equals(secondSection.getExamSchedule()))
                    violations.add(new SectionConflict(firstSection, secondSection));
            }
        }
        return violations;
    }
}
