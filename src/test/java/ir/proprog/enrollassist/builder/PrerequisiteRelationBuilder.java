package ir.proprog.enrollassist.builder;

import ir.proprog.enrollassist.domain.entity.Course;
import ir.proprog.enrollassist.domain.entity.Major;
import ir.proprog.enrollassist.domain.entity.PrerequisiteRelation;

import java.util.*;

import static ir.proprog.enrollassist.builder.CourseBuilder.someCourse;
import static ir.proprog.enrollassist.builder.MajorBuilder.aMajor;

public class PrerequisiteRelationBuilder {

    private Major major;
    private Course mainCourse;
    private Set<Course> prerequisites;

    private PrerequisiteRelationBuilder() {
        this.mainCourse = someCourse();
        this.major = aMajor();
    }

    public static PrerequisiteRelationBuilder aPrereqRel() {
        return new PrerequisiteRelationBuilder();
    }

    public PrerequisiteRelationBuilder withMajor(Major major) {
        this.major = major;
        return this;
    }

    public PrerequisiteRelationBuilder withMainCourse(Course mainCourse) {
        this.mainCourse = mainCourse;
        return this;
    }

    public PrerequisiteRelationBuilder withPrerequisites(Course... prerequisites) {
        this.prerequisites = new HashSet<>();
        this.prerequisites.addAll(Arrays.asList(prerequisites));
        return this;
    }

    public PrerequisiteRelation build() {
        PrerequisiteRelation prerequisiteRelation = new PrerequisiteRelation(this.major, this.mainCourse, this.prerequisites);
        return prerequisiteRelation;
    }

    public static PrerequisiteRelation createSomePrerequisiteRelation() {
        PrerequisiteRelationBuilder preBuilder = new PrerequisiteRelationBuilder();

        Set<Course> pres = new HashSet<>(3);
        pres.add(someCourse());
        pres.add(someCourse());
        pres.add(someCourse());
        preBuilder.mainCourse = someCourse();
        preBuilder.prerequisites = pres;
        preBuilder.major = aMajor();

        return preBuilder.build();
    }

    public static PrerequisiteRelation createSomePrerequisiteRelationForSpecificMajor(Major major) {
        PrerequisiteRelationBuilder preBuilder = new PrerequisiteRelationBuilder();

        Set<Course> pres = new HashSet<>(3);
        pres.add(someCourse());
        pres.add(someCourse());
        pres.add(someCourse());
        preBuilder.mainCourse = someCourse();
        preBuilder.prerequisites = pres;
        preBuilder.major = major;

        return preBuilder.build();
    }
}
