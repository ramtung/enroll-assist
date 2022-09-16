package ir.proprog.enrollassist.controller.section;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import ir.proprog.enrollassist.domain.entity.ClassSchedule;
import ir.proprog.enrollassist.domain.valueobject.WeekDayEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;

@Getter
@Setter
@NoArgsConstructor
@XStreamAlias("SessionTime")
public class SessionTimeXmlDTO {
    @XStreamAsAttribute
    private WeekDayEnum weekDay;
    @XStreamAsAttribute
    private Time startTime;
    @XStreamAsAttribute
    private Time endTime;

    public SessionTimeXmlDTO(ClassSchedule classSchedule) {
        this.weekDay = classSchedule.getWeekDay();
        this.startTime = classSchedule.getTimeSchedule().getFromTime();
        this.endTime = classSchedule.getTimeSchedule().getToTime();
    }
}
