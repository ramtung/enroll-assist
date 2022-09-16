package ir.proprog.enrollassist.builder;

import ir.proprog.enrollassist.domain.valueobject.schedule.TimeSchedule;

import java.sql.Time;

public class TimeScheduleBuilder {
    private String fromTime = "08:00:00";
    private String toTime = "10:00:00";

    private TimeScheduleBuilder() {
    }

    public TimeScheduleBuilder withFromTime(String fromTime) {
        this.fromTime = fromTime;
        return this;
    }

    public TimeScheduleBuilder withToTime(String toTime) {
        this.toTime = toTime;
        return this;
    }

    public TimeSchedule build() {
        return new TimeSchedule(Time.valueOf(this.fromTime), Time.valueOf(this.toTime));
    }

    public static TimeScheduleBuilder builder() {
        return new TimeScheduleBuilder();
    }

    public static TimeSchedule createSomeTimeSchedule() {
        return new TimeScheduleBuilder().build();
    }
}
