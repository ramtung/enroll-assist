package ir.proprog.enrollassist.builder;

import ir.proprog.enrollassist.domain.entity.EnrollmentList;
import ir.proprog.enrollassist.domain.entity.Section;
import ir.proprog.enrollassist.domain.entity.Student;

import java.util.ArrayList;
import java.util.List;

import static ir.proprog.enrollassist.builder.StudentBuilder.createSomeStudent;

public class EnrollmentListBuilder {
    private String listName = "daniList";
    private Student owner = createSomeStudent();
    private Section[] sections = {};
    private boolean finalList = false;

    public static EnrollmentListBuilder anEnrollmentList() {
        return new EnrollmentListBuilder();
    }

    public EnrollmentListBuilder withListName(String listName) {
        this.listName = listName;
        return this;
    }

    public EnrollmentListBuilder withOwner(Student owner) {
        this.owner = owner;
        return this;
    }

    public EnrollmentListBuilder withSections(Section... sections) {
        this.sections = sections;
        return this;
    }

    public EnrollmentListBuilder withFinalList(boolean finalList) {
        this.finalList = finalList;
        return this;
    }

    public EnrollmentList build() {
        EnrollmentList enrollmentList = new EnrollmentList(this.listName, this.owner);
        enrollmentList.addSections(sections);
        return enrollmentList;
    }

    public static EnrollmentList createSomeEnrollmentList() {
        return new EnrollmentListBuilder().build();
    }

    public static EnrollmentList createSomeEnrollmentListWithoutConflict() {
        List<Section> sectionList = SectionBuilder.aSection().createSectionList();
        return new EnrollmentListBuilder().withSections(sectionList.get(0), sectionList.get(1)).build();
    }

    public static List<EnrollmentList> createListOfEnrollmentListForAStudent(Student owner) {
        List<EnrollmentList> enrollmentLists = new ArrayList<>(3);
        enrollmentLists.add(new EnrollmentListBuilder().withOwner(owner).build());
        enrollmentLists.add(new EnrollmentListBuilder().withOwner(owner).build());
        enrollmentLists.add(new EnrollmentListBuilder().withOwner(owner).build());
        return enrollmentLists;
    }
}
