package ir.proprog.enrollassist.builder;

import ir.proprog.enrollassist.domain.entity.Course;
import ir.proprog.enrollassist.domain.entity.Major;
import ir.proprog.enrollassist.domain.entity.Student;
import ir.proprog.enrollassist.domain.entity.StudyRecord;
import ir.proprog.enrollassist.domain.valueobject.Grade;
import ir.proprog.enrollassist.domain.valueobject.Name;

import static ir.proprog.enrollassist.builder.MajorBuilder.aMajor;

public class StudentBuilder {

    private String number;
    private Name studentName;
    private Major major = aMajor();
    private StudyRecord[] grades = new StudyRecord[0];


    private static String baseName = "STUDENT";
    private static int lastNumber = 1;

    private String getNextNumber() {
        return Integer.toString(++lastNumber);
    }

    public StudentBuilder() {
        this.number = getNextNumber();
        this.studentName = new Name(baseName.concat(this.number));
    }

    public static StudentBuilder aStudent() {
        return new StudentBuilder();
    }

    public StudentBuilder withName(Name name) {
        this.studentName = name;
        return this;
    }

    public StudentBuilder withNumber(String number) {
        this.number = number;
        return this;
    }

    public StudentBuilder withMajor(Major major) {
        this.major = major;
        return this;
    }

    public static StudyRecord record(String term, Course course, double grade) {
        return new StudyRecord(term, course, new Grade(grade));
    }
    public StudentBuilder withTranscript(StudyRecord...records) {
        grades = new StudyRecord[records.length];
        for (int i = 0; i < grades.length; i++) {
            grades[i] = records[i];
        }
        return this;
    }

    public Student build() {
        Student result = new Student(this.number, this.studentName, this.major);
        for (StudyRecord sr : grades) {
            result.setGrade(sr.getTerm(), sr.getCourse(), sr.getGrade());
        }
        return result;
    }

    public static Student createSomeStudent() {
        return new StudentBuilder().build();
    }
}
