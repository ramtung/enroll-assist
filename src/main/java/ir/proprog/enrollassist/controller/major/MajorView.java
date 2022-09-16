package ir.proprog.enrollassist.controller.major;

import ir.proprog.enrollassist.controller.course.CourseView;
import ir.proprog.enrollassist.domain.entity.Course;
import ir.proprog.enrollassist.domain.entity.Major;
import ir.proprog.enrollassist.domain.entity.PrerequisiteRelation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
public class MajorView {
    private Long id;
    private String majorNumber;
    private String title;
    private String levelEnum;
    private List<PrerequisiteRelationView> chart = new ArrayList<>();

    public MajorView(@NonNull String majorNumber, @NonNull String title, @NonNull String levelEnum) {
        this.majorNumber = majorNumber;
        this.title = title;
        this.levelEnum = levelEnum;
    }

    public MajorView(@NonNull Major major) {
        this.majorNumber = major.getMajorNumber();
        this.title = major.getTitle().getName();
        for (PrerequisiteRelation relation : major.getChart()) {
            Set<CourseView> preCourseViewSet = new HashSet<>();
            for (Course preCourse : relation.getPrerequisites()) {
                preCourseViewSet.add(new ModelMapper().map(preCourse, CourseView.class));
            }
            this.chart.add(new PrerequisiteRelationView(new ModelMapper().map(relation.getMainCourse(), CourseView.class), preCourseViewSet));
        }
    }
}
