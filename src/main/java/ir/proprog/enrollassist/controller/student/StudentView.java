package ir.proprog.enrollassist.controller.student;

import ir.proprog.enrollassist.controller.major.MajorView;
import ir.proprog.enrollassist.domain.entity.Student;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class StudentView {
    private Long id;
    private String studentNumber;
    private String studentName;
    private Set<StudyRecordView> grades = new HashSet<>();
    private MajorView major;

    public StudentView(Student student) {
        this.id = student.getId();
        this.studentNumber = student.getStudentNumber();
        this.studentName = student.getStudentName().getName();
        this.grades = student.getGrades().stream().map(grade -> new ModelMapper().map(student.getGrades(), StudyRecordView.class)).collect(Collectors.toSet());
        this.major = new ModelMapper().map(student.getMajor(), MajorView.class);
    }

    public StudentView(String studentNumber, String studentName, MajorView major) {
        this.studentNumber = studentNumber;
        this.studentName = studentName;
        this.major = major;
    }
}
