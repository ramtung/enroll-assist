package ir.proprog.enrollassist.util;

import com.github.javafaker.Faker;
import ir.proprog.enrollassist.domain.entity.*;
import ir.proprog.enrollassist.domain.valueobject.Name;
import ir.proprog.enrollassist.repository.*;
import org.jeasy.random.EasyRandom;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseTestDataInitializer {
    CourseRepository courseRepository;

    public CourseTestDataInitializer(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public void populateListOfCourse(int size) {
        EasyRandom generator = new EasyRandom();
        List<Course> courses = generator.objects(CourseDataBuilder.class, size).map(CourseDataBuilder::build).collect(Collectors.toList());
        courseRepository.saveAll(courses);
    }

    public void deleteAll() {
        courseRepository.deleteAll();
    }

    static class CourseDataBuilder {

        private String courseNumber;
        private Name title;
        private int credits;
        private final Faker faker = new Faker();

        private CourseDataBuilder() {
            this.withCourseNumber().withTitle().withCredits();
        }

        public static CourseDataBuilder aCourseDataBuilder() {
            return new CourseDataBuilder();
        }

        public CourseDataBuilder withCourseNumber() {
            String prefix = faker.lorem().fixedString(5);
            this.courseNumber = faker.numerify(prefix + "#####");
            return this;
        }

        public CourseDataBuilder withTitle() {
            this.title = new Name(faker.lorem().fixedString(30));
            return this;
        }

        public void withCredits() {
            this.credits = 3;
        }

        public Course build() {
            return new Course(this.courseNumber, this.title, this.credits);
        }
    }
}
