package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.controller.section.SectionDemandView;
import ir.proprog.enrollassist.domain.entity.EnrollmentList;
import ir.proprog.enrollassist.util.SampleDataInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
public class EnrollmentListRepositoryTest {
    @Autowired
    private SampleDataInitializer dataInitializer;
    @Autowired
    private EnrollmentListRepository enrollmentListRepository;
    @Autowired
    private SectionRepository sectionRepository;

    @BeforeEach
    public void populate() {
        dataInitializer.populate();
    }

    @AfterEach
    public void cleanUp() {
        dataInitializer.deleteAll();
    }

    @Test
    public void Given_demandsForTwoStudents_When_loadAllDemands_Then_returnAllDemandedSectionsCorrectly() {
        List<SectionDemandView> demands = enrollmentListRepository.findDemandForAllSections();
        for (SectionDemandView demand : demands) {
            demand.setSectionView(sectionRepository.findById(demand.getSectionId()).orElseThrow());
        }
        assertThat(demands)
                .extracting("sectionView.courseTitle", "sectionView.sectionNo", "demand")
                .containsExactlyInAnyOrder(
                        tuple("MATH2", "01", 2L),
                        tuple("PHYS1", "01", 1L),
                        tuple("PHYS2", "02", 1L),
                        tuple("AP", "01", 2L),
                        tuple("DM", "01", 2L)
                );
    }

    @Test
    public void Given_enrollmentLists_When_findByFinalListIsTrue_Then_returnAllFinalLists() {
        List<EnrollmentList> enrollmentLists = enrollmentListRepository.findByFinalListIsTrue();
        assertThat(enrollmentLists).hasSize(1)
                .extracting("listName", "finalList")
                .containsExactlyInAnyOrder(
                        tuple("Student 2 List", true)
                );
    }
}
