package ir.proprog.enrollassist.integrationtest;

import ir.proprog.enrollassist.util.EnrollmentListTestDataInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EnrollmentPerformanceTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private EnrollmentListTestDataInitializer enrollmentListTestDataInitializer;

    @BeforeEach
    public void populate() {
        enrollmentListTestDataInitializer.populateListOfEnrollmentList(50);
    }

    @AfterEach
    public void cleanUp() {
        enrollmentListTestDataInitializer.deleteAll();
    }

    @Test
    @Disabled
    void Given_repository_When_enrollStudent_Then_checkPerformance() throws Exception {
        mvc.perform(get("/lists/enrollment")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();
    }
}
