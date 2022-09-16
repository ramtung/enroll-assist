package ir.proprog.enrollassist.builder;

import ir.proprog.enrollassist.controller.major.MajorView;
import ir.proprog.enrollassist.domain.entity.LevelEnum;

public class MajorViewBuilder {

    private String majorNumber = "1122";
    private String title = "SoftwareEngineer";
    private String levelEnum = LevelEnum.UNDERGRAD.name();

    public MajorViewBuilder withMajorNumber(String majorNumber) {
        this.majorNumber = majorNumber;
        return this;
    }

    public MajorViewBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public MajorView build() {
        return new MajorView(this.majorNumber, this.title, this.levelEnum);
    }

    public static MajorView createSomeMajorView(){
        return new MajorViewBuilder().build();
    }
}
