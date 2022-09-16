package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.domain.entity.StudyRecord;
import org.springframework.data.repository.CrudRepository;

public interface StudyRecordRepository extends CrudRepository<StudyRecord, Long> {
}
