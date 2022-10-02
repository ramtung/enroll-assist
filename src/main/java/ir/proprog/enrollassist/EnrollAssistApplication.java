package ir.proprog.enrollassist;

import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EnrollAssistApplication {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
    public static void main(String[] args) {
        new SpringApplicationBuilder(EnrollAssistApplication.class)
                .profiles("dev")
                .run(args);
    }

}
