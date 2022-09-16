package ir.proprog.enrollassist.domain.valueobject.schedule;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionSchedule implements Comparable<SectionSchedule> {
    private String courseName;
    private String sectionNo;
    private TimeSchedule timeSchedule;

    public SectionSchedule(String courseName, String sectionNo, TimeSchedule timeSchedule) {
        this.courseName = courseName;
        this.sectionNo = sectionNo;
        this.timeSchedule = timeSchedule;
    }

    @Override
    public int compareTo(SectionSchedule that) {
        return this.timeSchedule.compareTo(that.timeSchedule) != 0
                ? this.timeSchedule.compareTo(that.timeSchedule)
                : this.courseName.compareTo(that.courseName) != 0
                ? this.courseName.compareTo(that.courseName)
                : this.sectionNo.compareTo(that.sectionNo);
    }

    @Override
    public String toString() {
        return getStringBuilder().toString();
    }

    public StringBuilder getStringBuilder() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\t")
                .append(timeSchedule.getFromTime())
                .append("-")
                .append(timeSchedule.getToTime())
                .append("\t")
                .append(courseName)
                .append("\t")
                .append(sectionNo);
        return stringBuilder;
    }
}
