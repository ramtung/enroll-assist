package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.domain.entity.Course;
import ir.proprog.enrollassist.domain.entity.Major;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MajorRepository extends CrudRepository<Major, Long> {
    Optional<Major> findByMajorNumber(String majorNumber);
}
