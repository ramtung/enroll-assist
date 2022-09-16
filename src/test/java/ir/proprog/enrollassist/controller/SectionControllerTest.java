package ir.proprog.enrollassist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import ir.proprog.enrollassist.builder.*;
import ir.proprog.enrollassist.controller.course.CourseView;
import ir.proprog.enrollassist.controller.schedule.ClassScheduleView;
import ir.proprog.enrollassist.controller.section.*;
import ir.proprog.enrollassist.domain.entity.*;
import ir.proprog.enrollassist.domain.valueobject.*;
import ir.proprog.enrollassist.domain.valueobject.schedule.TimeSchedule;
import ir.proprog.enrollassist.repository.*;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SectionController.class)
public class SectionControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ModelMapper modelMapper;
    @MockBean
    private CourseRepository courseRepository;
    @MockBean
    private SectionRepository sectionRepository;
    @MockBean
    private EnrollmentListRepository enrollmentListRepository;

    SectionController sectionController;

    @PostConstruct
    void setupEnrollmentList() {
        sectionController = new SectionController(sectionRepository, courseRepository, enrollmentListRepository, modelMapper);
    }

    @Test
    public void Given_listOfSections_When_getAllSections_Then_returnAllOfThemCorrectly() throws Exception {
        List<Section> sections = List.of(
                SectionBuilder.aSection().withCourse(new Course("1", new Name("C1"), 3)).withSectionNo("01").build(),
                SectionBuilder.aSection().withCourse(new Course("2", new Name("C2"), 3)).withSectionNo("02").build(),
                SectionBuilder.aSection().withCourse(new Course("2", new Name("C2"), 3)).withSectionNo("01").build()
        );

        given(sectionRepository.findAll()).willReturn(sections);

        // TODO: Since there is no requirement on the order of the returned list, $[0] must be changed to proper filtering
        mvc.perform(get("/sections")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(sections.size())))
                .andExpect(jsonPath("$[0].courseTitle", is("C1")))
                .andExpect(jsonPath("$[0].courseNumber", is("1")))
                .andExpect(jsonPath("$[0].sectionNo", is("01")));
    }

    @Test
    public void Given_sectionNoWithZeroNumber_When_addSection_Then_throwException() throws Exception {
        Course course = CourseBuilder.aCourse().build();
        mvc.perform(post("/sections/section")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new SectionView("0", modelMapper.map(course, CourseView.class)))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isNotNull()
                                .hasMessageContaining("section number can not be zero"));
    }

    @Test
    public void Given_section_When_addSection_Then_returnSectionView() throws Exception {
        Course course = CourseBuilder.aCourse().build();
        mvc.perform(post("/sections/section")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new SectionView("1", modelMapper.map(course, CourseView.class)))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void Given_validScheduleClassData_When_addClassSchedule_Then_addSuccessfully() throws Exception {
        Section section = SectionBuilder.aSection().build();
        given(sectionRepository.findById(12L)).willReturn(Optional.of(section));
        ClassScheduleView classScheduleView = new ClassScheduleView("MONDAY", "08:00:00", "09:00:00");
        mvc.perform(post("/sections/{id}/newClassSchedule", 12L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(classScheduleView)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(section.getClassScheduleList().size())))
                .andExpect(jsonPath("$[0].fromTime", is(classScheduleView.getFromTime())))
                .andExpect(jsonPath("$[0].toTime", is(classScheduleView.getToTime())));
    }

    @Test
    public void Given_formTimeAfterToTimeClassSchedule_When_addClassSchedule_Then_throwException() throws Exception {
        Section section = SectionBuilder.aSection().build();
        given(sectionRepository.findById(12L)).willReturn(Optional.of(section));
        ClassScheduleView classScheduleView = new ClassScheduleView("MONDAY", "10:00:00", "09:00:00");
        mvc.perform(post("/sections/{id}/newClassSchedule", 12L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(classScheduleView)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isNotNull()
                                .hasMessageContaining("Time Schedule is not valid"));
    }

    @Test
    public void Given_classScheduleOnFriday_When_addClassSchedule_Then_throwException() throws Exception {
        Section section = SectionBuilder.aSection().build();
        given(sectionRepository.findById(12L)).willReturn(Optional.of(section));
        ClassScheduleView classScheduleView = new ClassScheduleView("FRIDAY", "08:00:00", "09:00:00");
        mvc.perform(post("/sections/{id}/newClassSchedule", 12L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(classScheduleView)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isNotNull()
                                .hasMessageContaining("Friday is the Holiday!"));
    }

    @Test
    public void Given_classScheduleOverlapWithPrevious_When_addClassSchedule_Then_throwException() throws Exception {
        Section section = SectionBuilder.aSection().build();
        section.addClassSchedule(new ClassSchedule(WeekDayEnum.SATURDAY, new TimeSchedule(Time.valueOf("08:00:00"), Time.valueOf("10:00:00"))));
        given(sectionRepository.findById(12L)).willReturn(Optional.of(section));
        ClassScheduleView classScheduleView = new ClassScheduleView("SATURDAY", "08:00:00", "09:00:00");
        mvc.perform(post("/sections/{id}/newClassSchedule", 12L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(classScheduleView)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isNotNull()
                                .hasMessageContaining("requested classSchedule conflicts with others"));
    }

    @Test
    public void Given_invalidSectionId_When_removeSection_Then_throwException() throws Exception {
        when(sectionRepository.findBySectionNo(any())).thenReturn(Optional.empty());
        doNothing().when(sectionRepository).delete(any());
        mvc.perform(delete("/sections/{sectionNumber}", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isInstanceOf(ResponseStatusException.class)
                                .hasMessageContaining("Section not found"));
    }

    @Test
    public void Given_validSectionId_When_removeSection_Then_donotThrowException() throws Exception {
        Section section = SectionBuilder.aSection().build();
        when(sectionRepository.findBySectionNo(section.getSectionNo())).thenReturn(Optional.of(section));
        doNothing().when(sectionRepository).delete(section);
        mvc.perform(delete("/sections/{sectionNumber}", section.getSectionNo())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void Given_xmlFileSections_When_addingSections_Then_convertXmlToSectionCorrectly() throws Exception {
        List<Section> sections = SectionBuilder.aSection().createSectionList();
        sections.get(0).addClassSchedule(new ClassScheduleBuilder().withWeekDay(WeekDayEnum.SATURDAY).build());
        sections.get(0).addClassSchedule(new ClassScheduleBuilder().withWeekDay(WeekDayEnum.SUNDAY).build());
        sections.get(1).addClassSchedule(new ClassScheduleBuilder().withWeekDay(WeekDayEnum.MONDAY).build());
        XStream xstream = new XStream();
        xstream.processAnnotations(SectionXmlDTO.class);
        xstream.processAnnotations(SessionTimeXmlDTO.class);
        xstream.processAnnotations(SectionListXmlDTO.class);

        String xml = xstream.toXML(new SectionListXmlDTO(sections));
        List<Course> courses = sections.stream().map(Section::getCourse).collect(Collectors.toList());
        for (Course o : courses) {
            List<Course> result = new ArrayList<>(1);
            result.add(o);
            when(courseRepository.findByCourseNumber(o.getCourseNumber())).thenReturn(result);
        }
        mvc.perform(post("/sections/insertSectionList")
                .contentType(MediaType.APPLICATION_XML)
                .content(xml))
                .andExpect(status().isOk());
        verify(sectionRepository).saveAll(sections);
    }

    @Test
    public void Given_invalidXmlFileSections_When_addingSections_Then_throwsException() throws Exception {
        List<Section> sections = SectionBuilder.aSection().createSectionList();
        sections.get(0).addClassSchedule(new ClassScheduleBuilder().withWeekDay(WeekDayEnum.SATURDAY).build());
        sections.get(0).addClassSchedule(new ClassScheduleBuilder().withWeekDay(WeekDayEnum.SUNDAY).build());
        sections.get(1).addClassSchedule(new ClassScheduleBuilder().withWeekDay(WeekDayEnum.MONDAY).build());
        XStream xstream = new XStream();
        xstream.processAnnotations(SectionXmlDTO.class);
        xstream.processAnnotations(SessionTimeXmlDTO.class);
        xstream.processAnnotations(SectionListXmlDTO.class);

        String xml = xstream.toXML(new SectionListXmlDTO(sections));
        List<Course> courses = new ArrayList<>(1);
        courses.add(null);
        when(courseRepository.findByCourseNumber(any())).thenReturn(courses);
        mvc.perform(post("/sections/insertSectionList")
                .contentType(MediaType.APPLICATION_XML)
                .content(xml))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isInstanceOf(ResponseStatusException.class)
                                .hasMessageContaining("course number is not valid"));
    }
}
