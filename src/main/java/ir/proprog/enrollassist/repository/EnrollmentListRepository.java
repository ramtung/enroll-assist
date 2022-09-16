package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.controller.enrollmentList.EnrollmentListView;
import ir.proprog.enrollassist.controller.section.SectionDemandView;
import ir.proprog.enrollassist.domain.entity.EnrollmentList;
import ir.proprog.enrollassist.domain.entity.Section;
import ir.proprog.enrollassist.domain.entity.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentListRepository extends CrudRepository<EnrollmentList, Long> {
    @Query(value = "select new ir.proprog.enrollassist.controller.section.SectionDemandView(section.id, count(distinct list.owner)) from EnrollmentList list join list.sections as section group by section.id")
    List<SectionDemandView> findDemandForAllSections();

    @Query(value = "select new ir.proprog.enrollassist.controller.enrollmentList.EnrollmentListView(list.listName, owner.studentNumber) from EnrollmentList list join list.owner as owner where owner.studentNumber = :studentNumber")
    Optional<EnrollmentListView> findByStudentNumber(String studentNumber);

    List<EnrollmentList> findBySectionsContains(Section section);

    List<EnrollmentList> findByOwner(Student owner);

    @Query(value = "select list from EnrollmentList list join list.owner as owner where owner.studentNumber = :studentNumber")
    List<EnrollmentList> findByOwnerNumber(String studentNumber);

    List<EnrollmentList> findByFinalListIsTrue();
}
