package ir.proprog.enrollassist.domain.entity;

import com.sun.istack.NotNull;
import ir.proprog.enrollassist.domain.valueobject.PrerequisiteRelationId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // as required by JPA, don't use it in your code
@Getter
public class PrerequisiteRelation {
    @EmbeddedId
    private PrerequisiteRelationId id;

    @MapsId("majorId")
    @ManyToOne
    private Major major;
    @MapsId("mainCourseId")
    @ManyToOne
    private Course mainCourse;
    @ManyToMany
    private Set<Course> prerequisites;

    public PrerequisiteRelation(@NotNull Major major, @NotNull Course mainCourse, Set<Course> prerequisites){
        this.major = major;
        this.mainCourse = mainCourse;
        this.prerequisites = prerequisites;
        this.id = new PrerequisiteRelationId(major.getId(), mainCourse.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrerequisiteRelation prerequisiteRelation = (PrerequisiteRelation) o;
        return this.major.equals(prerequisiteRelation.major)
                && this.mainCourse.equals(prerequisiteRelation.mainCourse)
                && prerequisiteRelation.prerequisites.containsAll(this.prerequisites);
    }

    @Override
    public int hashCode() {
        return mainCourse.hashCode() + prerequisites.hashCode();
    }
}
