package ir.proprog.enrollassist.builder;

import ir.proprog.enrollassist.domain.entity.ClassSchedule;
import ir.proprog.enrollassist.domain.valueobject.schedule.TimeSchedule;
import ir.proprog.enrollassist.domain.valueobject.WeekDayEnum;

import static ir.proprog.enrollassist.builder.TimeScheduleBuilder.createSomeTimeSchedule;

public class ClassScheduleBuilder {
    private WeekDayEnum weekDay = WeekDayEnum.WEDNESDAY;
    private TimeSchedule timeSchedule = createSomeTimeSchedule();

    public ClassScheduleBuilder withWeekDay(WeekDayEnum weekDay) {
        this.weekDay = weekDay;
        return this;
    }

    public ClassScheduleBuilder withTimeSchedule(TimeSchedule timeSchedule) {
        this.timeSchedule = timeSchedule;
        return this;
    }

    public ClassSchedule build() {
        return new ClassSchedule(weekDay, timeSchedule);
    }

    public static ClassScheduleBuilder builder() {
        return new ClassScheduleBuilder();
    }

    public static ClassSchedule createSomeClassSchedule() {
        return new ClassScheduleBuilder().build();
    }
}
