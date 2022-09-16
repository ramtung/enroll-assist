package ir.proprog.enrollassist.domain.valueobject.schedule;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // as required by JPA, don't use it in your code
@Getter
public class ExamSchedule {
    private Date examDate;
    @Embedded
    private TimeSchedule timeSchedule;

    public ExamSchedule(Date examDate, TimeSchedule timeSchedule) {
        this.examDate = examDate;
        this.timeSchedule = timeSchedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExamSchedule examSchedule = (ExamSchedule) o;
        return this.examDate.equals(examSchedule.examDate)
                && this.timeSchedule.equals(examSchedule.timeSchedule);
    }

    @Override
    public int hashCode() {
        return examDate.hashCode() + timeSchedule.hashCode();
    }
}
