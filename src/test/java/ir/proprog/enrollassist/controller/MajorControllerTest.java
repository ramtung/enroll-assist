package ir.proprog.enrollassist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.proprog.enrollassist.builder.CourseBuilder;
import ir.proprog.enrollassist.builder.MajorViewBuilder;
import ir.proprog.enrollassist.controller.major.MajorController;
import ir.proprog.enrollassist.controller.major.MajorView;
import ir.proprog.enrollassist.domain.entity.Course;
import ir.proprog.enrollassist.domain.entity.Major;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.MajorRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static ir.proprog.enrollassist.builder.MajorBuilder.aMajor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MajorController.class)
public class MajorControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    ModelMapper modelMapper;
    @MockBean
    private MajorRepository majorRepository;
    @MockBean
    private CourseRepository courseRepository;

    @Test
    public void Given_validNumberAndTitle_When_addMajor_Then_addSuccessfully() throws Exception {
        MajorView majorView = new MajorViewBuilder().build();
        when(majorRepository.findByMajorNumber(majorView.getMajorNumber())).thenReturn(Optional.empty());
        mvc.perform(post("/majors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(majorView)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.majorNumber", Matchers.is(majorView.getMajorNumber())))
                .andExpect(jsonPath("$.title", Matchers.is(majorView.getTitle())));
    }

    @Test
    public void Given_majorWithNumericName_When_addMajor_Then_throwException() throws Exception {
        MajorView majorView = new MajorViewBuilder().withTitle("321").build();
        mvc.perform(post("/majors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(majorView)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isNotNull()
                                .hasMessageContaining("Major title must contain at least one character"));
    }

    @Test
    public void Given_majorWithAlphabeticNumber_When_addMajor_Then_throwException() throws Exception {
        MajorView majorView = new MajorViewBuilder().withMajorNumber("cba").build();
        mvc.perform(post("/majors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(majorView)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isNotNull()
                                .hasMessageContaining("Major number must contain only numbers"));
    }

    @Test
    public void Given_majorWithZeroNumber_When_addMajor_Then_throwException() throws Exception {
        MajorView majorView = new MajorViewBuilder().withMajorNumber("000000").build();
        mvc.perform(post("/majors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(majorView)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isNotNull()
                                .hasMessageContaining("Major number can not be zero"));
    }

    @Test
    public void Given_duplicateMajorNumber_When_addMajor_Then_throwException() throws Exception {
        MajorView majorView = new MajorViewBuilder().build();
        given(majorRepository.findByMajorNumber(majorView.getMajorNumber())).willReturn(Optional.of(modelMapper.map(majorView, Major.class)));
        mvc.perform(post("/majors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(majorView)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isNotNull()
                                .hasMessageContaining("Major with this number already exists!"));
    }

    @Test
    public void Given_validCourseIdAndMajorId_When_addCourseToMajor_Then_addSuccessfully() throws Exception {
        Major major = aMajor();
        Course course = CourseBuilder.aCourse().build();

        when(majorRepository.findById(1L)).thenReturn(Optional.of(major));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
        when(courseRepository.save(course)).thenReturn(course);
        mvc.perform(put("/majors/{majorId}/courses/{courseId}", 1L, 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chart", hasSize(1)))
                .andExpect(jsonPath("$.chart[0].courseView.courseNumber", is(course.getCourseNumber())));
    }

    @Test
    public void Given_invalidMajorId_When_addCourseToMajor_Then_throwException() throws Exception {
        when(majorRepository.findById(1L)).thenReturn(Optional.empty());
        mvc.perform(put("/majors/{majorId}/courses/{courseId}", 1L, 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isInstanceOf(ResponseStatusException.class)
                                .hasMessageContaining("Major not found"));
    }

    @Test
    public void Given_invalidCourseId_When_addCourseToMajor_Then_throwException() throws Exception {
        Major major = aMajor();
        when(majorRepository.findById(1L)).thenReturn(Optional.of(major));
        when(courseRepository.findById(2L)).thenReturn(Optional.empty());
        mvc.perform(put("/majors/{majorId}/courses/{courseId}", 1L, 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isInstanceOf(ResponseStatusException.class)
                                .hasMessageContaining("Course not found"));
    }
}
