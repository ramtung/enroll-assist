package ir.proprog.enrollassist.controller.enrollmentList;

import com.sun.istack.NotNull;
import ir.proprog.enrollassist.controller.section.SectionEnrollmentResultView;
import ir.proprog.enrollassist.controller.section.SectionView;
import ir.proprog.enrollassist.domain.entity.EnrollmentList;
import ir.proprog.enrollassist.domain.entity.Section;
import ir.proprog.enrollassist.domain.entity.Student;
import ir.proprog.enrollassist.domain.service.EnrollmentListService;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import ir.proprog.enrollassist.repository.StudentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/lists")
public class EnrollmentListController {
    private EnrollmentListRepository enrollmentListRepository;
    private SectionRepository sectionRepository;
    private StudentRepository studentRepository;
    private EnrollmentListService enrollmentListService;
    private ModelMapper modelMapper;

    public EnrollmentListController(EnrollmentListRepository enrollmentListRepository, SectionRepository sectionRepository,
                                    StudentRepository studentRepository, EnrollmentListService enrollmentListService, ModelMapper modelMapper) {
        this.enrollmentListRepository = enrollmentListRepository;
        this.sectionRepository = sectionRepository;
        this.studentRepository = studentRepository;
        this.enrollmentListService = enrollmentListService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public Iterable<EnrollmentListView> all() {
        return StreamSupport.stream(enrollmentListRepository.findAll().spliterator(), false)
                .map(enrollmentList -> modelMapper.map(enrollmentList, EnrollmentListView.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EnrollmentListView one(@PathVariable Long id) {
        EnrollmentList enrollmentList = enrollmentListRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment List not found"));
        return modelMapper.map(enrollmentList, EnrollmentListView.class);
    }

    @GetMapping("/{id}/sections")
    public Iterable<SectionView> getListSections(@PathVariable Long id) {
        EnrollmentList enrollmentList = enrollmentListRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment List not found"));
        return enrollmentList.getSections().stream().map(SectionView::new).collect(Collectors.toList());
    }

    @PutMapping("/{listId}/sections/{sectionId}")
    public Iterable<SectionView> addSection(@PathVariable Long listId, @PathVariable Long sectionId) {
        EnrollmentList enrollmentList = enrollmentListRepository.findById(listId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment List not found"));
        Section section = sectionRepository.findById(sectionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found"));
        enrollmentList.addSection(section);
        enrollmentListRepository.save(enrollmentList);
        return enrollmentList.getSections().stream().map(SectionView::new).collect(Collectors.toList());
    }

    @GetMapping("/{listId}/check")
    public EnrollmentCheckResultView checkRegulations(@PathVariable Long listId) {
        EnrollmentList enrollmentList = enrollmentListRepository.findById(listId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment List not found"));

        return new EnrollmentCheckResultView(enrollmentList.checkEnrollmentRules());
    }

    @DeleteMapping("/{listId}/sections/{sectionId}")
    public Iterable<SectionView> removeSection(@PathVariable Long listId, @PathVariable Long sectionId) {
        EnrollmentList enrollmentList = enrollmentListRepository.findById(listId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment List not found"));
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found"));
        enrollmentList.getSections().removeIf(sc -> sc.getSectionNo().equals(section.getSectionNo()));
        enrollmentListRepository.save(enrollmentList);
        return enrollmentList.getSections().stream().map(SectionView::new).collect(Collectors.toList());
    }

    @PostMapping("/newList")
    public EnrollmentListView addEnrollmentList(@RequestBody @NotNull EnrollmentListView enrollmentListView) {
        try {
            Optional<Student> studentOptional = studentRepository.findByStudentNumber(enrollmentListView.getOwnerNumber());
            if (studentOptional.isEmpty()) {
                throw new IllegalArgumentException("Student with this number does not exists!");
            }
            EnrollmentList enrollmentList = new EnrollmentList(enrollmentListView.getEnrollmentListName(), studentOptional.get());
            enrollmentListRepository.save(enrollmentList);
            return modelMapper.map(enrollmentList, EnrollmentListView.class);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @GetMapping(value = "/{studentNumber}/weeklySchedule", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getWeeklySchedule(@PathVariable @NotNull String studentNumber) {
        Student owner = studentRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        return enrollmentListService.getWeeklySchedule(owner).toString();
    }

    @GetMapping(value = "/weeklySchedules", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getWeeklySchedule() {
        List<Student> students = (List<Student>) studentRepository.findAll();
        StringBuilder result = new StringBuilder();
        students.stream().map(student -> enrollmentListService.getWeeklySchedule(student))
                .forEach(schedule -> result.append(schedule).append(System.lineSeparator()));
        return result.toString();
    }

    @GetMapping("/finalEnrollmentList/{id}")
    public String finalizeEnrollmentList(@PathVariable Long id) {
        EnrollmentList enrollmentList = enrollmentListRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment List not found"));
        List<EnrollmentList> enrollmentLists = enrollmentListRepository.findByOwner(enrollmentList.getOwner());
        if (enrollmentLists.stream().anyMatch(list -> Boolean.TRUE.equals(list.isFinalList())))
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "This student already has final EnrollmentList");
        enrollmentList.setListAsFinal();
        enrollmentListRepository.save(enrollmentList);
        return "redirect:/";
    }

    @GetMapping("/enrollment")
    public List<SectionEnrollmentResultView> enrollStudents() {
        Set<Section> results = enrollmentListService.enrollStudents();
        List<SectionEnrollmentResultView> resultViews = new ArrayList<>();
        results.forEach(section -> resultViews.add(new SectionEnrollmentResultView(section.getSectionNo(),
                section.getStudents().stream().map(Student::getStudentNumber).collect(Collectors.toList()))));
        return resultViews;
    }
}
