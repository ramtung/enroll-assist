package ir.proprog.enrollassist.controller.course;

import com.sun.istack.NotNull;
import ir.proprog.enrollassist.domain.entity.Course;
import ir.proprog.enrollassist.domain.valueobject.Name;
import ir.proprog.enrollassist.repository.CourseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/courses")
public class CourseController {
    CourseRepository courseRepository;
    ModelMapper modelMapper;

    public CourseController(CourseRepository courseRepository, ModelMapper modelMapper) {
        this.courseRepository = courseRepository;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/")
    public CourseView addCourse(@RequestBody @NotNull CourseView courseView) {
        try {
            validateBeforeSave(courseView);
            Course course = new Course(courseView.getCourseNumber(), new Name(courseView.getTitle()), courseView.getCredits());
            courseRepository.save(course);
            return modelMapper.map(course, CourseView.class);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private void validateBeforeSave(CourseView courseView) throws IllegalArgumentException {
        StringBuilder message = new StringBuilder();
        if (courseView.getCourseNumber().equals(""))
            message.append("Course number cannot be empty").append("\n");
        if (courseView.getTitle().equals(""))
            message.append("Course must have a name").append("\n");
        if (courseView.getCredits() <= 0)
            message.append("Course credit units cannot be negative or zero ").append("\n");
        if (courseView.getCredits() > 5)
            message.append("credits must be between 1-5").append("\n");
        if (courseRepository.findByCourseNumber(courseView.getCourseNumber()).size() > 0)
            message.append("course number must be unique").append("\n");

        if (message.length() > 0)
            throw new IllegalArgumentException(message.toString());
    }

    @GetMapping("/{id}")
    public CourseView one(@PathVariable Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        return modelMapper.map(course, CourseView.class);
    }
}
