package ir.proprog.enrollassist.controller.course;

import ir.proprog.enrollassist.domain.valueobject.Name;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
public class CourseView {
    private Long id;
    private String courseNumber;
    private String title;
    private int credits;

    public CourseView(@NonNull String courseNumber, @NonNull String title, int credits) {
        this.courseNumber = courseNumber;
        this.title = new Name(title).getName();
        this.credits = credits;
    }
}
