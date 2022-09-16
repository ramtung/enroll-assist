package ir.proprog.enrollassist.controller.student;

import com.sun.istack.NotNull;
import ir.proprog.enrollassist.controller.section.SectionView;
import ir.proprog.enrollassist.domain.entity.Major;
import ir.proprog.enrollassist.domain.entity.Section;
import ir.proprog.enrollassist.domain.entity.Student;
import ir.proprog.enrollassist.domain.valueobject.Name;
import ir.proprog.enrollassist.repository.MajorRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import ir.proprog.enrollassist.repository.StudentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/students")
public class StudentController {
    StudentRepository studentRepository;
    MajorRepository majorRepository;
    SectionRepository sectionRepository;
    ModelMapper modelMapper;

    public StudentController(StudentRepository studentRepository, MajorRepository majorRepository , SectionRepository sectionRepository, ModelMapper modelMapper) {
        this.studentRepository = studentRepository;
        this.majorRepository = majorRepository;
        this.modelMapper = modelMapper;
        this.sectionRepository = sectionRepository;
    }

    @PostMapping("/newStudent")
    public StudentView addStudent(@RequestBody @NotNull StudentView studentView) {
        try {
            Optional<Major> optionalMajor = majorRepository.findByMajorNumber(studentView.getMajor().getMajorNumber());
            if (optionalMajor.isEmpty()) {
                throw new IllegalArgumentException("Major with this number does not exists!");
            }
            Student student = new Student(studentView.getStudentNumber(), new Name(studentView.getStudentName()), optionalMajor.get());
            if (studentRepository.findByStudentNumber(student.getStudentNumber()).isPresent()) {
                throw new IllegalArgumentException("Student with this number already exists!");
            }
            studentRepository.save(student);
            return modelMapper.map(student, StudentView.class);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @GetMapping("/{id}")
    public StudentView one(@PathVariable Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        return modelMapper.map(student, StudentView.class);
    }

    @GetMapping("/{id}/listOfTakeableSections")
    public List<SectionView> getListOfTakAbleSection(@PathVariable @NotNull Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        Iterable<Section> allSections = sectionRepository.findAll();
        List<Section> takeableSectionList = student.sectionsThatCanTake(allSections);
        return takeableSectionList.stream().map(section ->
                        modelMapper.map(section, SectionView.class))
                .collect(Collectors.toList());
    }

}
