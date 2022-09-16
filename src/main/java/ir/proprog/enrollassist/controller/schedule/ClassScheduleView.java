package ir.proprog.enrollassist.controller.schedule;

import ir.proprog.enrollassist.domain.entity.ClassSchedule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClassScheduleView {
    private Long id;
    private String weekDay;
    private String fromTime;
    private String toTime;

    public ClassScheduleView(ClassSchedule classSchedule) {
        this.weekDay = classSchedule.getWeekDay().toString();
        this.fromTime = classSchedule.getTimeSchedule().getFromTime().toString();
        this.toTime = classSchedule.getTimeSchedule().getToTime().toString();
    }

    public ClassScheduleView(String weekDay, String fromTime, String toTime) {
        this.weekDay = weekDay;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }
}
