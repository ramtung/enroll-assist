package ir.proprog.enrollassist.controller.section;

import ir.proprog.enrollassist.controller.course.CourseView;
import ir.proprog.enrollassist.controller.schedule.ClassScheduleView;
import ir.proprog.enrollassist.controller.schedule.ExamScheduleView;
import ir.proprog.enrollassist.domain.entity.Section;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SectionView {
    private Long sectionId;
    private String sectionNo;
    private Long courseId;
    private String courseNumber;
    private String courseTitle;
    private int courseCredits;
    private CourseView courseView;
    private List<ClassScheduleView> classScheduleViewList = new ArrayList<>();
    private ExamScheduleView examScheduleView;
    private int capacity;

    public void setExamSchedule(ExamScheduleView examScheduleView) {
        this.examScheduleView = examScheduleView;
    }

    public SectionView(Section section) {
        this.sectionId = section.getId();
        this.sectionNo = section.getSectionNo();
        this.courseId = section.getCourse().getId();
        this.courseNumber = section.getCourse().getCourseNumber();
        this.courseTitle = section.getCourse().getTitle().getName();
        this.courseCredits = section.getCourse().getCredits();
        if (section.getExamSchedule() != null)
            setExamSchedule(new ExamScheduleView(section.getExamSchedule()));
        this.capacity = section.getCapacity();
    }

    public SectionView(String sectionNo, CourseView courseView) {
        this.sectionNo = sectionNo;
        this.courseView = courseView;
        this.courseId = courseView.getId();
        this.courseNumber = courseView.getCourseNumber();
        this.courseTitle = courseView.getTitle();
        this.courseCredits = courseView.getCredits();
    }

    public void addClassScheduleView(ClassScheduleView classScheduleView) {
        classScheduleViewList.add(classScheduleView);
    }

    @Override
    public String toString() {
        return "SectionView{" +
                "sectionId=" + sectionId +
                ", sectionNo='" + sectionNo + '\'' +
                ", courseId=" + courseId +
                ", courseNumber='" + courseNumber + '\'' +
                ", courseTitle='" + courseTitle + '\'' +
                ", courseCredits=" + courseCredits +
                ", courseView=" + courseView +
                ", capacity=" + capacity +
                '}';
    }
}
