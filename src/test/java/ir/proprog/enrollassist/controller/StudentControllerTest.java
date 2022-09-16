package ir.proprog.enrollassist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.proprog.enrollassist.builder.MajorBuilder;
import ir.proprog.enrollassist.builder.StudentViewBuilder;
import ir.proprog.enrollassist.controller.section.SectionController;
import ir.proprog.enrollassist.controller.student.StudentController;
import ir.proprog.enrollassist.controller.student.StudentView;
import ir.proprog.enrollassist.domain.entity.Student;
import ir.proprog.enrollassist.repository.MajorRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import ir.proprog.enrollassist.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.PostConstruct;
import java.util.Optional;

import static ir.proprog.enrollassist.builder.StudentBuilder.createSomeStudent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ModelMapper modelMapper;
    @MockBean
    private StudentRepository studentRepository;
    @MockBean
    SectionRepository sectionRepository;
    @MockBean
    private MajorRepository majorRepository;

    @Test
    public void Given_studentWithNumericName_When_addStudent_Then_throwException() throws Exception {
        StudentView studentView = new StudentViewBuilder().withName("321").build();
        given(majorRepository.findByMajorNumber(studentView.getMajor().getMajorNumber()))
                .willReturn(Optional.of(MajorBuilder.aMajor()));
        mvc.perform(post("/students/newStudent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(studentView)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isNotNull()
                                .hasMessageContaining("Student name must contain at least one character"));
    }

    @Test
    public void Given_studentWithAlphabeticNumber_When_addStudent_Then_throwException() throws Exception {
        StudentView studentView = new StudentViewBuilder().withNumber("cba").build();
        given(majorRepository.findByMajorNumber(studentView.getMajor().getMajorNumber()))
                .willReturn(Optional.of(MajorBuilder.aMajor()));
        mvc.perform(post("/students/newStudent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(studentView)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isNotNull()
                                .hasMessageContaining("Student number must contain only numbers"));
    }

    @Test
    public void Given_studentWithZeroNumber_When_addStudent_Then_throwException() throws Exception {
        StudentView studentView = new StudentViewBuilder().withNumber("000000").build();
        given(majorRepository.findByMajorNumber(studentView.getMajor().getMajorNumber()))
                .willReturn(Optional.of(MajorBuilder.aMajor()));
        mvc.perform(post("/students/newStudent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(studentView)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isNotNull()
                                .hasMessageContaining("Student number can not be zero"));
    }

    @Test
    public void Given_studentWithValidNameAndNumber_When_addStudent_Then_returnStudentView() throws Exception {
        StudentView studentView = new StudentViewBuilder().build();
        given(studentRepository.findByStudentNumber(studentView.getStudentNumber())).willReturn(Optional.empty());
        given(majorRepository.findByMajorNumber(studentView.getMajor().getMajorNumber()))
                .willReturn(Optional.of(MajorBuilder.aMajor()));
        mvc.perform(post("/students/newStudent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(studentView)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentNumber", is(studentView.getStudentNumber())))
                .andExpect(jsonPath("$.studentName", is(studentView.getStudentName())));
    }

    @Test
    public void Given_duplicateStudentNumber_When_addStudent_Then_throwException() throws Exception {
        Student student = createSomeStudent();
        StudentView studentView = new StudentViewBuilder().withNumber(student.getStudentNumber()).build();
        given(majorRepository.findByMajorNumber(studentView.getMajor().getMajorNumber()))
                .willReturn(Optional.of(MajorBuilder.aMajor()));
        given(studentRepository.findByStudentNumber(studentView.getStudentNumber())).willReturn(Optional.of(student));
        mvc.perform(post("/students/newStudent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(studentView)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isNotNull()
                                .hasMessageContaining("Student with this number already exists!"));
    }

    @Test
    public void Given_studentWithInvalidMajor_When_addStudent_Then_throwException() throws Exception {
        StudentView studentView = new StudentViewBuilder().build();
        given(majorRepository.findByMajorNumber(studentView.getMajor().getMajorNumber())).willReturn(Optional.empty());
        mvc.perform(post("/students/newStudent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(studentView)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isNotNull()
                                .hasMessageContaining("Major with this number does not exists!"));
    }
}
