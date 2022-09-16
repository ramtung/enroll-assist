package ir.proprog.enrollassist.domain.service;

import ir.proprog.enrollassist.domain.entity.ClassSchedule;
import ir.proprog.enrollassist.domain.entity.EnrollmentList;
import ir.proprog.enrollassist.domain.entity.Section;
import ir.proprog.enrollassist.domain.entity.Student;
import ir.proprog.enrollassist.domain.valueobject.WeekDayEnum;
import ir.proprog.enrollassist.domain.valueobject.schedule.DailySchedule;
import ir.proprog.enrollassist.domain.valueobject.schedule.ExamSchedule;
import ir.proprog.enrollassist.domain.valueobject.schedule.SectionSchedule;
import ir.proprog.enrollassist.domain.valueobject.schedule.WeeklySchedule;
import ir.proprog.enrollassist.domain.violation.*;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EnrollmentListService {
    private SectionRepository sectionRepository;
    private EnrollmentListRepository enrollmentListRepository;

    public EnrollmentListService(SectionRepository sectionRepository, EnrollmentListRepository enrollmentListRepository) {
        this.sectionRepository = sectionRepository;
        this.enrollmentListRepository = enrollmentListRepository;
    }

    public StringBuilder getWeeklySchedule(String studentNumber) {
        List<EnrollmentList> enrollmentLists = enrollmentListRepository.findByOwnerNumber(studentNumber);
        return getWeeklySchedule(enrollmentLists);
    }

    public StringBuilder getWeeklySchedule(Student owner) {
        List<EnrollmentList> enrollmentLists = enrollmentListRepository.findByOwner(owner);
        return getWeeklySchedule(enrollmentLists);
    }

    private StringBuilder getWeeklySchedule(List<EnrollmentList> enrollmentLists) {
        StringBuilder weeklySchedule = new StringBuilder();
        enrollmentLists.stream().map(this::getWeeklySchedule).map(WeeklySchedule::getStringBuilder).forEach(weeklySchedule::append);
        return weeklySchedule;
    }

    private WeeklySchedule getWeeklySchedule(EnrollmentList enrollmentList) {
        List<DailySchedule> dailySchedules = getDailyScheduleList(enrollmentList.getSections());
        return new WeeklySchedule(enrollmentList.getOwner().getStudentNumber(),
                enrollmentList.getListName(), dailySchedules);
    }

    private List<DailySchedule> getDailyScheduleList(List<Section> sections) {
        List<DailySchedule> dailySchedules = new ArrayList<>();
        List<SectionSchedule> sectionSchedules;
        for (WeekDayEnum today : WeekDayEnum.values()) {
            sectionSchedules = new ArrayList<>();
            for (Section s : sections)
                for (ClassSchedule c : s.getClassScheduleList())
                    if (c.getWeekDay().equals(today))
                        sectionSchedules.add(s.getSectionSchedule(c));
            dailySchedules.add(new DailySchedule(today, sectionSchedules));
        }
        return dailySchedules;
    }

    public Map<Section, List<Student>> extractEnrollmentListsStructure(List<EnrollmentList> finalLists) {
        finalLists.removeIf(o -> o.checkEnrollmentRules().size() > 0);
        Map<Section, List<Student>> result = new HashMap<>();
        for (EnrollmentList enrollmentList : finalLists) {
            for (Section section : enrollmentList.getSections()) {
                if (!result.containsKey(section))
                    result.put(section, new ArrayList<>());
                result.get(section).add(enrollmentList.getOwner());
            }
        }
        return result;
    }

    public Set<Section> enrollStudents() {
        List<EnrollmentList> enrollmentLists = enrollmentListRepository.findByFinalListIsTrue();
        Map<Section, List<Student>> requests = extractEnrollmentListsStructure(enrollmentLists);
        requests.forEach((section, students) -> {
            section.enrollStudents(students);
            sectionRepository.save(section);
        });

        return requests.keySet();
    }
}
