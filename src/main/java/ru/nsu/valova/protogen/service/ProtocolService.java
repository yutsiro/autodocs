package ru.nsu.valova.protogen.service;

import ru.nsu.astakhov.autodocs.model.Course;
import ru.nsu.astakhov.autodocs.model.Specialization;
import ru.nsu.astakhov.autodocs.model.StudentEntity;
import ru.nsu.astakhov.autodocs.repository.StudentRepository;
import ru.nsu.valova.protogen.config.AppSettings;
import ru.nsu.valova.protogen.config.QuestionConfigItem;
import ru.nsu.valova.protogen.config.QuestionsConfig;
import ru.nsu.valova.protogen.generator.ProtocolGenerator;
import ru.nsu.valova.protogen.handlers.QuestionHandler;
import ru.nsu.valova.protogen.handlers.QuestionHandlerFactory;
import ru.nsu.valova.protogen.model.ProtocolData;
import ru.nsu.valova.protogen.model.Student;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProtocolService {
    private final StudentRepository studentRepository;
    private final QuestionsConfig questionsConfig;
    private final AppSettings appSettings;
    private final QuestionHandlerFactory handlerFactory;
    private ProtocolData currentProtocolData;

    public ProtocolService(StudentRepository studentRepository,
                           QuestionsConfig questionsConfig,
                           AppSettings appSettings) {
        this.studentRepository = studentRepository;
        this.questionsConfig = questionsConfig;
        this.appSettings = appSettings;
        this.handlerFactory = new QuestionHandlerFactory();
    }

    public List<QuestionConfigItem> getAvailableQuestions() {
        return questionsConfig.getQuestions();
    }

    public List<Student> loadStudentsForQuestion(QuestionConfigItem config) throws IOException {
        System.out.println("=== loadStudentsForQuestion ===");
        System.out.println("Вопрос: " + config.getCode());
        System.out.println("Ожидаемый профиль: " + config.getEducationalProfile());
        System.out.println("Ожидаемый курс: " + config.getCourse());
        System.out.println("Ожидаемый тип практики: " + config.getPracticeTypeNominative());

        Course courseEnum = getCourseEnum(config.getCourse());

        Specialization specializationEnum = getSpecializationEnum(config.getEducationalProfile());

        if (specializationEnum == null) {
            System.err.println("Не удалось найти специализацию: " + config.getEducationalProfile());
            return new ArrayList<>();
        }

        List<StudentEntity> entities = studentRepository.findByCourseAndSpecialization(
                courseEnum,
                specializationEnum
        );

        System.out.println("Найдено студентов в репозитории: " + entities.size());

        List<StudentEntity> filteredEntities = entities.stream()
                .filter(s -> {
                    if (config.getPracticeTypeNominative() == null || config.getPracticeTypeNominative().isEmpty()) {
                        return true;
                    }
                    boolean match = s.getInternshipType() != null &&
                            s.getInternshipType().getValue().equals(config.getPracticeTypeNominative());
                    if (!match && s.getInternshipType() != null) {
                        System.out.println("  Не совпал тип практики: '" + s.getInternshipType().getValue() +
                                "' != '" + config.getPracticeTypeNominative() + "'");
                    }
                    return match;
                })
                .collect(Collectors.toList());

        System.out.println("Отфильтровано по практике: " + filteredEntities.size());

        List<Student> students = convertToMyModel(filteredEntities);

        System.out.println("Итоговое количество студентов: " + students.size());
        return students;
    }

    public void generateProtocol(String protocolNumber,
                                 List<QuestionConfigItem> selectedQuestions,
                                 String templatePath,
                                 String outputDirectory) throws IOException {

        ProtocolData data = new ProtocolData();
        data.setProtocolNumber(protocolNumber);

        LocalDate today = LocalDate.now();
        data.setDay(String.valueOf(today.getDayOfMonth()));
        data.setMonth(getMonthName(today.getMonthValue()));
        data.setYear(String.valueOf(today.getYear()));
        data.setChairman(appSettings.getChairmanFullName());
        data.setSecretary(appSettings.getSecretaryFullName());

        List<QuestionHandler> handlersWithStudents = new ArrayList<>();

        for (QuestionConfigItem config : selectedQuestions) {
            List<Student> students = loadStudentsForQuestion(config);

            if (students.isEmpty()) {
                throw new IOException("Нет данных для вопроса: " + config.getPracticeTypeNominative() +
                        " (профиль=" + config.getEducationalProfile() +
                        ", курс=" + config.getCourse() + ")");
            }

            handlersWithStudents.add(handlerFactory.createHandler(config, students));
            data.addQuestion(handlersWithStudents.get(handlersWithStudents.size() - 1));
        }

        String outputPath = outputDirectory + "/protocol_" +
                protocolNumber.replace("/", "-") + "_" +
                System.currentTimeMillis() + ".docx";

        ProtocolGenerator.generateProtocol(templatePath, outputPath, data);
        appSettings.incrementProtocolNumber();
    }

    public ProtocolDataPreview prepareProtocolData(String protocolNumber, List<QuestionConfigItem> selectedQuestions) throws IOException {
        ProtocolData data = new ProtocolData();
        data.setProtocolNumber(protocolNumber);

        LocalDate today = LocalDate.now();
        data.setDay(String.valueOf(today.getDayOfMonth()));
        data.setMonth(getMonthName(today.getMonthValue()));
        data.setYear(String.valueOf(today.getYear()));
        data.setChairman(appSettings.getChairmanFullName());
        data.setSecretary(appSettings.getSecretaryFullName());

        List<QuestionHandler> handlers = new ArrayList<>();
        for (QuestionConfigItem config : selectedQuestions) {
            List<Student> students = loadStudentsForQuestion(config);
            if (students.isEmpty()) {
                throw new IOException("Нет данных для вопроса: " + config.getPracticeTypeNominative());
            }
            QuestionHandler handler = handlerFactory.createHandler(config, students);
            handlers.add(handler);
            data.addQuestion(handler);
        }

        this.currentProtocolData = data;
        return new ProtocolDataPreview(data, handlers);
    }

    public void generateFinalProtocol(String outputDirectory, String templatePath) throws IOException {
        String outputPath = outputDirectory + "/protocol_" +
                currentProtocolData.getProtocolNumber().replace("/", "-") + "_" +
                System.currentTimeMillis() + ".docx";
        ProtocolGenerator.generateProtocol(templatePath, outputPath, currentProtocolData);
        appSettings.incrementProtocolNumber();
        currentProtocolData = null;
    }

    public static class ProtocolDataPreview {
        public final ProtocolData protocolData;
        public final List<QuestionHandler> handlers;

        public ProtocolDataPreview(ProtocolData protocolData, List<QuestionHandler> handlers) {
            this.protocolData = protocolData;
            this.handlers = handlers;
        }
    }

    public String generateProtocolNumber() {
        return appSettings.getLastProtocolNumber();
    }

    public AppSettings getAppSettings() {
        return appSettings;
    }

    private String getMonthName(int month) {
        String[] months = {"января", "февраля", "марта", "апреля", "мая", "июня",
                "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        return months[month - 1];
    }

    private Course getCourseEnum(int course) {
        switch (course) {
            case 1: return Course.FIRST;
            case 2: return Course.SECOND;
            case 3: return Course.THIRD;
            case 4: return Course.FOURTH;
            default: return null;
        }
    }

    private Specialization getSpecializationEnum(String profile) {
        if (profile == null) return null;

        if (profile.contains("Программная инженерия") || profile.contains("Программная инженерия и компьютерные науки")) {
            return Specialization.SOFTWARE_ENGINEERING_AND_CS;
        }
        if (profile.contains("Компьютерные науки и системотехника")) {
            return Specialization.CS_AND_SYSTEMS_ENGINEERING;
        }
        if (profile.contains("Технология разработки программных систем")) {
            return Specialization.SOFTWARE_SYSTEMS_DEVELOPMENT;
        }
        if (profile.contains("Искусственный интеллект") || profile.contains("Искусственный интеллект и Data Science")) {
            return Specialization.AI_AND_DATA_SCIENCE;
        }
        if (profile.contains("Интернет вещей")) {
            return Specialization.INTERNET_OF_THINGS;
        }

        System.err.println("Неизвестный профиль: " + profile);
        return null;
    }

    private List<Student> convertToMyModel(List<StudentEntity> entities) {
        List<Student> students = new ArrayList<>();

        for (StudentEntity entity : entities) {
            Student student = new Student();

            student.setFullName(entity.getFullName());
            student.setCourse(entity.getCourse() != null ? entity.getCourse().getValue() : 0);
            student.setGroup(entity.getGroupName());
            student.setEmail(entity.getEmail());

            if (entity.getEduProgram() != null) {
                student.setEducationalProgram(entity.getEduProgram().getValue());
            }
            if (entity.getSpecialization() != null) {
                student.setEducationalProfile(entity.getSpecialization().getValue());
            }

            if (entity.getInternshipType() != null) {
                student.setPracticeType(entity.getInternshipType().getValue());
            }

            if (entity.getFullOrganizationName() != null) {
                student.setPracticeBase(entity.getFullOrganizationName());
            }
            student.setFullPlaceOfInternship(entity.getFullPlaceOfInternship());
            student.setOrganizationName(entity.getOrganizationName());

            if (entity.getNSUSupervisor() != null) {
                student.setNsuSupervisorFullName(entity.getNSUSupervisor().name());
                student.setNsuSupervisorPosition(entity.getNSUSupervisor().position());
                student.setNsuSupervisorDegree(entity.getNSUSupervisor().degree());
                student.setNsuSupervisorAcademicTitle(entity.getNSUSupervisor().title());
            }

            if (entity.getOrganizationSupervisor() != null) {
                student.setInstituteSupervisorFullName(entity.getOrganizationSupervisor().name());
                student.setInstituteSupervisorPosition(entity.getOrganizationSupervisor().position());
                student.setInstituteSupervisorDegree(entity.getOrganizationSupervisor().degree());
                student.setInstituteSupervisorAcademicTitle(entity.getOrganizationSupervisor().title());
            }

            students.add(student);
        }

        return students;
    }

    private String getShortProfileName(String fullProfileName) {
        if (fullProfileName == null) return "";
        if (fullProfileName.contains("Программная инженерия")) return "ПИиКН";
        if (fullProfileName.contains("Компьютерные науки")) return "КНиС";
        if (fullProfileName.contains("Технология разработки")) return "ТРПС";
        if (fullProfileName.contains("Искусственный интеллект")) return "ИИиDS";
        if (fullProfileName.contains("Интернет вещей")) return "IoT";
        return fullProfileName;
    }
}
