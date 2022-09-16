package ir.proprog.enrollassist.util;

import ir.proprog.enrollassist.builder.EnrollmentListBuilder;
import ir.proprog.enrollassist.builder.MajorBuilder;
import ir.proprog.enrollassist.builder.SectionBuilder;
import ir.proprog.enrollassist.builder.StudentBuilder;
import ir.proprog.enrollassist.domain.entity.*;
import ir.proprog.enrollassist.domain.factory.LevelFactory;
import ir.proprog.enrollassist.domain.valueobject.Grade;
import ir.proprog.enrollassist.domain.valueobject.Name;
import ir.proprog.enrollassist.repository.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class TestDataInitializer {
    StudentRepository studentRepository;
    CourseRepository courseRepository;
    SectionRepository sectionRepository;
    EnrollmentListRepository enrollmentListRepository;
    MajorRepository majorRepository;
    LevelRepository levelRepository;

    public TestDataInitializer(StudentRepository studentRepository, CourseRepository courseRepository, SectionRepository sectionRepository, EnrollmentListRepository enrollmentListRepository, MajorRepository majorRepository, LevelRepository levelRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.enrollmentListRepository = enrollmentListRepository;
        this.majorRepository = majorRepository;
        this.levelRepository = levelRepository;
    }

    public void populate() {
        Course math1 = new Course("4", new Name("MATH1"), 3);
        Course phys1 = new Course("8", new Name("PHYS1"), 3);
        Course prog = new Course("7", new Name("PROG"), 4);
        Course math2 = new Course("6", new Name("MATH2"), 3);
        Course phys2 = new Course("9", new Name("PHYS2"), 3);
        Course ap = new Course("2", new Name("AP"), 3);
        Course dm = new Course("3", new Name("DM"), 3);
        Course economy = new Course("1", new Name("ECO"), 3);
        Course maaref = new Course("5", new Name("MAAREF"), 2);
        Course farsi = new Course("12", new Name("FA"), 2);
        Course english = new Course("10", new Name("EN"), 2);
        Course akhlagh = new Course("11", new Name("AKHLAGH"), 2);
        Course karafarini = new Course("13", new Name("KAR"), 3);
        courseRepository.saveAll(List.of(math1, phys1, prog, math2, phys2, ap, dm, economy, maaref, farsi, english, akhlagh, karafarini));

        LevelFactory.reset();
        levelRepository.save(Objects.requireNonNull(LevelFactory.getLevel(LevelEnum.UNDERGRAD)));
        Major majorSoftware = MajorBuilder.builder().withTitle("Software Engineer").build();
        majorRepository.save(majorSoftware);
        MajorBuilder.builder().withMajor(majorSoftware)
                .withChart(math2, math1)
                .withChart(phys2, math1, phys1)
                .withChart(math1)
                .withChart(phys1)
                .withChart(prog)
                .withChart(farsi)
                .withChart(akhlagh);
        majorRepository.save(majorSoftware);
        Student mahsa = new StudentBuilder().withMajor(majorSoftware).build()
                .setGrade("t1", math1, new Grade(10))
                .setGrade("t1", phys1, new Grade(12))
                .setGrade("t1", prog, new Grade(16.3))
                .setGrade("t1", farsi, new Grade(18.5))
                .setGrade("t1", akhlagh, new Grade(15));
        studentRepository.save(mahsa);

        Major majorIT = MajorBuilder.builder().withTitle("IT Engineer").build();
        majorRepository.save(majorIT);
        MajorBuilder.builder().withMajor(majorIT)
                .withChart(math2, math1)
                .withChart(phys2, math1, math2)
                .withChart(math1)
                .withChart(phys1)
                .withChart(prog)
                .withChart(english)
                .withChart(akhlagh);
        majorRepository.save(majorIT);
        Student changiz = new StudentBuilder()
                .withNumber("810199998")
                .withName(new Name("Changiz Changizi"))
                .withMajor(majorIT)
                .build()
                .setGrade("t1", math1, new Grade(13.2))
                .setGrade("t1", phys1, new Grade(8.3))
                .setGrade("t1", prog, new Grade(10.5))
                .setGrade("t1", english, new Grade(11))
                .setGrade("t1", akhlagh, new Grade(16));
        studentRepository.save(changiz);

        Section math1_1 = SectionBuilder.aSection().withCourse(math1).withSectionNo("01").build(); sectionRepository.save(math1_1);
        Section phys1_1 = SectionBuilder.aSection().withCourse(phys1).withSectionNo("01").build(); sectionRepository.save(phys1_1);
        Section math2_1 = SectionBuilder.aSection().withCourse(math2).withSectionNo("01").build(); sectionRepository.save(math2_1);
        Section math2_2 = SectionBuilder.aSection().withCourse(math2).withSectionNo("02").build(); sectionRepository.save(math2_2);
        Section phys2_1 = SectionBuilder.aSection().withCourse(phys2).withSectionNo("01").build(); sectionRepository.save(phys2_1);
        Section phys2_2 = SectionBuilder.aSection().withCourse(phys2).withSectionNo("02").build(); sectionRepository.save(phys2_2);
        Section ap_1 = SectionBuilder.aSection().withCourse(ap).withSectionNo("01").build(); sectionRepository.save(ap_1);
        Section dm_1 = SectionBuilder.aSection().withCourse(dm).withSectionNo("01").build(); sectionRepository.save(dm_1);
        Section akhlagh_1 = SectionBuilder.aSection().withCourse(akhlagh).withSectionNo("01").build(); sectionRepository.save(akhlagh_1);
        Section english_1 = SectionBuilder.aSection().withCourse(english).withSectionNo("01").build(); sectionRepository.save(english_1);
        EnrollmentList mahsaList = EnrollmentListBuilder.anEnrollmentList().withOwner(mahsa).build();
        mahsaList.addSections(math2_1, phys2_2, ap_1, dm_1);
        enrollmentListRepository.save(mahsaList);
        EnrollmentList changizList = EnrollmentListBuilder.anEnrollmentList().withListName("Changiz's List").withOwner(changiz).build();
        changizList.addSections(math2_1, phys1_1, ap_1, dm_1);
        changizList.setListAsFinal();
        enrollmentListRepository.save(changizList);
    }

    public void deleteAll() {
        enrollmentListRepository.deleteAll();
        sectionRepository.deleteAll();
        studentRepository.deleteAll();
        majorRepository.deleteAll();
        levelRepository.deleteAll();
        courseRepository.deleteAll();
    }
}
