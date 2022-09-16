package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.domain.entity.Section;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SectionRepository extends CrudRepository<Section, Long> {
    Optional<Section> findBySectionNo(String sectionNumber);
}
