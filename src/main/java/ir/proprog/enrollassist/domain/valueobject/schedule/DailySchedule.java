package ir.proprog.enrollassist.domain.valueobject.schedule;

import com.sun.istack.NotNull;
import ir.proprog.enrollassist.domain.valueobject.WeekDayEnum;
import lombok.Getter;

import java.util.List;

@Getter
public class DailySchedule implements Comparable<DailySchedule> {
    private WeekDayEnum dayOfWeek;
    private List<SectionSchedule> sectionSchedules;

    public DailySchedule(@NotNull WeekDayEnum dayOfWeek, @NotNull List<SectionSchedule> sectionSchedules) {
        sectionSchedules.sort(SectionSchedule::compareTo);
        this.dayOfWeek = dayOfWeek;
        this.sectionSchedules = sectionSchedules;
    }

    @Override
    public String toString() {
        return getStringBuilder().toString();
    }

    public StringBuilder getStringBuilder() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.dayOfWeek)
                .append(System.lineSeparator());
        sectionSchedules.stream().map(SectionSchedule::getStringBuilder).forEach(sectionSchedule -> stringBuilder.append(sectionSchedule).append(System.lineSeparator()));
        return stringBuilder;
    }

    @Override
    public int compareTo(DailySchedule that) {
        return !this.dayOfWeek.equals(that.dayOfWeek) ? this.dayOfWeek.compareTo(that.dayOfWeek) : 1;
    }
}
