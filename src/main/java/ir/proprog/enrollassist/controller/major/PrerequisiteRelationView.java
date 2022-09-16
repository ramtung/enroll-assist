package ir.proprog.enrollassist.controller.major;

import ir.proprog.enrollassist.controller.course.CourseView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
public class PrerequisiteRelationView {
    private CourseView courseView;
    private Set<CourseView> prerequisites;

    public PrerequisiteRelationView(CourseView courseView, Set<CourseView> prerequisites) {
        this.courseView = courseView;
        this.prerequisites = prerequisites;
    }
}
