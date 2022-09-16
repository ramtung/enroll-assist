package ir.proprog.enrollassist.controller.section;

import com.sun.istack.NotNull;
import com.thoughtworks.xstream.XStream;
import ir.proprog.enrollassist.controller.schedule.ClassScheduleView;
import ir.proprog.enrollassist.domain.entity.*;
import ir.proprog.enrollassist.domain.valueobject.*;
import ir.proprog.enrollassist.domain.valueobject.schedule.ExamSchedule;
import ir.proprog.enrollassist.domain.valueobject.schedule.TimeSchedule;
import ir.proprog.enrollassist.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@RestController
@RequestMapping("/sections")
public class SectionController {
    SectionRepository sectionRepository;
    CourseRepository courseRepository;
    EnrollmentListRepository enrollmentListRepository;
    ModelMapper modelMapper;

    public SectionController(SectionRepository sectionRepository, CourseRepository courseRepository,
                             EnrollmentListRepository enrollmentListRepository,
                             ModelMapper modelMapper) {
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
        this.enrollmentListRepository = enrollmentListRepository;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public Iterable<SectionView> all() {
        return StreamSupport.stream(sectionRepository.findAll().spliterator(), false).map(SectionView::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public SectionView one(@PathVariable Long id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found"));
        return new SectionView(section);
    }

    @GetMapping("/demands")
    public Iterable<SectionDemandView> allDemands() {
        List<SectionDemandView> demands = enrollmentListRepository.findDemandForAllSections();
        for (SectionDemandView demand : demands) {
            demand.setSectionView(sectionRepository.findById(demand.getSectionId()).orElseThrow());
        }
        return demands;
    }

    @PostMapping
    public SectionView addSection(@RequestBody @NotNull SectionView sectionView) {
        try {
            Course course = new Course(sectionView.getCourseView().getCourseNumber(), new Name(sectionView.getCourseView().getTitle()),
                    sectionView.getCourseView().getCredits());
            Section section = new Section(course, sectionView.getSectionNo(), sectionView.getCapacity());
            if (sectionView.getExamScheduleView() != null)
                sectionView.setExamSchedule(sectionView.getExamScheduleView());
            sectionRepository.save(section);
            return modelMapper.map(section, SectionView.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{id}/classSchedule")
    public Iterable<ClassScheduleView> addClassSchedule(@PathVariable @NotNull Long id, @RequestBody @NotNull ClassScheduleView classScheduleView) {
        try {
            Section section = sectionRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found"));
            TimeSchedule timeSchedule = new TimeSchedule(Time.valueOf(classScheduleView.getFromTime()), Time.valueOf(classScheduleView.getToTime()));
            ClassSchedule classSchedule = new ClassSchedule(WeekDayEnum.valueOf(classScheduleView.getWeekDay()), timeSchedule);
            section.addClassSchedule(classSchedule);
            sectionRepository.save(section);
            return section.getClassScheduleList().stream().map(ClassScheduleView::new).collect(Collectors.toList());
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @DeleteMapping("/{sectionNumber}")
    public String removeSection(@NotNull @PathVariable String sectionNumber) {
        Section section = sectionRepository.findBySectionNo(sectionNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found"));
        sectionRepository.delete(section);
        return "redirect:/";
    }

    @PostMapping(value = "/insertSectionList", consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_XML_VALUE})
    public String putDataIntoDatabase(@RequestBody String sectionList) {
        XStream xstream = new XStream();
        xstream.allowTypes(new Class[]{SectionXmlDTO.class, SessionTimeXmlDTO.class, SectionListXmlDTO.class});
        xstream.processAnnotations(SectionXmlDTO.class);
        xstream.processAnnotations(SessionTimeXmlDTO.class);
        xstream.processAnnotations(SectionListXmlDTO.class);
        SectionListXmlDTO sectionListXmlDTO = (SectionListXmlDTO) xstream.fromXML(sectionList);
        List<Section> sections;
        try {
            sections = convertSectionListXmlDTOToSections(sectionListXmlDTO);
        } catch (ParseException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        sectionRepository.saveAll(sections);
        return "redirect:/";
    }

    private List<Section> convertSectionListXmlDTOToSections(SectionListXmlDTO sectionListXmlDTO) throws ParseException {
        List<Section> sectionList = new ArrayList<>();
        for (SectionXmlDTO sectionXmlDTO : sectionListXmlDTO.getSections()) {
            Course course = courseRepository.findByCourseNumber(sectionXmlDTO.getCourseNumber()).get(0);
            if (course == null)
                throw new IllegalArgumentException("course number is not valid. courseNumber = " + sectionXmlDTO.getCourseNumber());
            Section section = new Section(course, sectionXmlDTO.getSectionNo(), sectionXmlDTO.getCapacity());
            sectionXmlDTO.getClassSchedules().forEach(o -> section.addClassSchedule(new ClassSchedule(o.getWeekDay(), new TimeSchedule(o.getStartTime(), o.getEndTime()))));
            DateFormat sdf = new SimpleDateFormat("hh:mm:ss");
            Time fromTime = new Time(sdf.parse(sectionXmlDTO.getExamStart().split("T")[1]).getTime());
            Time toTime = new Time(sdf.parse(sectionXmlDTO.getExamEnd().split("T")[1]).getTime());
            Date date = Date.valueOf(sectionXmlDTO.getExamStart().split("T")[0]);
            section.setExamSchedule(new ExamSchedule(date, new TimeSchedule(fromTime, toTime)));
            sectionList.add(section);
        }
        return sectionList;
    }
}

