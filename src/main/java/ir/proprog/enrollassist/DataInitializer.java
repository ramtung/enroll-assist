package ir.proprog.enrollassist;

import ir.proprog.enrollassist.util.SampleDataInitializer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DataInitializer implements ApplicationRunner {
    private SampleDataInitializer sampleDataInitializer;

    public DataInitializer(SampleDataInitializer sampleDataInitializer) {
        this.sampleDataInitializer = sampleDataInitializer;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        sampleDataInitializer.populate();
    }
}
