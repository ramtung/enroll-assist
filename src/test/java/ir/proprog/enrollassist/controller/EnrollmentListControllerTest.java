package ir.proprog.enrollassist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.proprog.enrollassist.builder.EnrollmentListBuilder;
import ir.proprog.enrollassist.builder.SectionBuilder;
import ir.proprog.enrollassist.builder.StudentBuilder;
import ir.proprog.enrollassist.builder.CourseBuilder;
import ir.proprog.enrollassist.controller.enrollmentList.EnrollmentListController;
import ir.proprog.enrollassist.controller.enrollmentList.EnrollmentListView;
import ir.proprog.enrollassist.domain.entity.Course;
import ir.proprog.enrollassist.domain.entity.EnrollmentList;
import ir.proprog.enrollassist.domain.entity.Section;
import ir.proprog.enrollassist.domain.entity.Student;
import ir.proprog.enrollassist.domain.service.EnrollmentListService;
import ir.proprog.enrollassist.domain.valueobject.Name;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import ir.proprog.enrollassist.repository.StudentRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static ir.proprog.enrollassist.builder.EnrollmentListBuilder.createListOfEnrollmentListForAStudent;
import static ir.proprog.enrollassist.builder.StudentBuilder.createSomeStudent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnrollmentListController.class)
public class EnrollmentListControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private EnrollmentListRepository enrollmentListRepository;
    @MockBean
    private SectionRepository sectionRepository;
    @MockBean
    private StudentRepository studentRepository;
    @MockBean
    private EnrollmentListService enrollmentListService;

    @Test
    public void Given_enrollmentListAndSection_When_callingAddSectionUrl_Then_addSectionToEnrollmentList() throws Exception {
        Student bebe = StudentBuilder.aStudent().build();
        Course phys1 = CourseBuilder.aCourse().withCourseNumber("2").withTitle("PHYS1").build();
        Section section = SectionBuilder.aSection().withCourse(phys1).withSectionNo("2").build();
        EnrollmentList enrollmentList = EnrollmentListBuilder.anEnrollmentList().withOwner(bebe).build();
        enrollmentList.addSections(SectionBuilder.aSection().withSectionNo("1").build());

        given(enrollmentListRepository.findById(123L)).willReturn(java.util.Optional.of(enrollmentList));
        given(sectionRepository.findById(12L)).willReturn(java.util.Optional.of(section));

        mvc.perform(put("/lists/{listId}/sections/{sectionId}", 123L, 12L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(enrollmentList.getSections().size())))
                .andExpect(jsonPath("$[1].courseTitle", is("PHYS1")))
                .andExpect(jsonPath("$[1].sectionNo", is("2")));
    }

    @Test
    public void Given_enrollmentListIdAndSectionId_When_removeSection_Then_returnListWithoutThatSection() throws Exception {
        Student bebe = StudentBuilder.aStudent().build();
        Section math1_1 = SectionBuilder.aSection().withCourse(new Course("4", new Name("MATH1"), 3)).withSectionNo("01").build();
        Section phys1_1 = SectionBuilder.aSection().withCourse(new Course("8", new Name("PHYS1"), 3)).withSectionNo("02").build();
        EnrollmentList bebeList = EnrollmentListBuilder.anEnrollmentList().withOwner(bebe).build();
        bebeList.addSections(math1_1, phys1_1);

        given(enrollmentListRepository.findById(123L)).willReturn(java.util.Optional.of(bebeList));
        given(sectionRepository.findById(2L)).willReturn(java.util.Optional.of(phys1_1));

        mvc.perform(delete("/lists/{listId}/sections/{sectionId}", 123, 2L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(bebeList.getSections().size())))
                .andExpect(jsonPath("$[0].courseTitle", is("MATH1")))
                .andExpect(jsonPath("$[0].sectionNo", is("01")));
    }

    @Test
    public void Given_invalidEnrollmentListIdAndSectionId_When_removeSection_Then_throwException() throws Exception {
        when(enrollmentListRepository.findById(123L)).thenReturn(Optional.empty());
        mvc.perform(delete("/lists/{listId}/sections/{sectionId}", 123, 2L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isInstanceOf(ResponseStatusException.class)
                                .hasMessageContaining("Enrollment List not found"));
    }

    @Test
    public void Given_enrollmentListIdAndInvalidSectionId_When_removeSection_Then_throwException() throws Exception {
        when(enrollmentListRepository.findById(123L)).thenReturn(Optional.of(mock(EnrollmentList.class)));
        given(sectionRepository.findById(2L)).willReturn(Optional.empty());
        mvc.perform(delete("/lists/{listId}/sections/{sectionId}", 123, 2L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isInstanceOf(ResponseStatusException.class)
                                .hasMessageContaining("Section not found"));
    }

    @Test
    public void Given_enrollmentListWithValidData_When_addEnrollmentList_Then_addSuccessfully() throws Exception {
        String enrollmentListName = "abc";
        Student student = StudentBuilder.aStudent().build();

        given(studentRepository.findByStudentNumber(student.getStudentNumber())).willReturn(Optional.of(student));
        given(enrollmentListRepository.findByStudentNumber(student.getStudentNumber())).willReturn(Optional.empty());

        mvc.perform(post("/lists/newList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new EnrollmentListView(enrollmentListName, student.getStudentNumber()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentListName", Matchers.is(enrollmentListName)))
                .andExpect(jsonPath("$.ownerNumber", Matchers.is(student.getStudentNumber())));
    }

    @Test
    public void Given_enrollmentListWithValidData_When_addEnrollmentList_Then_persistSuccessfully() throws Exception {
        String enrollmentListName = "abc";
        Student student = StudentBuilder.aStudent().build();

        given(studentRepository.findByStudentNumber(student.getStudentNumber())).willReturn(Optional.of(student));
        given(enrollmentListRepository.findByStudentNumber(student.getStudentNumber())).willReturn(Optional.empty());
        EnrollmentList enrollmentList = EnrollmentListBuilder.anEnrollmentList().withListName(enrollmentListName).withOwner(student).build();
        mvc.perform(post("/lists/newList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new EnrollmentListView(enrollmentListName, student.getStudentNumber()))))
                .andExpect(status().isOk());
        verify(enrollmentListRepository).save(enrollmentList);
    }

    @Test
    public void Given_enrollmentListWithEmptyName_When_addEnrollmentList_Then_throwException() throws Exception {
        String enrollmentListName = "";
        Student student = StudentBuilder.aStudent().build();

        given(studentRepository.findByStudentNumber(student.getStudentNumber())).willReturn(Optional.of(student));
        given(enrollmentListRepository.findByStudentNumber(student.getStudentNumber())).willReturn(Optional.empty());

        mvc.perform(post("/lists/newList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new EnrollmentListView(enrollmentListName, student.getStudentNumber()))))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isNotNull()
                                .hasMessageContaining("Enrollment list must have a name"));
    }

    @Test
    public void Given_enrollmentListWithNullName_When_addEnrollmentList_Then_throwException() throws Exception {
        Student student = StudentBuilder.aStudent().build();

        given(studentRepository.findByStudentNumber(student.getStudentNumber())).willReturn(Optional.of(student));
        given(enrollmentListRepository.findByStudentNumber(student.getStudentNumber())).willReturn(Optional.empty());

        mvc.perform(post("/lists/newList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new EnrollmentListView(null, student.getStudentNumber()))))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isNotNull()
                                .hasMessageContaining("listName is marked non-null but is null"));
    }

    @Test
    public void Given_enrollmentListForInvalidStudent_When_addEnrollmentList_Then_throwException() throws Exception {
        String studentNumber = "123";
        String enrollmentListName = "abc";

        given(studentRepository.findByStudentNumber(studentNumber)).willReturn(Optional.empty());

        mvc.perform(post("/lists/newList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new EnrollmentListView(enrollmentListName, studentNumber))))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isNotNull()
                                .hasMessageContaining("Student with this number does not exists!"));
    }

    @Test
    void Given_studentNumber_When_gettingWeeklySchedule_Then_receiveStringWeeklySchedule() throws Exception {
        Student student = createSomeStudent();
        given(studentRepository.findByStudentNumber(student.getStudentNumber())).willReturn(Optional.of(student));
        given(enrollmentListService.getWeeklySchedule(student)).willReturn(new StringBuilder("test"));
        mvc.perform(get("/lists/{studentNumber}/weeklySchedule", student.getStudentNumber())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("test")));
    }

    @Test
    void Given_invalidStudentNumber_When_gettingWeeklySchedule_Then_receiveStringWeeklySchedule() throws Exception {
        given(studentRepository.findByStudentNumber(any())).willReturn(Optional.empty());
        mvc.perform(get("/lists/{studentNumber}/weeklySchedule", "123")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isInstanceOf(ResponseStatusException.class)
                                .hasMessageContaining("Student not found"));
    }

    @Test
    void Given_invalidEnrollmentList_When_finalizeEnrollmentList_Then_throwsException() throws Exception {
        given(enrollmentListRepository.findById(any())).willReturn(Optional.empty());
        mvc.perform(get("/lists/finalEnrollmentList/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isInstanceOf(ResponseStatusException.class)
                                .hasMessageContaining("Enrollment List not found"));
    }

    @Test
    void Given_studentHasFinalEnrollmentList_When_finalizeEnrollmentList_Then_throwsException() throws Exception {
        Student student = createSomeStudent();
        List<EnrollmentList> enrollmentLists = createListOfEnrollmentListForAStudent(student);
        enrollmentLists.get(1).setListAsFinal();
        given(enrollmentListRepository.findById(any())).willReturn(Optional.ofNullable(enrollmentLists.get(0)));
        given(enrollmentListRepository.findByOwner(student)).willReturn(enrollmentLists);
        mvc.perform(get("/lists/finalEnrollmentList/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isInstanceOf(ResponseStatusException.class)
                                .hasMessageContaining("This student already has final EnrollmentList"));
    }

    @Test
    void Given_validEnrollmentList_When_finalizeEnrollmentList_Then_updateEnrollmentList() throws Exception {
        Student student = createSomeStudent();
        List<EnrollmentList> enrollmentLists = createListOfEnrollmentListForAStudent(student);
        given(enrollmentListRepository.findById(any())).willReturn(Optional.ofNullable(enrollmentLists.get(0)));
        given(enrollmentListRepository.findByOwner(student)).willReturn(enrollmentLists);
        mvc.perform(get("/lists/finalEnrollmentList/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }
}