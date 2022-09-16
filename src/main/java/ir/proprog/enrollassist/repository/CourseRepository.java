package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.domain.entity.Course;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CourseRepository extends CrudRepository<Course, Long> {
    List<Course> findByCourseNumber(String courseNumber);
}
