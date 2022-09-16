package ir.proprog.enrollassist.builder;

import ir.proprog.enrollassist.domain.entity.Course;
import ir.proprog.enrollassist.domain.valueobject.Name;

public class CourseBuilder {

    private String courseNumber;
    private Name title = new Name("Math-1");
    private int credits = 3;

    private static int lastCourseNumber = 1;

    private String getNextCourseNumber() {
        return Integer.toString(++lastCourseNumber);
    }

    private CourseBuilder() {
        this.courseNumber = getNextCourseNumber();
    }

    public static CourseBuilder aCourse() {
        return new CourseBuilder();
    }

    public CourseBuilder withCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
        return this;
    }

    public CourseBuilder withTitle(String title) {
        this.title = new Name(title);
        return this;
    }

    public CourseBuilder withCredits(int credits) {
        this.credits = credits;
        return this;
    }

    public Course build() {
        return new Course(this.courseNumber, this.title, this.credits);
    }

    public static Course someCourse() {
        return new CourseBuilder().build();
    }

}
