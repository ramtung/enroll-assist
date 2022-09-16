package ir.proprog.enrollassist.builder;

import ir.proprog.enrollassist.controller.major.MajorView;
import ir.proprog.enrollassist.controller.student.StudentView;

import static ir.proprog.enrollassist.builder.MajorViewBuilder.createSomeMajorView;


public class StudentViewBuilder {

    private String number = "810101400";
    private String name = "STUDENT";
    private MajorView major = createSomeMajorView();

    public StudentViewBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public StudentViewBuilder withNumber(String number) {
        this.number = number;
        return this;
    }

    public StudentViewBuilder withMajor(MajorView major) {
        this.major = major;
        return this;
    }

    public StudentView build() {
        return new StudentView(this.number, this.name, this.major);
    }

    public static StudentView createSomeStudentView(){
        return new StudentViewBuilder().build();
    }
}
