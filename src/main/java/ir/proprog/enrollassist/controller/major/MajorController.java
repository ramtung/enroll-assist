package ir.proprog.enrollassist.controller.major;

import com.sun.istack.NotNull;
import ir.proprog.enrollassist.domain.entity.Course;
import ir.proprog.enrollassist.domain.entity.LevelEnum;
import ir.proprog.enrollassist.domain.entity.Major;
import ir.proprog.enrollassist.domain.entity.PrerequisiteRelation;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.MajorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Optional;

@RestController
@RequestMapping("/majors")
public class MajorController {
    ModelMapper modelMapper;
    MajorRepository majorRepository;
    CourseRepository courseRepository;

    public MajorController(ModelMapper modelMapper, MajorRepository majorRepository, CourseRepository courseRepository) {
        this.modelMapper = modelMapper;
        this.majorRepository = majorRepository;
        this.courseRepository = courseRepository;
    }

    @PostMapping("/newMajor")
    public MajorView addMajor(@RequestBody @NotNull MajorView majorView) {
        try {
            Major major = new Major(majorView.getMajorNumber(), majorView.getTitle(), LevelEnum.valueOf(majorView.getLevelEnum()));
            if (majorRepository.findByMajorNumber(majorView.getMajorNumber()).isPresent()) {
                throw new IllegalArgumentException("Major with this number already exists!");
            }
            majorRepository.save(major);
            return majorView;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{majorId}/courses/{courseId}")
    public MajorView addCourse(@PathVariable Long majorId, @PathVariable Long courseId) {
        try {
            Optional<Major> optionalMajor = majorRepository.findById(majorId);
            if (optionalMajor.isEmpty()) {
                throw new IllegalArgumentException("Major not found");
            }
            Optional<Course> optionalCourse = courseRepository.findById(courseId);
            if (optionalCourse.isEmpty()) {
                throw new IllegalArgumentException("Course not found");
            }
            optionalMajor.get().addToChart(new PrerequisiteRelation(optionalMajor.get(), optionalCourse.get(), new HashSet<>()));
            majorRepository.save(optionalMajor.get());
            return new MajorView(optionalMajor.get());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
