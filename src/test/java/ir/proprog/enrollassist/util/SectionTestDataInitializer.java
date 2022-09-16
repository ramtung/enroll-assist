package ir.proprog.enrollassist.util;

import com.github.javafaker.Faker;
import ir.proprog.enrollassist.domain.entity.*;
import ir.proprog.enrollassist.domain.factory.LevelFactory;
import ir.proprog.enrollassist.domain.valueobject.WeekDayEnum;
import ir.proprog.enrollassist.domain.valueobject.schedule.TimeSchedule;
import ir.proprog.enrollassist.repository.*;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import org.jeasy.random.api.Randomizer;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Time;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SectionTestDataInitializer {
    SectionRepository sectionRepository;
    CourseRepository courseRepository;
    StudentRepository studentRepository;
    MajorRepository majorRepository;
    StudyRecordRepository studyRecordRepository;
    LevelRepository levelRepository;

    public SectionTestDataInitializer(SectionRepository sectionRepository, CourseRepository courseRepository, StudentRepository studentRepository, MajorRepository majorRepository, StudyRecordRepository studyRecordRepository, LevelRepository levelRepository) {
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.majorRepository = majorRepository;
        this.studyRecordRepository = studyRecordRepository;
        this.levelRepository = levelRepository;
    }

    private static List<Section> randomSectionList = new ArrayList<>();
    private static int sectionSize = 10;

    public static List<Section> generateRandomStudents() {
        if (randomSectionList.isEmpty()) {
            for (int i = 0; i < sectionSize; i++) {
                randomSectionList.add(getRandomSection());
            }
        }
        return randomSectionList;
    }

    public void populateListOfSections(int size) {
        LevelFactory.reset();
        EasyRandomParameters parameters = new EasyRandomParameters();
        parameters.randomize(Section.class, new SectionRandomizer());

        EasyRandom generator = new EasyRandom(parameters);
        List<Section> sectionList = generator.objects(Section.class, size)
                .collect(Collectors.toList());
        Collection<Course> courseList = sectionList.stream()
                .map(Section::getCourse)
                .collect(Collectors.toList());
        Collection<Student> studentList = sectionList.stream().map(Section::getStudents).flatMap(Collection::stream).collect(Collectors.toList());

        List<StudyRecord> studyRecordList = studentList.stream()
                .map(Student::getGrades)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        List<Major> majorList = studentList.stream()
                .map(Student::getMajor)
                .collect(Collectors.toList());
        List<Level> levelList = majorList.stream()
                .map(Major::getLevel)
                .collect(Collectors.toList());
        levelRepository.saveAll(levelList);
        majorRepository.saveAll(majorList);
        studyRecordRepository.saveAll(studyRecordList);
        studentRepository.saveAll(studentList);
        courseRepository.saveAll(courseList);
        sectionRepository.saveAll(sectionList);
    }

    public void deleteAll() {
        sectionRepository.deleteAll();
        studentRepository.deleteAll();
        studyRecordRepository.deleteAll();
        majorRepository.deleteAll();
        levelRepository.deleteAll();
        courseRepository.deleteAll();
    }

    static class IntegerRandomizer implements Randomizer<Integer> {
        @Override
        public Integer getRandomValue() {
            Faker faker = new Faker();
            return faker.number().numberBetween(3, 3);
        }
    }

    static class CourseRandomizer implements Randomizer<Course> {
        @Override
        public Course getRandomValue() {
            return CourseTestDataInitializer.CourseDataBuilder.aCourseDataBuilder().build();
        }
    }

    static class StringRandomizer implements Randomizer<String> {
        @Override
        public String getRandomValue() {
            Faker faker = new Faker();
            return faker.numerify("####");
        }
    }

    static class TimeScheduleRandomizer implements Randomizer<TimeSchedule> {
        private static int hour = 0;

        @Override
        public TimeSchedule getRandomValue() {
            if (hour > 23) hour = 0;
            Time time1 = Time.valueOf(hour + ":00:00");
            Time time2 = Time.valueOf(++hour + ":00:00");
            return new TimeSchedule(time1, time2);
        }
    }

    static class WeekDayEnumRandomizer implements Randomizer<WeekDayEnum> {
        private static int index = 0;

        @Override
        public WeekDayEnum getRandomValue() {
            if (index > 6) index = 0;
            return WeekDayEnum.values()[index++];
        }
    }

    static class DateRandomizer implements Randomizer<Date> {
        private static Date randomDate;

        private DateRandomizer() {
            Faker faker = new Faker();
            int year = faker.number().numberBetween(2000, 2030);
            int month = faker.number().numberBetween(1, 12);
            int day = faker.number().numberBetween(1, 30);
            randomDate = Date.valueOf(year + "-" + month + "-" + day);
        }

        @Override
        public Date getRandomValue() {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(randomDate);
            calendar.add(Calendar.DATE, 1);

            Instant instant = calendar.getTime().toInstant();
            ZoneId zoneId = ZoneId.of("America/Chicago");
            ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, zoneId);
            LocalDate localDate = zdt.toLocalDate();

            return Date.valueOf(localDate);
        }
    }

    private static Section getRandomSection() {
        EasyRandomParameters parameters = new EasyRandomParameters();
        parameters.collectionSizeRange(1, 1)
                .excludeField(FieldPredicates.named("id").and(FieldPredicates.inClass(Section.class)))
                .randomize(String.class, new StringRandomizer())
                .randomize(Course.class, new CourseRandomizer())
                .randomize(int.class, new IntegerRandomizer())
                .randomize(WeekDayEnum.class, new WeekDayEnumRandomizer())
                .randomize(Date.class, new DateRandomizer())
                .randomize(TimeSchedule.class, new TimeScheduleRandomizer())
                .randomize(Student.class, new StudentTestDataInitializer.StudentRandomizer());
        EasyRandom generator = new EasyRandom(parameters);
        return generator.nextObject(Section.class);
    }

    static class SectionRandomizer implements Randomizer<Section> {
        @Override
        public Section getRandomValue() {
            Faker faker = new Faker();
            return generateRandomStudents().get(faker.number().numberBetween(0, sectionSize - 1));
        }
    }
}
