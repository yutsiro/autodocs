package ru.nsu.astakhov.autodocs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nsu.astakhov.autodocs.document.PreparedTemplateInfo;
import ru.nsu.astakhov.autodocs.document.generator.DocumentGenerator;
import ru.nsu.astakhov.autodocs.exceptions.GenderResolutionException;
import ru.nsu.astakhov.autodocs.integration.google.scanner.SheetScanner;
import ru.nsu.astakhov.autodocs.model.Course;
import ru.nsu.astakhov.autodocs.model.Specialization;
import ru.nsu.astakhov.autodocs.model.StudentDto;
import ru.nsu.astakhov.autodocs.model.StudentEntity;
import ru.nsu.astakhov.autodocs.model.Supervisor;
import ru.nsu.astakhov.autodocs.model.WarningList;
import ru.nsu.astakhov.autodocs.model.WorkType;
import ru.nsu.astakhov.autodocs.repository.StudentRepository;
import ru.nsu.astakhov.autodocs.mapper.StudentMapper;
import ru.nsu.astakhov.autodocs.ui.controller.Conflict;
import ru.nsu.astakhov.autodocs.ui.controller.FieldConflict;
import ru.nsu.astakhov.autodocs.ui.controller.GenderConflict;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudentService {
    private final StudentRepository repository;
    private final SheetScanner sheetScanner;
    private final WarningList warningList;
    private volatile boolean updateInProgress = false;
    private final Object updateLock = new Object();

    public List<FieldConflict> startUpdate() {
        synchronized (updateLock) {
            logger.info("Starting student data update");
            if (updateInProgress) {
                logger.error("Already updating student data");
                throw new IllegalStateException("Обновление уже выполняется");
            }
            updateInProgress = true;

            try {
                logger.info("Clearing existing student data");
                clearAllData();
                logger.info("Scanning internship lists");
                scanInternshipLists();
                logger.info("Scanning thesis lists");
                return scanThesisLists();
            }
            catch (Exception e) {
                logger.error("Error during student data update: {}", e.getMessage());
                updateInProgress = false;
                throw e;
            }
        }
    }

    public List<StudentEntity> saveConflictingEntities(List<? extends Conflict> conflicts) {
        List<StudentEntity> entities = conflicts.stream().map(Conflict::getEntity).toList();
        return repository.saveAll(entities);
    }

    public void finishUpdate(List<FieldConflict> resolvedCollisions) {
        synchronized (updateLock) {
        logger.info("Finishing student data update");
        if (resolvedCollisions == null || resolvedCollisions.isEmpty()) {
            updateInProgress = false;
            return;
        }

        saveConflictingEntities(resolvedCollisions);
        updateInProgress = false;
        }
    }

    public List<StudentDto> getStudentsByGenerator(PreparedTemplateInfo preparedTemplateInfo) {
        Course course = preparedTemplateInfo.getCourse();
        Specialization specialization = preparedTemplateInfo.specialization();

        return getStudentsByCourseAndSpecialization(course, specialization);
    }

    public List<GenderConflict> generateStudents(List<StudentDto> studentDtos, PreparedTemplateInfo preparedTemplateInfo) {
        Course course = preparedTemplateInfo.getCourse();
        Specialization specialization = preparedTemplateInfo.specialization();

        DocumentGenerator generator = new DocumentGenerator(preparedTemplateInfo);

        List<GenderConflict> conflicts = new ArrayList<>();

        for (StudentDto dto : studentDtos) {
            if (dto.course() == course && dto.specialization() == specialization) {
                try {
                    generator.generate(dto);
                }
                catch (GenderResolutionException e) {
                    conflicts.add(new GenderConflict(StudentMapper.toEntity(dto)));
                }
            }
        }
        return conflicts;
    }

    private void clearAllData() {
        warningList.clear();
        repository.deleteAll();
    }

    private void scanInternshipLists() {
        List<StudentDto> studentDtos = sheetScanner.readAllInternshipLists();
        createStudents(studentDtos);
    }

    private List<FieldConflict> scanThesisLists() {
        List<StudentDto> studentDtos = sheetScanner.readAllThesisLists();
        return updateStudents(studentDtos);
    }

    private List<StudentDto> getStudentsByCourseAndSpecialization(Course course, Specialization specialization) {
        List<StudentEntity> entities = repository.findByCourseAndSpecialization(course, specialization);
        return StudentMapper.listToDto(entities);
    }

    private void createStudents(List<StudentDto> studentDtos) {
        logger.info("Creating new students with length: {}", studentDtos.size());

        List<StudentDto> validDtos = new ArrayList<>();
        for (StudentDto dto : studentDtos) {
            if (isNullOrBlank(dto.fullName())) {
                logger.error("Error: Student name must be initialized on creation");
                continue;
            }
            if (dto.id() != null) {
                logger.error("Error: Student id must be null when creating a new student");
                checkInternshipDto(dto);
                continue;
            }
            checkInternshipDto(dto);
            validDtos.add(dto);
        }

        List<StudentEntity> entities = StudentMapper.listToEntity(validDtos);

        List<StudentEntity> savedEntities = repository.saveAll(entities);
        logger.info("Students created successfully with length: {}", savedEntities.size());
    }

    private void checkInternshipDto(StudentDto dto) {
        final String studentName = dto.fullName();

        notifyIfStringFieldMissing(WorkType.INTERNSHIP, studentName, dto.email(), "почта");
        notifyIfObjectFieldMissing(WorkType.INTERNSHIP, studentName, dto.eduProgram(), "образовательная программа");
        notifyIfStringFieldMissing(WorkType.INTERNSHIP, studentName, dto.groupName(), "группа");
        notifyIfObjectFieldMissing(WorkType.INTERNSHIP, studentName, dto.specialization(), "профиль обучения");
        notifyIfStringFieldMissing(WorkType.INTERNSHIP, studentName, dto.actualSupervisor(), "фактический руководитель");
        notifyIfObjectFieldMissing(WorkType.INTERNSHIP, studentName, dto.internshipType(), "вид практики");

        notifyIfStringFieldMissing(WorkType.INTERNSHIP, studentName, dto.fullOrganizationName(), "название организации");

        notifyIfStringFieldMissing(WorkType.INTERNSHIP, studentName, dto.administrativeActFromOrganization(), "распорядительный акт");
        notifyIfStringFieldMissing(WorkType.INTERNSHIP, studentName, dto.fullPlaceOfInternship(), "место практики");
        notifyIfStringFieldMissing(WorkType.INTERNSHIP, studentName, dto.organizationName(), "наименование организации");

        if (dto.course().getValue() <= 2) {
            checkInternshipSupervisor(dto.thesisSupervisor(), dto.fullName());
            checkInternshipSupervisor(dto.NSUSupervisor(), dto.fullName());
            checkInternshipSupervisor(dto.organizationSupervisor(), dto.fullName());
        }
    }

    private void notifyIfStringFieldMissing(WorkType type, String student, String value, String fieldName) {
        if (isNullOrBlank(value)) {
            warningList.addWarning(type, student, fieldName);
        }
    }

    private void notifyIfObjectFieldMissing(WorkType type, String student, Object value, String fieldName) {
        if (value == null) {
            warningList.addWarning(type, student, fieldName);
        }
    }

    private void checkInternshipSupervisor(Supervisor supervisor, String studentName) {
        notifyIfStringFieldMissing(WorkType.INTERNSHIP, studentName, supervisor.name(), "имя руководителя");
        notifyIfStringFieldMissing(WorkType.INTERNSHIP, studentName, supervisor.job(), "место работы руководителя");
        notifyIfStringFieldMissing(WorkType.INTERNSHIP, studentName, supervisor.position(), "должность руководителя");
        notifyIfStringFieldMissing(WorkType.INTERNSHIP, studentName, supervisor.degree(), "учёная степень руководителя");
        notifyIfStringFieldMissing(WorkType.INTERNSHIP, studentName, supervisor.title(), "учёное звание руководителя");
    }

    private List<FieldConflict> updateStudents(List<StudentDto> studentDtos) {
        logger.info("Updating students with length: {}", studentDtos.size());

        List<StudentDto> validDtos = new ArrayList<>();
        for (StudentDto dto : studentDtos) {
            if (isNullOrBlank(dto.fullName())) {
                logger.error("Error: Student name must be initialized when updating");
                continue;
            }
            if (dto.id() != null) {
                logger.error("Error: Student id must be null when updating student");
                checkThesisDto(dto);
                continue;
            }
            checkThesisDto(dto);
            validDtos.add(dto);
        }

        List<StudentEntity> existingEntities = repository.findAll();
        Map<String, StudentEntity> existingByFullName = existingEntities.stream()
                .collect(Collectors.toMap(StudentEntity::getFullName, Function.identity()));

        List<StudentEntity> entitiesToSave = new ArrayList<>();
        List<FieldConflict> collisions = new ArrayList<>();

        for (StudentDto dto : validDtos) {
            StudentEntity entity = existingByFullName.get(dto.fullName());

            if (entity == null) {
                entity = StudentMapper.toEntity(dto);
            }
            else {
                checkCollision(entity, dto, collisions);
                mergeFromThesis(entity, dto); // TODO: временно мержим всё (плохо) из ВКР, но коллизии переопределим позже
            }
            entitiesToSave.add(entity);
        }

        List<StudentEntity> savedEntity = repository.saveAll(entitiesToSave);
        logger.info("Students updated successfully with length: {}", savedEntity.size());

        return collisions;
    }

    private void checkCollision(StudentEntity entity, StudentDto dto, List<FieldConflict> collisions) {
        checkFieldCollision(entity.getEmail(), dto.email(), entity::setEmail, entity, "почта", collisions);
        checkFieldCollision(entity.getEduProgram().getValue(), dto.eduProgram().getValue(), entity::setEduProgram, entity, "образовательная программа", collisions);
        checkFieldCollision(entity.getGroupName(), dto.groupName(), entity::setGroupName, entity, "группа", collisions);
        checkFieldCollision(entity.getSpecialization().getValue(), dto.specialization().getValue(), entity::setSpecialization, entity, "профиль обучения", collisions);
        checkFieldCollision(entity.getActualSupervisor(), dto.actualSupervisor(), entity::setActualSupervisor, entity, "фактический руководитель", collisions);
    }

    private void checkFieldCollision(
            String entityValue,
            String dtoValue,
            Consumer<String> entitySetter,
            StudentEntity entity,
            String fieldName,
            List<FieldConflict> collisions
    ) {
        if (!Objects.equals(entityValue, dtoValue)) {
            collisions.add(new FieldConflict(
                    entitySetter,
                    entity,
                    fieldName,
                    entityValue,
                    dtoValue
            ));
        }
    }

    private void checkThesisDto(StudentDto dto) {
        final String studentName = dto.fullName();

        notifyIfStringFieldMissing(WorkType.THESIS, studentName, dto.email(), "почта");
        notifyIfStringFieldMissing(WorkType.THESIS, studentName, dto.phoneNumber(), "телефон");
        notifyIfObjectFieldMissing(WorkType.THESIS, studentName, dto.eduProgram(), "образовательная программа");
        notifyIfStringFieldMissing(WorkType.THESIS, studentName, dto.groupName(), "группа");
        notifyIfObjectFieldMissing(WorkType.THESIS, studentName, dto.specialization(), "профиль обучения");
        notifyIfStringFieldMissing(WorkType.THESIS, studentName, dto.actualSupervisor(), "фактический руководитель");

        if (dto.course() != Course.THIRD) {
            notifyIfStringFieldMissing(WorkType.THESIS, studentName, dto.orderOnApprovalTopic(), "распоряжение об утверждении");
            notifyIfStringFieldMissing(WorkType.THESIS, studentName, dto.orderOnCorrectionTopic(), "распоряжение о корректировке");
            notifyIfStringFieldMissing(WorkType.THESIS, studentName, dto.thesisCoSupervisor(), "соруководитель");
            notifyIfStringFieldMissing(WorkType.THESIS, studentName, dto.thesisConsultant(), "консультант");
            notifyIfStringFieldMissing(WorkType.THESIS, studentName, dto.thesisTopic(), "тема ВКР");

            if (dto.course() != Course.FOURTH) {
                notifyIfStringFieldMissing(WorkType.THESIS, studentName, dto.reviewer(), "рецензент");
            }
        }
    }

    private void mergeFromThesis(StudentEntity entity, StudentDto dto) {
        entity.setPhoneNumber(dto.phoneNumber());
        entity.setOrderOnApprovalTopic(dto.orderOnApprovalTopic());
        entity.setOrderOnCorrectionTopic(dto.orderOnCorrectionTopic());
        entity.setThesisCoSupervisor(dto.thesisCoSupervisor());
        entity.setThesisConsultant(dto.thesisConsultant());
        entity.setThesisTopic(dto.thesisTopic());
        entity.setReviewer(dto.reviewer());
        entity.setThesisCoSupervisorDegree(dto.thesisCoSupervisorDegree());
        entity.setThesisCoSupervisorTitle(dto.thesisCoSupervisorTitle());
        entity.setThesisCoSupervisorPositionAndJob(dto.thesisCoSupervisorPositionAndJob());
    }

    private boolean isNullOrBlank(String field) {
        return field == null || field.isBlank();
    }
}
