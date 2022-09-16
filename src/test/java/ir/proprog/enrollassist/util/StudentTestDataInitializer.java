package ir.proprog.enrollassist.util;

import com.github.javafaker.Faker;
import ir.proprog.enrollassist.builder.MajorBuilder;
import ir.proprog.enrollassist.builder.StudentBuilder;
import ir.proprog.enrollassist.builder.StudyRecordBuilder;
import ir.proprog.enrollassist.domain.entity.*;
import ir.proprog.enrollassist.domain.factory.LevelFactory;
import ir.proprog.enrollassist.domain.valueobject.Grade;
import ir.proprog.enrollassist.domain.valueobject.Name;
import ir.proprog.enrollassist.repository.*;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.api.Randomizer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ir.proprog.enrollassist.builder.CourseBuilder.aCourse;
import static ir.proprog.enrollassist.builder.StudentBuilder.record;

@Component
public class StudentTestDataInitializer {
    StudentRepository studentRepository;
    MajorRepository majorRepository;
    StudyRecordRepository studyRecordRepository;
    CourseRepository courseRepository;
    LevelRepository levelRepository;

    public StudentTestDataInitializer(StudentRepository studentRepository, MajorRepository majorRepository, StudyRecordRepository studyRecordRepository, CourseRepository courseRepository, LevelRepository levelRepository) {
        this.studentRepository = studentRepository;
        this.majorRepository = majorRepository;
        this.studyRecordRepository = studyRecordRepository;
        this.courseRepository = courseRepository;
        this.levelRepository = levelRepository;
    }

    private static List<Student> randomStudentList = new ArrayList<>();
    private static int studentSize = 25;

    public static List<Student> generateRandomStudents() {
        if (randomStudentList.isEmpty()) {
            for (int i = 0; i < studentSize; i++) {
                randomStudentList.add(getRandomStudent());
            }
        }
        return randomStudentList;
    }

    public void populate() {
        LevelFactory.reset();
        Student student = getRandomStudent();
        Student student2 = getRandomStudent();

        List<Student> studentList = List.of(student, student2);
        List<StudyRecord> studyRecordList = studentList.stream()
                .map(Student::getGrades)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        List<Course> courseList = studyRecordList.stream()
                .map(StudyRecord::getCourse)
                .collect(Collectors.toList());
        List<Major> majorList = studentList.stream()
                .map(Student::getMajor)
                .collect(Collectors.toList());
        List<Level> levelList = majorList.stream()
                .map(Major::getLevel)
                .collect(Collectors.toList());

        courseRepository.saveAll(courseList);
        studyRecordRepository.saveAll(studyRecordList);
        levelRepository.saveAll(levelList);
        majorRepository.saveAll(majorList);
        studentRepository.saveAll(studentList);
    }

    public static Student getRandomStudent() {
        Faker faker = new Faker();
        EasyRandomParameters parameters = new EasyRandomParameters();
        EasyRandom generator = new EasyRandom(parameters);

        Major major = MajorBuilder.createSomeMajorWithChart();
        return generator.nextObject(StudentBuilder.class)
                .withName(new Name(faker.name().name()))
                .withMajor(major)
                .withTranscript(record(generator.nextObject(String.class), aCourse().build(), 12))
                .build();
    }

    static class StudentRandomizer implements Randomizer<Student> {
        @Override
        public Student getRandomValue() {
            Faker faker = new Faker();
            return generateRandomStudents().get(faker.number().numberBetween(0, studentSize - 1));
        }
    }

    public void deleteAll() {
        studentRepository.deleteAll();
        majorRepository.deleteAll();
        levelRepository.deleteAll();
        studyRecordRepository.deleteAll();
        courseRepository.deleteAll();
    }
}
