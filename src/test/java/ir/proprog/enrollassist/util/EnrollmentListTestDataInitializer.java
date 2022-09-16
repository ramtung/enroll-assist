package ir.proprog.enrollassist.util;

import com.github.javafaker.Faker;
import ir.proprog.enrollassist.domain.entity.*;
import ir.proprog.enrollassist.repository.*;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import org.jeasy.random.api.Randomizer;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EnrollmentListTestDataInitializer {
    EnrollmentListRepository enrollmentListRepository;
    SectionRepository sectionRepository;
    CourseRepository courseRepository;
    StudentRepository studentRepository;
    MajorRepository majorRepository;
    StudyRecordRepository studyRecordRepository;
    LevelRepository levelRepository;

    public EnrollmentListTestDataInitializer(EnrollmentListRepository enrollmentListRepository, SectionRepository sectionRepository,
                                             CourseRepository courseRepository, StudentRepository studentRepository,
                                             MajorRepository majorRepository, StudyRecordRepository studyRecordRepository,
                                             LevelRepository levelRepository) {
        this.enrollmentListRepository = enrollmentListRepository;
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.majorRepository = majorRepository;
        this.studyRecordRepository = studyRecordRepository;
        this.levelRepository = levelRepository;
    }

    public void populateListOfEnrollmentList(int size) {
        EasyRandomParameters parameters = new EasyRandomParameters();
        parameters.collectionSizeRange(6, 6)
                .excludeField(FieldPredicates.named("id").and(FieldPredicates.inClass(EnrollmentList.class)))
                .randomize(Student.class, new StudentTestDataInitializer.StudentRandomizer())
                .randomize(Section.class, new SectionTestDataInitializer.SectionRandomizer())
                .randomize(String.class, new StringRandomizer())
                .randomize(boolean.class, new BooleanRandomizer());

        EasyRandom generator = new EasyRandom(parameters);
        List<EnrollmentList> enrollmentLists = generator.objects(EnrollmentList.class, size)
                .collect(Collectors.toList());
        List<Section> sectionList = enrollmentLists.stream()
                .map(EnrollmentList::getSections)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        List<Course> courseList = sectionList.stream()
                .map(Section::getCourse)
                .collect(Collectors.toList());
        List<Student> studentList = enrollmentLists.stream()
                .map(EnrollmentList::getOwner)
                .collect(Collectors.toList());
        studentList.addAll(sectionList.stream()
                .map(Section::getStudents)
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
        List<StudyRecord> studyRecordList = studentList.stream()
                .map(Student::getGrades)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        List<Course> courseList2 = studyRecordList.stream()
                .map(StudyRecord::getCourse)
                .collect(Collectors.toList());
        List<Major> majorList = studentList.stream()
                .map(Student::getMajor)
                .collect(Collectors.toList());
        List<Level> levelList = majorList.stream()
                .map(Major::getLevel)
                .collect(Collectors.toList());

        levelRepository.saveAll(levelList);
        majorRepository.saveAll(majorList);
        courseRepository.saveAll(courseList2);
        studentRepository.saveAll(studentList);
        studyRecordRepository.saveAll(studyRecordList);
        courseRepository.saveAll(courseList);
        sectionRepository.saveAll(sectionList);
        enrollmentListRepository.saveAll(enrollmentLists);
    }

    static class StringRandomizer implements Randomizer<String> {
        @Override
        public String getRandomValue() {
            Faker faker = new Faker();
            return faker.numerify("####");
        }
    }

    static class BooleanRandomizer implements Randomizer<Boolean> {
        @Override
        public Boolean getRandomValue() {
            Faker faker = new Faker();
            return faker.random().nextBoolean();
        }
    }

    public void deleteAll() {
        enrollmentListRepository.deleteAll();
        sectionRepository.deleteAll();
        studentRepository.deleteAll();
        studyRecordRepository.deleteAll();
        majorRepository.deleteAll();
        courseRepository.deleteAll();
        levelRepository.deleteAll();
    }
}
