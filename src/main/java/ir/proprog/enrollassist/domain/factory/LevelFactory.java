package ir.proprog.enrollassist.domain.factory;

import ir.proprog.enrollassist.domain.entity.*;

import java.util.Objects;

public class LevelFactory {
    private static UnderGradLevel underGradLevel;
    private static MasterLevel masterLevel;
    private static DoctorateLevel doctorateLevel;

    public static Level getLevel(LevelEnum levelEnum) {
        switch (levelEnum) {
            case UNDERGRAD:
                if (!Objects.nonNull(underGradLevel)) {
                    underGradLevel = new UnderGradLevel();
                }
                return underGradLevel;
            case MASTER:
                if (!Objects.nonNull(masterLevel)) {
                    masterLevel = new MasterLevel();
                }
                return masterLevel;
            case DOCTORATE:
                if (!Objects.nonNull(doctorateLevel)) {
                    doctorateLevel = new DoctorateLevel();
                }
                return doctorateLevel;
            default:
                return null;
        }
    }

    /***  only use in test data initializer  ***/
    public static void reset() {
        underGradLevel = null;
        masterLevel = null;
        doctorateLevel = null;
    }
}
