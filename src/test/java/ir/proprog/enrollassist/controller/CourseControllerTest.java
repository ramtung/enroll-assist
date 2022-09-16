package ir.proprog.enrollassist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.proprog.enrollassist.builder.CourseBuilder;
import ir.proprog.enrollassist.controller.course.CourseController;
import ir.proprog.enrollassist.controller.course.CourseView;
import ir.proprog.enrollassist.domain.entity.Course;
import ir.proprog.enrollassist.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
public class CourseControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    ModelMapper modelMapper;
    @MockBean
    private CourseRepository courseRepository;

    @Test
    public void Given_courseViewObject_When_addCourse_Then_returnCourseView() throws Exception {
        Course economy = CourseBuilder.aCourse().build();
        List<Course> courses = new ArrayList<>();
        courses.add(economy);

        CourseView courseView = new CourseView("123", "MyCourse", 1);

        ObjectMapper mapper = new ObjectMapper();
        String courseJson = mapper.writeValueAsString(courseView);

        when(courseRepository.findByCourseNumber("123")).thenReturn(new ArrayList<>());
        when(courseRepository.findByCourseNumber(economy.getCourseNumber())).thenReturn(courses);
        doReturn(null).when(courseRepository).save(any());
        mvc.perform(post("/courses/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(courseJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseNumber", is("123")))
                .andExpect(jsonPath("$.credits", is(1)));
    }

    @Test
    public void Given_nullBody_When_addCourse_Then_returnHttpMessageNotReadableException() throws Exception {
        MvcResult result = mvc.perform(post("/courses/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        Optional<Exception> exception = Optional.ofNullable(result.getResolvedException());
        exception.ifPresent((e) -> assertTrue(e instanceof HttpMessageNotReadableException));
        exception.ifPresent((e) -> assertTrue(e.getMessage().contains("Required request body")));
    }

    @Test
    public void Given_courseViewObjectWithEmptyCourseNumberAndEmptyTitleAndNegativeCreditsAndInvalidPre_When_addCourse_Then_returnException() throws Exception {
        Course economy = CourseBuilder.aCourse().build();
        CourseView courseView = new CourseView("", "", -1);

        ObjectMapper mapper = new ObjectMapper();
        String courseJson = mapper.writeValueAsString(courseView);

        when(courseRepository.findByCourseNumber(any())).thenReturn(new ArrayList<>());
        doReturn(null).when(courseRepository).save(any());
        mvc.perform(post("/courses/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(courseJson))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isInstanceOf(ResponseStatusException.class)
                                .hasMessageContaining("Course number cannot be empty")
                                .hasMessageContaining("Course must have a name")
                                .hasMessageContaining("Course credit units cannot be negative or zero"));

    }

    @Test
    public void Given_courseViewObjectWithDuplicateCourseNumberAndOutOfRangeCourseNumber_When_addCourse_Then_returnException() throws Exception {
        Course economy = CourseBuilder.aCourse().withCourseNumber("1").build();
        List<Course> courses = new ArrayList<>();
        courses.add(economy);

        CourseView courseView = new CourseView("123", "MyCourse", 7);

        ObjectMapper mapper = new ObjectMapper();
        String courseJson = mapper.writeValueAsString(courseView);

        when(courseRepository.findByCourseNumber("123")).thenReturn(courses);
        when(courseRepository.findByCourseNumber("1")).thenReturn(new ArrayList<>());
        doReturn(null).when(courseRepository).save(any());
        mvc.perform(post("/courses/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(courseJson))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isInstanceOf(ResponseStatusException.class)
                                .hasMessageContaining("credits must be between 1-5"));
    }
}
