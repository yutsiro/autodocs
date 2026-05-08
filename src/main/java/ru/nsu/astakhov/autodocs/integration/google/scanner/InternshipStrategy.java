package ru.nsu.astakhov.autodocs.integration.google.scanner;

import lombok.extern.slf4j.Slf4j;
import ru.nsu.astakhov.autodocs.model.Course;
import ru.nsu.astakhov.autodocs.model.EduProgram;
import ru.nsu.astakhov.autodocs.model.InternshipType;
import ru.nsu.astakhov.autodocs.model.Specialization;
import ru.nsu.astakhov.autodocs.model.StudentDto;
import ru.nsu.astakhov.autodocs.model.Supervisor;

import java.util.List;

import static ru.nsu.astakhov.autodocs.integration.google.scanner.InternshipColumnType.*;
import static ru.nsu.astakhov.autodocs.integration.google.scanner.SheetUtil.readCellAsString;

@Slf4j
public class InternshipStrategy implements SheetStrategy {
    @Override
    public String buildRange(Course course) {
        int c = course.getValue();
        String degree = c >= 3 ? "бак." : "маг.";
        return "Пр " + c + " к. " + degree;
    }

    @Override
    public StudentDto parseRow(List<Object> row, Course course) {
        return parseRowFromInternship(row, course);
    }

    private StudentDto parseRowFromInternship(List<Object> row, Course course) {
        if (row == null || row.isEmpty()) {
            return null;
        }

        try {
            return StudentDto.builder()
                    .fullName(readCellAsString(row, FULL_NAME.getColumnNumber()))
                    .course(course)
                    .email(readCellAsString(row, EMAIL.getColumnNumber()))
                    .eduProgram(EduProgram.fromValue(readCellAsString(row, EDU_PROGRAM.getColumnNumber())))
                    .groupName(readCellAsString(row, GROUP_NAME.getColumnNumber()))
                    .specialization(Specialization.fromValue(readCellAsString(row, SPECIALIZATION.getColumnNumber())))
                    .actualSupervisor(readCellAsString(row, ACTUAL_SUPERVISOR.getColumnNumber()))
                    .internshipType(InternshipType.fromValue(readCellAsString(row, INTERNSHIP_TYPE.getColumnNumber())))
                    .thesisSupervisor(new Supervisor(
                            readCellAsString(row, THESIS_SUPERVISOR_NAME.getColumnNumber()),
                            readCellAsString(row, THESIS_SUPERVISOR_JOB.getColumnNumber()),
                            readCellAsString(row, THESIS_SUPERVISOR_POSITION.getColumnNumber()),
                            readCellAsString(row, THESIS_SUPERVISOR_DEGREE.getColumnNumber()),
                            readCellAsString(row, THESIS_SUPERVISOR_TITLE.getColumnNumber())
                    ))
                    .fullOrganizationName(readCellAsString(row, FULL_ORGANIZATION_NAME.getColumnNumber()))
                    .NSUSupervisor(new Supervisor(
                            readCellAsString(row, NSU_SUPERVISOR_NAME.getColumnNumber()),
                            readCellAsString(row, NSU_SUPERVISOR_JOB.getColumnNumber()),
                            readCellAsString(row, NSU_SUPERVISOR_POSITION.getColumnNumber()),
                            readCellAsString(row, NSU_SUPERVISOR_DEGREE.getColumnNumber()),
                            readCellAsString(row, NSU_SUPERVISOR_TITLE.getColumnNumber())
                    ))
                    .organizationSupervisor(new Supervisor(
                            readCellAsString(row, ORGANIZATION_SUPERVISOR_NAME.getColumnNumber()),
                            readCellAsString(row, ORGANIZATION_SUPERVISOR_JOB.getColumnNumber()),
                            readCellAsString(row, ORGANIZATION_SUPERVISOR_POSITION.getColumnNumber()),
                            readCellAsString(row, ORGANIZATION_SUPERVISOR_DEGREE.getColumnNumber()),
                            readCellAsString(row, ORGANIZATION_SUPERVISOR_TITLE.getColumnNumber())
                    ))
                    .administrativeActFromOrganization(readCellAsString(row, ADMINISTRATIVE_ACT_FROM_ORGANIZATION.getColumnNumber()))
                    .fullPlaceOfInternship(readCellAsString(row, FULL_PLACE_OF_INTERNSHIP.getColumnNumber()))
                    .organizationName(readCellAsString(row, ORGANIZATION_NAME.getColumnNumber()))
                    .dateOfPracticeAssignment(readCellAsString(row, DATE_OF_PRACTICE_ASSIGNMENT.getColumnNumber()))
                    .build();
        }
        catch (Exception e) {
            // TODO: тут теряется ошибка, которую кидают ниже
            logger.info(e.getMessage());
            throw new IllegalArgumentException("Ошибка при парсинге строки: " + row, e);
        }
    }
}
