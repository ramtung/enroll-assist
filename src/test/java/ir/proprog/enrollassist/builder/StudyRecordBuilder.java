package ir.proprog.enrollassist.builder;

import ir.proprog.enrollassist.domain.entity.Course;
import ir.proprog.enrollassist.domain.entity.StudyRecord;
import ir.proprog.enrollassist.domain.valueobject.Grade;

import static ir.proprog.enrollassist.builder.CourseBuilder.someCourse;

public class StudyRecordBuilder {
    private String term;
    private Course course = someCourse();
    private Grade grade;

    public static StudyRecordBuilder builder() {
        return new StudyRecordBuilder();
    }

    public StudyRecordBuilder withTerm(String term) {
        this.term = term;
        return this;
    }

    public StudyRecordBuilder withGrade(Grade grade) {
        this.grade = grade;
        return this;
    }

    public StudyRecord build() {
        return new StudyRecord(this.term, this.course, this.grade);
    }

    public static StudyRecord createSomeStudyRecord() {
        return new StudyRecordBuilder().build();
    }
}
