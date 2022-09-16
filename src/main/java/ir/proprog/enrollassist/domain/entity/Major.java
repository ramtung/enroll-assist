package ir.proprog.enrollassist.domain.entity;

import ir.proprog.enrollassist.domain.exception.BusinessException;
import ir.proprog.enrollassist.domain.factory.LevelFactory;
import ir.proprog.enrollassist.domain.valueobject.Name;
import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // as required by JPA, don't use it in your code
@Getter
public class Major {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String majorNumber;
    @Embedded
    private Name title;
    @ManyToOne
    private Level level;
    @OneToMany
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<PrerequisiteRelation> chart = new ArrayList<>();

    public Major(@NonNull String majorNumber, @NonNull String title, @NonNull LevelEnum levelEnum) {
        checkFields(majorNumber, title);
        this.majorNumber = majorNumber;
        this.title = new Name(title);
        this.level = LevelFactory.getLevel(levelEnum);
    }

    public void addToChart(PrerequisiteRelation prerequisiteRelation) {
        if (chart.stream().anyMatch(relation -> relation.equals(prerequisiteRelation))) {
            throw new IllegalArgumentException("this node added previously");
        }
        this.chart.add(prerequisiteRelation);
    }

    private void checkFields(String majorNumber, String title) {
        if (majorNumber.equals(""))
            throw new IllegalArgumentException("Major number cannot be empty");
        else if (!majorNumber.matches("\\d+"))
            throw new IllegalArgumentException("Major number must contain only numbers");
        else if (BigDecimal.ZERO.equals(new BigDecimal(majorNumber)))
            throw new IllegalArgumentException("Major number can not be zero");
        if (title.equals(""))
            throw new IllegalArgumentException("Major must have a title");
        else if (!title.matches(".*[a-zA-Z].*"))
            throw new IllegalArgumentException("Major title must contain at least one character");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Major major = (Major) o;
        return majorNumber.equals(major.majorNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(majorNumber);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", majorNumber, title);
    }

    private Optional<PrerequisiteRelation> findCourseInChart(Course mainCourse) {
        return getChart().stream().filter(node -> node.getMainCourse().getCourseNumber().equals(mainCourse.getCourseNumber())).findFirst();
    }

    public List<Course> getListOfPrerequisiteOfCourse(Course course) {
        return getChart().stream()
                .filter(prerequisiteRelation -> prerequisiteRelation.getMainCourse().equals(course))
                .map(PrerequisiteRelation::getPrerequisites)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private Course findCourseMakingCycleInChart(List<Course> headCourses, Course mainCourse) throws BusinessException {
        Course course = null;
        PrerequisiteRelation node = findCourseInChart(mainCourse)
                .orElseThrow(() -> new BusinessException("In this major there wasn't any mainCourse with this number: " + mainCourse.getCourseNumber(), mainCourse));
        headCourses.add(node.getMainCourse());
        if (node.getPrerequisites() != null && node.getPrerequisites().size() > 0) {
            for (Course pre : node.getPrerequisites()) {
                if (headCourses.contains(pre))
                    course = node.getMainCourse();
                else
                    course = findCourseMakingCycleInChart(headCourses, pre);
                if (course != null)
                    throw new BusinessException("There was a loop between courses, please check course number: " + course.getCourseNumber(), course);
            }
        }
        headCourses.remove(node);
        return course;
    }

    public void findCourseMakingCycleInChart(Course mainCourse) throws BusinessException {
        List<Course> headCourses = new ArrayList<>();
        findCourseMakingCycleInChart(headCourses, mainCourse);
    }

    public List<Course> findTakeableCourses() {
        return chart.stream().map(PrerequisiteRelation::getMainCourse).collect(Collectors.toList());
    }
}
