package ru.nsu.astakhov.autodocs.integration.google.scanner;

import lombok.extern.slf4j.Slf4j;
import ru.nsu.astakhov.autodocs.model.Course;
import ru.nsu.astakhov.autodocs.model.EduProgram;
import ru.nsu.astakhov.autodocs.model.Specialization;
import ru.nsu.astakhov.autodocs.model.StudentDto;

import java.util.List;

import static ru.nsu.astakhov.autodocs.integration.google.scanner.ThesisColumnType.*;
import static ru.nsu.astakhov.autodocs.integration.google.scanner.SheetUtil.readCellAsString;

@Slf4j
public class ThesisStrategy implements SheetStrategy {
    @Override
    public String buildRange(Course course) {
        int c = course.getValue();
        String degree = c >= 3 ? "б." : "м.";
        return c + " курс " + degree;
    }

    @Override
    public StudentDto parseRow(List<Object> row, Course course) {
        return parseRowThesis(row, course);
    }

    @Override
    public boolean hasSpecialParsing(Course course) {
        return course == Course.THIRD;
    }

    @Override
    public StudentDto parseSpecialRow(List<Object> row, Course course) {
        return parseRowOnlyThirdCourseThesis(row);
    }

    private StudentDto parseRowThesis(List<Object> row, Course course) {
        if (row == null || row.isEmpty()) {
            return null;
        }

        // flag for scanning additional data about co-supervisors and thesis supervisors
        boolean isSpecial = course == Course.FOURTH || course == Course.SECOND;

        try {
            return StudentDto.builder()
                    .fullName(readCellAsString(row, FULL_NAME.getColumnNumber()))
                    .course(course)
                    .email(readCellAsString(row, EMAIL.getColumnNumber()))
                    .phoneNumber(readCellAsString(row, PHONE_NUMBER.getColumnNumber()))
                    .eduProgram(EduProgram.fromValue(readCellAsString(row, EDU_PROGRAM.getColumnNumber())))
                    .groupName(readCellAsString(row, GROUP_NAME.getColumnNumber()))
                    .specialization(Specialization.fromValue(readCellAsString(row, SPECIALIZATION.getColumnNumber())))
                    .orderOnApprovalTopic(readCellAsString(row, ORDER_ON_APPROVAL_TOPIC.getColumnNumber()))
                    .orderOnCorrectionTopic(readCellAsString(row, ORDER_ON_CORRECTION_TOPIC.getColumnNumber()))
                    .actualSupervisor(readCellAsString(row, ACTUAL_SUPERVISOR.getColumnNumber()))
                    .thesisCoSupervisor(readCellAsString(row, THESIS_CO_SUPERVISOR_NAME.getColumnNumber()))
                    .thesisConsultant(readCellAsString(row, THESIS_CONSULTANT_NAME.getColumnNumber()))
                    .thesisTopic(readCellAsString(row, THESIS_TOPIC.getColumnNumber()))
                    .reviewer(course.getValue() == 4 ? null : readCellAsString(row, REVIEWER.getColumnNumber()))
                    .thesisCoSupervisorDegree(!isSpecial ? null : readCellAsString(row, THESIS_CO_SUPERVISOR_DEGREE.getColumnNumber()))
                    .thesisCoSupervisorTitle(!isSpecial ? null : readCellAsString(row, THESIS_CO_SUPERVISOR_TITLE.getColumnNumber()))
                    .thesisCoSupervisorPositionAndJob(!isSpecial ? null : readCellAsString(row, THESIS_CO_SUPERVISOR_POSITION_AND_JOB.getColumnNumber()))
                    .build();
        }
        catch (IllegalArgumentException e) {
            // TODO: тут теряется ошибка, которую кидают ниже
            logger.info(e.getMessage());
            throw new IllegalArgumentException("Ошибка при парсинге строки: " + row, e);
        }
    }

    private StudentDto parseRowOnlyThirdCourseThesis(List<Object> row) {
        if (row == null || row.isEmpty()) {
            return null;
        }
        final int specialActualSupervisorColumnNumber = 7;

        try {
            return StudentDto.builder()
                    .fullName(readCellAsString(row, FULL_NAME.getColumnNumber()))
                    .course(Course.THIRD)
                    .email(readCellAsString(row, EMAIL.getColumnNumber()))
                    .phoneNumber(readCellAsString(row, PHONE_NUMBER.getColumnNumber()))
                    .eduProgram(EduProgram.fromValue(readCellAsString(row, EDU_PROGRAM.getColumnNumber())))
                    .groupName(readCellAsString(row, GROUP_NAME.getColumnNumber()))
                    .specialization(Specialization.fromValue(readCellAsString(row, SPECIALIZATION.getColumnNumber())))
                    .actualSupervisor(readCellAsString(row, specialActualSupervisorColumnNumber))
                    .build();
        }
        catch (Exception e) {
            // TODO: тут теряется ошибка, которую кидают ниже
            logger.info(e.getMessage());
            throw new IllegalArgumentException("Ошибка при парсинге строки: " + row, e);
        }
    }
}
