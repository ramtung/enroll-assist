package ir.proprog.enrollassist.domain.entity;

import com.sun.istack.NotNull;
import ir.proprog.enrollassist.domain.valueobject.schedule.TimeSchedule;
import ir.proprog.enrollassist.domain.valueobject.WeekDayEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // as required by JPA, don't use it in your code
@Getter
public class ClassSchedule {
    private WeekDayEnum weekDay;
    @Embedded
    private TimeSchedule timeSchedule;

    public ClassSchedule(@NotNull WeekDayEnum weekDay, @NotNull TimeSchedule timeSchedule) {
        if (WeekDayEnum.FRIDAY.equals(weekDay)) {
            throw new IllegalArgumentException("Friday is the Holiday!");
        }
        this.weekDay = weekDay;
        this.timeSchedule = timeSchedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassSchedule classSchedule = (ClassSchedule) o;
        return weekDay.equals(classSchedule.weekDay) && timeSchedule.equals(classSchedule.timeSchedule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weekDay, timeSchedule);
    }

    public boolean overlaps(Object o) {
        if (this == o) return false;
        if (o == null || getClass() != o.getClass()) return false;
        ClassSchedule that = (ClassSchedule) o;
        return this.weekDay.equals(that.weekDay)
                && this.timeSchedule.equals(that.timeSchedule);
    }
}
