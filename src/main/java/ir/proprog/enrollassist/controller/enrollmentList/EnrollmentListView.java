package ir.proprog.enrollassist.controller.enrollmentList;

import ir.proprog.enrollassist.controller.student.StudentView;
import ir.proprog.enrollassist.controller.section.SectionView;
import ir.proprog.enrollassist.domain.entity.EnrollmentList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class EnrollmentListView {
    private Long enrollmentListId;
    private String enrollmentListName;
    private String ownerNumber;
    private StudentView owner;
    List<SectionView> sectionViews;
    private boolean finalList;

    public EnrollmentListView(String enrollmentListName, String ownerNumber) {
        this.enrollmentListName = enrollmentListName;
        this.ownerNumber = ownerNumber;
    }

    public EnrollmentListView(EnrollmentList enrollmentList) {
        this.enrollmentListName = enrollmentList.getListName();
        this.owner = new ModelMapper().map(enrollmentList.getOwner(), StudentView.class);
        this.sectionViews = enrollmentList.getSections().stream().map(section -> new ModelMapper().map(section, SectionView.class)).collect(Collectors.toList());
    }
}
