package ir.proprog.enrollassist.domain.valueobject.schedule;

import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Time;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // as required by JPA, don't use it in your code
@Getter
@Access(AccessType.FIELD)
public class TimeSchedule implements Comparable<TimeSchedule> {
    private Time fromTime;
    private Time toTime;

    public TimeSchedule(@NotNull Time fromTime, @NotNull Time toTime) {
        if (!fromTime.before(toTime)) {
            throw new IllegalArgumentException("Time Schedule is not valid");
        }
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSchedule that = (TimeSchedule) o;
        return !(this.fromTime.equals(that.toTime) || this.fromTime.after(that.toTime)
                || that.fromTime.equals(this.toTime) || that.fromTime.after(this.toTime));
    }

    @Override
    public int hashCode() {
        return fromTime.hashCode() + toTime.hashCode();
    }

    @Override
    public int compareTo(TimeSchedule that) {
        return !this.fromTime.equals(that.fromTime)
                ? this.fromTime.compareTo(that.fromTime)
                : this.toTime.compareTo(that.toTime);
    }
}
