package ir.proprog.enrollassist.util;

import ir.proprog.enrollassist.domain.entity.*;
import ir.proprog.enrollassist.domain.factory.LevelFactory;
import ir.proprog.enrollassist.domain.valueobject.Grade;
import ir.proprog.enrollassist.domain.valueobject.Name;
import ir.proprog.enrollassist.repository.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
//@Profile("dev")
public class SampleDataInitializer {
    StudentRepository studentRepository;
    CourseRepository courseRepository;
    SectionRepository sectionRepository;
    EnrollmentListRepository enrollmentListRepository;
    MajorRepository majorRepository;
    LevelRepository levelRepository;

    public SampleDataInitializer(StudentRepository studentRepository, CourseRepository courseRepository, SectionRepository sectionRepository, EnrollmentListRepository enrollmentListRepository, MajorRepository majorRepository, LevelRepository levelRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.enrollmentListRepository = enrollmentListRepository;
        this.majorRepository = majorRepository;
        this.levelRepository = levelRepository;
    }

//    @PostConstruct
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

        Major majorSoftware = new Major("1", "Software Engineering", LevelEnum.UNDERGRAD);
        majorRepository.save(majorSoftware);
        majorSoftware.addToChart(new PrerequisiteRelation(majorSoftware, math1, Set.of()));
        majorSoftware.addToChart(new PrerequisiteRelation(majorSoftware, phys1, Set.of()));
        majorSoftware.addToChart(new PrerequisiteRelation(majorSoftware, math2, Set.of(math1)));
        majorSoftware.addToChart(new PrerequisiteRelation(majorSoftware, phys2, Set.of(math1, phys1)));
        majorSoftware.addToChart(new PrerequisiteRelation(majorSoftware, prog, Set.of()));
        majorSoftware.addToChart(new PrerequisiteRelation(majorSoftware, ap, Set.of(prog)));
        majorSoftware.addToChart(new PrerequisiteRelation(majorSoftware, dm, Set.of(prog, math1)));
        majorSoftware.addToChart(new PrerequisiteRelation(majorSoftware, farsi, Set.of()));
        majorSoftware.addToChart(new PrerequisiteRelation(majorSoftware, akhlagh, Set.of()));
        majorRepository.save(majorSoftware);

        Major majorIT = new Major("1", "IT Engineering", LevelEnum.UNDERGRAD);
        majorRepository.save(majorIT);
        majorIT.addToChart(new PrerequisiteRelation(majorIT, math1, Set.of()));
        majorIT.addToChart(new PrerequisiteRelation(majorIT, phys1, Set.of()));
        majorIT.addToChart(new PrerequisiteRelation(majorIT, math2, Set.of(math1)));
        majorIT.addToChart(new PrerequisiteRelation(majorIT, phys2, Set.of(math1, phys1)));
        majorIT.addToChart(new PrerequisiteRelation(majorIT, prog, Set.of()));
        majorIT.addToChart(new PrerequisiteRelation(majorIT, ap, Set.of(prog)));
        majorIT.addToChart(new PrerequisiteRelation(majorIT, dm, Set.of(math1)));
        majorIT.addToChart(new PrerequisiteRelation(majorIT, farsi, Set.of()));
        majorIT.addToChart(new PrerequisiteRelation(majorIT, akhlagh, Set.of()));
        majorRepository.save(majorIT);

        Student student1 = new Student("810100000", new Name("Student 1"), majorSoftware)
                .setGrade("t1", math1, new Grade(10))
                .setGrade("t1", phys1, new Grade(12))
                .setGrade("t1", prog, new Grade(16.3))
                .setGrade("t1", farsi, new Grade(18.5))
                .setGrade("t1", akhlagh, new Grade(15));
        studentRepository.save(student1);

        Student student2 = new Student("810100001", new Name("Student 2"), majorSoftware)
                .setGrade("t1", math1, new Grade(13.2))
                .setGrade("t1", phys1, new Grade(8.3))
                .setGrade("t1", prog, new Grade(10.5))
                .setGrade("t1", akhlagh, new Grade(16));
        studentRepository.save(student2);

        Section math1_1 = new Section(math1, "01", 100);
        Section phys1_1 = new Section(phys1, "01", 100);
        Section math2_1 = new Section(math2, "01", 100);
        Section math2_2 = new Section(math2, "02", 100);
        Section phys2_1 = new Section(phys2, "01", 100);
        Section phys2_2 = new Section(phys2, "02", 100);
        Section ap_1 = new Section(ap, "01", 100);
        Section dm_1 = new Section(dm, "01", 100);
        Section akhlagh_1 = new Section(akhlagh, "01", 100);
        sectionRepository.saveAll(List.of(math1_1, phys1_1, math2_1, math2_2, phys2_1, phys2_2, ap_1, dm_1, akhlagh_1));
        
        EnrollmentList student1List = new EnrollmentList("Student 1 List", student1);
        student1List.addSections(math2_1, phys2_2, ap_1, dm_1);
        enrollmentListRepository.save(student1List);
        EnrollmentList student2List = new EnrollmentList("Student 2 List", student2);
        student2List.addSections(math2_1, phys1_1, ap_1, dm_1);
        student2List.setListAsFinal();
        enrollmentListRepository.save(student2List);
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
