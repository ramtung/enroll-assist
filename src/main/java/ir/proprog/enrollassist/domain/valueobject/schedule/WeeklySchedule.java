package ir.proprog.enrollassist.domain.valueobject.schedule;

import com.sun.istack.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class WeeklySchedule {
    private String studentNumber;
    private String enrollmentListName;
    private List<DailySchedule> dailySchedules;

    public WeeklySchedule(@NotNull String studentNumber, @NotNull String enrollmentListName, @NotNull List<DailySchedule> dailySchedules) {
        dailySchedules.sort(DailySchedule::compareTo);
        this.studentNumber = studentNumber;
        this.enrollmentListName = enrollmentListName;
        this.dailySchedules = dailySchedules;
    }

    @Override
    public String toString() {
        return getStringBuilder().toString();
    }

    public StringBuilder getStringBuilder() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.studentNumber)
                .append(" - ")
                .append(this.enrollmentListName)
                .append(System.lineSeparator());
        dailySchedules.stream().map(DailySchedule::getStringBuilder).forEach(stringBuilder::append);
        return stringBuilder;
    }
}
