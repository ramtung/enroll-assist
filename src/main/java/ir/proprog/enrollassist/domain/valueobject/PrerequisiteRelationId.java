package ir.proprog.enrollassist.domain.valueobject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // as required by JPA, don't use it in your code
@Getter
public class PrerequisiteRelationId implements Serializable {

    private long majorId;
    private long mainCourseId;

    public PrerequisiteRelationId(long majorId, long mainCourseId) {
        this.majorId = majorId;
        this.mainCourseId = mainCourseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrerequisiteRelationId prerequisiteRelationId = (PrerequisiteRelationId) o;
        return this.majorId == prerequisiteRelationId.majorId
                && this.mainCourseId == prerequisiteRelationId.mainCourseId;
    }

    @Override
    public int hashCode() {
        return (majorId + "").hashCode() + (mainCourseId + "").hashCode();
    }
}
