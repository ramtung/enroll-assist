package ir.proprog.enrollassist.controller.schedule;

import ir.proprog.enrollassist.domain.valueobject.schedule.ExamSchedule;
import ir.proprog.enrollassist.domain.valueobject.schedule.TimeSchedule;
import lombok.Getter;

import java.sql.Date;

@Getter
public class ExamScheduleView {
    private Date examDate;
    private TimeSchedule timeSchedule;

    public ExamScheduleView(ExamSchedule examSchedule) {
        this.examDate = examSchedule.getExamDate();
        this.timeSchedule = examSchedule.getTimeSchedule();
    }
}
