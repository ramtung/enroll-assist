package ir.proprog.enrollassist.domain.valueobject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Name {
    private String name;

    public Name(String name) {
        this.name = name;
    }
}
