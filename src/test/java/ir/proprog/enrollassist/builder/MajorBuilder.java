package ir.proprog.enrollassist.builder;

import ir.proprog.enrollassist.domain.entity.*;

import java.util.*;

import static ir.proprog.enrollassist.builder.PrerequisiteRelationBuilder.createSomePrerequisiteRelationForSpecificMajor;
import static java.util.stream.Collectors.toList;

public class MajorBuilder {

    private String title = "SoftwareEngineer";

    private LevelEnum levelEnum = LevelEnum.UNDERGRAD;

    private Major major;

    private static int lastMajorNumber = 1;

    private String getNextMajorNumber() {
        return Integer.toString(++lastMajorNumber);
    }

    private MajorBuilder() {
        this.major = new Major(getNextMajorNumber(), this.title, this.levelEnum);
    }

    public static MajorBuilder builder() {
        return new MajorBuilder();
    }

    public MajorBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public Major build() {
        return major;
    }

    public static Major aMajor() {
        return new MajorBuilder().build();
    }

    public static Major createSomeMajorWithChart() {
        Major major = new MajorBuilder().build();
        List<PrerequisiteRelation> chart = createChart(major);
        chart.forEach(major::addToChart);
        return major;
    }

    private static List<PrerequisiteRelation> createChart(Major major) {
        List<PrerequisiteRelation> chart = new ArrayList<>();
        chart.add(createSomePrerequisiteRelationForSpecificMajor(major));
        chart.add(createSomePrerequisiteRelationForSpecificMajor(major));
        chart.add(createSomePrerequisiteRelationForSpecificMajor(major));
        List<Course> mainCourses = chart.stream().map(PrerequisiteRelation::getMainCourse).collect(toList());
        Set<Course> preCourses = new HashSet<>();
        chart.stream().map(PrerequisiteRelation::getPrerequisites).forEach(preCourses::addAll);
        List<Course> courseWithNoPre = preCourses.stream().filter(course -> !mainCourses.contains(course)).collect(toList());
        for (Course course : courseWithNoPre)
            chart.add(PrerequisiteRelationBuilder.aPrereqRel().withMainCourse(course).withMajor(major).build());
        return chart;
    }

    public MajorBuilder withChart(Course mainCourse, Course... prerequisites) {
        PrerequisiteRelation newChart = PrerequisiteRelationBuilder.aPrereqRel()
                .withMajor(this.major)
                .withMainCourse(mainCourse)
                .withPrerequisites(prerequisites)
                .build();
        this.major.addToChart(newChart);
        return this;
    }

    public MajorBuilder withMajor(Major major) {
        this.major = major;
        return this;
    }
}
