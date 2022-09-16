package ir.proprog.enrollassist.controller.section;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import ir.proprog.enrollassist.domain.entity.Section;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@XStreamAlias("Section")
public class SectionXmlDTO {
    @XStreamAsAttribute
    private String sectionNo;
    @XStreamAsAttribute
    private String courseNumber;
    @XStreamAlias("ExamStart")
    private String examStart;
    @XStreamAlias("ExamEnd")
    private String examEnd;
    @XStreamAlias("Capacity")
    private int capacity;
    @XStreamAlias("ClassSchedule")
    private List<SessionTimeXmlDTO> classSchedules;

    public SectionXmlDTO(Section section) {
        this.sectionNo = section.getSectionNo();
        this.courseNumber = section.getCourse().getCourseNumber();
        this.examStart = section.getExamSchedule().getExamDate().toString() + "T" +
                section.getExamSchedule().getTimeSchedule().getFromTime().toString();
        this.examEnd = section.getExamSchedule().getExamDate().toString() + "T" +
                section.getExamSchedule().getTimeSchedule().getToTime().toString();
        this.classSchedules = new ArrayList<>();
        this.capacity = section.getCapacity();
        section.getClassScheduleList().forEach(o -> this.classSchedules.add(new SessionTimeXmlDTO(o)));
    }
}
