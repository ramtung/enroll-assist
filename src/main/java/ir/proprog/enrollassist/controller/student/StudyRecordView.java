package ir.proprog.enrollassist.controller.student;

import ir.proprog.enrollassist.controller.course.CourseView;
import ir.proprog.enrollassist.domain.entity.StudyRecord;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@NoArgsConstructor
public class StudyRecordView {
    private String term;
    private CourseView course;
    private double grade;

    public StudyRecordView(StudyRecord studyRecord) {
        this.term = studyRecord.getTerm();
        this.course = new ModelMapper().map(studyRecord.getCourse(), CourseView.class);
        this.grade = studyRecord.getGrade().getGrade();
    }

    public StudyRecordView(String term, CourseView course, double grade) {
        this.term = term;
        this.course = course;
        this.grade = grade;
    }
}
