package ir.proprog.enrollassist.builder;

import ir.proprog.enrollassist.domain.valueobject.schedule.ExamSchedule;
import ir.proprog.enrollassist.domain.valueobject.schedule.TimeSchedule;

import java.sql.Date;

import static ir.proprog.enrollassist.builder.TimeScheduleBuilder.createSomeTimeSchedule;

public class ExamScheduleBuilder {
    private Date examDate = Date.valueOf("2022-01-08");
    private TimeSchedule timeSchedule = createSomeTimeSchedule();

    public ExamScheduleBuilder withExamDate(Date examDate) {
        this.examDate = examDate;
        return this;
    }

    public ExamScheduleBuilder withTimeSchedule(TimeSchedule timeSchedule) {
        this.timeSchedule = timeSchedule;
        return this;
    }

    public ExamSchedule build() {
        return new ExamSchedule(this.examDate, this.timeSchedule);
    }

    public static ExamSchedule anExamSchedule(){
        return new ExamScheduleBuilder().build();
    }
}
