package ru.nsu.astakhov.autodocs.document.generator;

import com.github.petrovich4j.Gender;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import ru.nsu.astakhov.autodocs.document.RussianWordDecliner;
import ru.nsu.astakhov.autodocs.document.PreparedTemplateInfo;
import ru.nsu.astakhov.autodocs.document.generator.table.ThesisAssignmentTableProcessor;
import ru.nsu.astakhov.autodocs.document.generator.table.ThesisFrontPageTableProcessor;
import ru.nsu.astakhov.autodocs.document.generator.table.ThesisSupervisorReviewTableProcessor;
import ru.nsu.astakhov.autodocs.exceptions.GenderResolutionException;
import ru.nsu.astakhov.autodocs.model.StudentDto;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;

import static java.util.Map.entry;

public class DocumentGenerator extends AbstractGenerator<StudentDto> {
    private final RussianWordDecliner russianWordDecliner;
    private final PreparedTemplateInfo preparedTemplateInfo;

    public DocumentGenerator(PreparedTemplateInfo preparedTemplateInfo) {
        this.russianWordDecliner = new RussianWordDecliner();
        this.preparedTemplateInfo = preparedTemplateInfo;

        initOutputDirectory();
    }

    private static final Map<String, Function<StudentDto, String>> RESOLVERS = Map.ofEntries(
            entry("fullname", StudentDto::fullName),
            entry("course", tempDto -> String.valueOf(tempDto.course().getValue())),
            entry("email", StudentDto::email),
            entry("phonenumber", StudentDto::phoneNumber),
            entry("eduprogram", tempDto -> tempDto.eduProgram().getValue()),
            entry("groupname", StudentDto::groupName),
            entry("specialization", tempDto -> tempDto.specialization().getValue()),
            entry("orderonapprovaltopic", StudentDto::orderOnApprovalTopic),
            entry("orderoncorrectiontopic", StudentDto::orderOnCorrectionTopic),
            entry("actualsupervisor", StudentDto::actualSupervisor),
            entry("thesiscosupervisor", StudentDto::thesisCoSupervisor),
            entry("thesisconsultant", StudentDto::thesisConsultant),
            entry("thesistopic", StudentDto::thesisTopic),
            entry("reviewer", StudentDto::reviewer),
            entry("internshiptype", tempDto -> tempDto.internshipType().getValue()),
            entry("thesissupervisor.name", tempDto -> tempDto.thesisSupervisor().name()),
            entry("thesissupervisor.position", tempDto -> tempDto.thesisSupervisor().position()),
            entry("thesissupervisor.degree", tempDto -> tempDto.thesisSupervisor().degree()),
            entry("thesissupervisor.title", tempDto -> tempDto.thesisSupervisor().title()),
            entry("fullorganizationname", StudentDto::fullOrganizationName),
            entry("nsusupervisor.name", tempDto -> tempDto.NSUSupervisor().name()),
            entry("nsusupervisor.position", tempDto -> tempDto.NSUSupervisor().position()),
            entry("nsusupervisor.degree", tempDto -> tempDto.NSUSupervisor().degree()),
            entry("nsusupervisor.title", tempDto -> tempDto.NSUSupervisor().title()),
            entry("organizationsupervisor.name", tempDto -> tempDto.organizationSupervisor().name()),
            entry("organizationsupervisor.position", tempDto -> tempDto.organizationSupervisor().position()),
            entry("organizationsupervisor.degree", tempDto -> tempDto.organizationSupervisor().degree()),
            entry("organizationsupervisor.title", tempDto -> tempDto.organizationSupervisor().title()),
            entry("administrativeactfromorganization", StudentDto::administrativeActFromOrganization),
            entry("fullplaceofinternship", StudentDto::fullPlaceOfInternship),
            entry("organizationname", StudentDto::organizationName)
    );

    private static final Map<String, Function<StudentDto, String>> RESOLVERS_RUS = Map.ofEntries(
            entry("полноеимя", StudentDto::fullName),
            entry("курс", tempDto -> String.valueOf(tempDto.course().getValue())),
            entry("почта", StudentDto::email),
            entry("телефон", StudentDto::phoneNumber),
            entry("образпрограмма", tempDto -> tempDto.eduProgram().getValue()),
            entry("группа", StudentDto::groupName),
            entry("специализация", tempDto -> tempDto.specialization().getValue()),
            entry("утверждениетемы", StudentDto::orderOnApprovalTopic),
            entry("корректировкатемы", StudentDto::orderOnCorrectionTopic),
            entry("фактический", StudentDto::actualSupervisor),
            entry("соруководительвкр.имя", StudentDto::thesisCoSupervisor),
            entry("консультантвкр", StudentDto::thesisConsultant),
            entry("темавкр", StudentDto::thesisTopic),
            entry("рецензент", StudentDto::reviewer),
            entry("видпрактики", tempDto -> tempDto.internshipType().getValue()),
            entry("руководительвкр.имя", tempDto -> tempDto.thesisSupervisor().name()),
            entry("руководительвкр.работангу", tempDto -> tempDto.thesisSupervisor().job()),
            entry("руководительвкр.должность", tempDto -> tempDto.thesisSupervisor().position()),
            entry("руководительвкр.степень", tempDto -> tempDto.thesisSupervisor().degree()),
            entry("руководительвкр.звание", tempDto -> tempDto.thesisSupervisor().title()),
            entry("полноеимяорганизации", StudentDto::fullOrganizationName),
            entry("руководительнгу.имя", tempDto -> tempDto.NSUSupervisor().name()),
            entry("руководительнгу.работа", tempDto -> tempDto.NSUSupervisor().job()),
            entry("руководительнгу.должность", tempDto -> tempDto.NSUSupervisor().position()),
            entry("руководительнгу.степень", tempDto -> tempDto.NSUSupervisor().degree()),
            entry("руководительнгу.звание", tempDto -> tempDto.NSUSupervisor().title()),
            entry("руководительорганизации.имя", tempDto -> tempDto.organizationSupervisor().name()),
            entry("руководительорганизации.работа", tempDto -> tempDto.organizationSupervisor().job()),
            entry("руководительорганизации.должность", tempDto -> tempDto.organizationSupervisor().position()),
            entry("руководительорганизации.степень", tempDto -> tempDto.organizationSupervisor().degree()),
            entry("руководительорганизации.звание", tempDto -> tempDto.organizationSupervisor().title()),
            entry("актоторганизации", StudentDto::administrativeActFromOrganization),
            entry("местопрактикиполностью", StudentDto::fullPlaceOfInternship),
            entry("наименованиеорганизации", StudentDto::organizationName),
            entry("датавыдачизаданияпрактики", StudentDto::dateOfPracticeAssignment),
            entry("соруководительвкр.степень", StudentDto::thesisCoSupervisorDegree),
            entry("соруководительвкр.звание", StudentDto::thesisCoSupervisorTitle),
            entry("соруководительвкр.должность.работангу", StudentDto::thesisCoSupervisorPositionAndJob)
    );

    private static final Map<String, BiFunction<StudentDto, RussianWordDecliner, String>> ADDITIONAL_RESOLVERS = Map.ofEntries(
            entry("genitivestudentform", (student, decliner) -> {
                Gender gender = student.gender();
                if (gender != null) {
                    return decliner.getGenitiveFormalStudentByGender(gender);
                }
                String[] nameParts = student.fullName().split(" ");
                if (nameParts.length != 3 || (gender = decliner.getGenderByPatronymic(nameParts[2])) == Gender.Both) {
                    throw new GenderResolutionException(student);
                }
                return decliner.getGenitiveFormalStudentByGender(gender);
            }),
            entry("genitivefullname", (student, decliner) -> {
                String fullName = student.fullName();
                Gender gender = student.gender();
                if (gender != null) {
                    return decliner.getFullNameInGenitiveCase(fullName, gender);
                }
                String[] nameParts = fullName.split(" ");
                if (nameParts.length != 3 || (gender = decliner.getGenderByPatronymic(nameParts[2])) == Gender.Both) {
                    throw new GenderResolutionException(student);
                }
                return decliner.getFullNameInGenitiveCase(fullName, gender);
            }),
            entry("studentform", (student, decliner) -> {
                Gender gender = student.gender();
                if (gender != null) {
                    return decliner.getFormalStudentByGender(gender);
                }
                String[] nameParts = student.fullName().split(" ");
                if (nameParts.length != 3 || (gender = decliner.getGenderByPatronymic(nameParts[2])) == Gender.Both) {
                    throw new GenderResolutionException(student);
                }
                return decliner.getFormalStudentByGender(gender);
            })
    );

    private static final Map<String, BiFunction<StudentDto, RussianWordDecliner, String>> ADDITIONAL_RESOLVERS_RUS = Map.ofEntries(
            entry("полобучающегося", (student, decliner) -> {
                Gender gender = student.gender();
                if (gender != null) {
                    return decliner.getGenitiveFormalStudentByGender(gender);
                }
                String[] nameParts = student.fullName().split(" ");
                if (nameParts.length != 3 || (gender = decliner.getGenderByPatronymic(nameParts[2])) == Gender.Both) {
                    throw new GenderResolutionException(student);
                }
                return decliner.getGenitiveFormalStudentByGender(gender);
            }),
            entry("полноеимяродительный", (student, decliner) -> {
                String fullName = student.fullName();
                Gender gender = student.gender();
                if (gender != null) {
                    return decliner.getFullNameInGenitiveCase(fullName, gender);
                }
                String[] nameParts = fullName.split(" ");
                if (nameParts.length != 3 || (gender = decliner.getGenderByPatronymic(nameParts[2])) == Gender.Both) {
                    throw new GenderResolutionException(student);
                }
                return decliner.getFullNameInGenitiveCase(fullName, gender);
            }),
            entry("полобучающийся", (student, decliner) -> {
                Gender gender = student.gender();
                if (gender != null) {
                    return decliner.getFormalStudentByGender(gender);
                }
                String[] nameParts = student.fullName().split(" ");
                if (nameParts.length != 3 || (gender = decliner.getGenderByPatronymic(nameParts[2])) == Gender.Both) {
                    throw new GenderResolutionException(student);
                }
                return decliner.getFormalStudentByGender(gender);
            }),
            entry("полстудент", (student, decliner) -> {
                Gender gender = student.gender();
                if (gender != null) {
                    return decliner.getCommonStudentByGender(gender);
                }
                String[] nameParts = student.fullName().split(" ");
                if (nameParts.length != 3 || (gender = decliner.getGenderByPatronymic(nameParts[2])) == Gender.Both) {
                    throw new GenderResolutionException(student);
                }
                return decliner.getCommonStudentByGender(gender);
            }),
            entry("иофамилиястудента", (student, decliner) ->
                    decliner.getAbbreviatedName(student.fullName())
            ),
            entry("полстудентом", (student, decliner) -> {
                Gender gender = student.gender();
                if (gender != null) {
                    return decliner.getInstrumentalCommonStudentByGender(gender);
                }
                String[] nameParts = student.fullName().split(" ");
                if (nameParts.length != 3 || (gender = decliner.getGenderByPatronymic(nameParts[2])) == Gender.Both) {
                    throw new GenderResolutionException(student);
                }
                return decliner.getInstrumentalCommonStudentByGender(gender);
            }),
            entry("полноеимятворительный", (student, decliner) -> {
                String fullName = student.fullName();
                Gender gender = student.gender();
                if (gender != null) {
                    return decliner.getFullNameInInstrumentalCase(fullName, gender);
                }
                String[] nameParts = fullName.split(" ");
                if (nameParts.length != 3 || (gender = decliner.getGenderByPatronymic(nameParts[2])) == Gender.Both) {
                    throw new GenderResolutionException(student);
                }
                return decliner.getFullNameInInstrumentalCase(fullName, gender);
            }),
            entry("руководительвкр.имякратко", (student, decliner) ->
                decliner.getAbbreviatedName2(student.thesisSupervisor().name())
            ),
            entry("полноеимякратко", (student, decliner) ->
                    decliner.getAbbreviatedName2(student.fullName())
            ),
            entry("соруководительвкр.имякратко", (student, decliner) ->
                    decliner.getAbbreviatedName2(student.thesisCoSupervisor())
            ),
            entry("полстуденту", (student, decliner) -> {
                Gender gender = student.gender();
                if (gender != null) {
                    return decliner.getDativeStudentByGender(gender);
                }
                String[] nameParts = student.fullName().split(" ");
                if (nameParts.length != 3 || (gender = decliner.getGenderByPatronymic(nameParts[2])) == Gender.Both) {
                    throw new GenderResolutionException(student);
                }
                return decliner.getDativeStudentByGender(gender);
            }),
            entry("полноеимядательный", (student, decliner) -> {
                String fullName = student.fullName();
                Gender gender = student.gender();
                if (gender != null) {
                    return decliner.getFullNameInDativeCase(fullName, gender);
                }
                String[] nameParts = fullName.split(" ");
                if (nameParts.length != 3 || (gender = decliner.getGenderByPatronymic(nameParts[2])) == Gender.Both) {
                    throw new GenderResolutionException(student);
                }
                return decliner.getFullNameInDativeCase(fullName, gender);
            }),
            entry("общаядатаподписи", (student, decliner) -> {
                String orderOnCorrection = student.orderOnCorrectionTopic();
                String date;
                if (orderOnCorrection != null &&  !orderOnCorrection.isBlank()) {
                    date = orderOnCorrection.split(" ")[2];
                }
                else {
                    date = student.orderOnApprovalTopic().split(" ")[2];
                }
                return date;
            }),
            entry("корректировкатемыеслиесть", (student, decliner) -> {
                String orderOnCorrection = student.orderOnCorrectionTopic();
                if (orderOnCorrection == null || orderOnCorrection.isBlank()) {
                    return ".";
                }
                else {
                    return ", скорректирована распоряжением проректора по учебной работе " + orderOnCorrection;
                }
            })
    );

    private void initOutputDirectory() {
        try {
            Files.createDirectories(Paths.get(preparedTemplateInfo.documentDir()));
        }
        catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию для документов", e);
        }
    }

    public void generate(StudentDto dto) {
        String saveName = dto.fullName().replace(' ', '_') + '_' + preparedTemplateInfo.fileName() + ".docx";
        Path outputFilePath = Paths.get(preparedTemplateInfo.documentDir(), saveName);
        generateDocument(outputFilePath, dto);
    }

    private void generateDocument(Path outputFilePath, StudentDto studentDto) {
        try (InputStream inputStream = new FileInputStream(preparedTemplateInfo.templatePath())) {

            try (XWPFDocument document = new XWPFDocument(inputStream);
                 FileOutputStream out = new FileOutputStream(outputFilePath.toFile())) {
                super.processDocument(document, studentDto);
                document.write(out);
            }

        }
        catch (IOException e) {
            throw new RuntimeException("Ошибка при генерации документа: " + outputFilePath, e);
        }
    }

    @Override
    protected void applyReplacement(XWPFRun run, Matcher matcher, StudentDto studentDto, StringBuilder result) {
        String key = matcher.group(1); // ключ без $()
        applyDefaultReplacement(run, matcher, studentDto, key, result);
    }

    @Override
    protected void applyTableReplacement(XWPFRun run, XWPFTable table, Matcher matcher, StudentDto studentDto, StringBuilder result) {
        String key = matcher.group(1); // ключ без $()
        boolean hasCoSupervisor = studentDto.thesisCoSupervisor() != null && !studentDto.thesisCoSupervisor().isBlank();
        boolean hasThesisConsultant = studentDto.thesisConsultant() != null && !studentDto.thesisConsultant().isBlank();

        switch (key) {
            case "соруководительТитульник" -> {
                ThesisFrontPageTableProcessor tableProcessor = new ThesisFrontPageTableProcessor();
                tableProcessor.removeMarkerRow(table, key);
                if (hasCoSupervisor) {
                    tableProcessor.addCoSupervisor(table);
                }
            }
            case "соруководительЗадание" -> {
                ThesisAssignmentTableProcessor tableProcessor = new ThesisAssignmentTableProcessor();
                tableProcessor.removeMarkerRow(table, key);
                if (hasCoSupervisor) {
                    tableProcessor.addCoSupervisor(table);
                }
            }
            case "консультантВКРЕслиЕсть" -> {
                ThesisAssignmentTableProcessor tableProcessor = new ThesisAssignmentTableProcessor();
                tableProcessor.removeMarkerRow(table, key);
                if (hasThesisConsultant) {
                    tableProcessor.addThesisConsultant(table);
                }
            }
            case "соруководительОтзыв" -> {
                ThesisSupervisorReviewTableProcessor tableProcessor = new ThesisSupervisorReviewTableProcessor();
                tableProcessor.removeMarkerRow(table, key);
                if (hasCoSupervisor) {
                    tableProcessor.addCoSupervisor(table);
                }
            }
            default -> applyDefaultReplacement(run, matcher, studentDto, key, result);
        }
    }

    private void applyDefaultReplacement(
            XWPFRun run,
            Matcher matcher,
            StudentDto studentDto,
            String key,
            StringBuilder result) {
        String normalizedKey = key.toLowerCase(Locale.ROOT);
        Function<StudentDto, String> resolver = RESOLVERS.get(normalizedKey);
        String value;
        if (resolver == null) {
            resolver = RESOLVERS_RUS.get(normalizedKey);
        }

        if (resolver != null) {
            value = resolver.apply(studentDto);
        }
        else {
            BiFunction<StudentDto, RussianWordDecliner, String> additionalResolver = ADDITIONAL_RESOLVERS.get(normalizedKey);
            if (additionalResolver == null) {
                additionalResolver = ADDITIONAL_RESOLVERS_RUS.get(normalizedKey);
            }
            if (additionalResolver == null) {
                throw new RuntimeException("Resolver is null by key: " + key);
            }
            value = additionalResolver.apply(studentDto, russianWordDecliner);
        }
        // TODO: пометить плейсхолдеры в документах "заявление на практику" жёлтым
        // TODO: исправить уведомление "разрешение конфликтов" при генерации. Оно не исчезает
        if (value == null) {
            throw new IllegalStateException("Value is empty: " + key + ":" + studentDto.fullName());
        }
        if (!value.isBlank()) {
            run.setTextHighlightColor("white");
        }
        else if (key.equals("administrativeActFromOrganization")) {
            value = "\"__\" __________ 20__ г. №______";
        }
        else if (key.equals("thesisSupervisor.name") ||
                key.equals("thesisSupervisor.position") ||
                key.equals("thesisSupervisor.degree")) {
            value = "_____________";
            run.setTextHighlightColor("white");
        }

        boolean replaceWithValue = !value.isBlank()
                || normalizedKey.equals("руководительвкр.степень")
                || normalizedKey.equals("руководительвкр.звание")
                || normalizedKey.equals("руководительнгу.степень")
                || normalizedKey.equals("руководительнгу.звание")
                || normalizedKey.equals("руководительорганизации.степень")
                || normalizedKey.equals("руководительорганизации.звание")
                || normalizedKey.equals("соруководительвкр.степень")
                || normalizedKey.equals("соруководительвкр.звание")
                || normalizedKey.equals("рецензент");

        if (normalizedKey.equals("руководительвкр.звание") && value.isBlank()
        || normalizedKey.equals("рецензент") && value.isBlank()
        || normalizedKey.equals("руководительвкр.степень") && value.isBlank()) {
            value = " ";
        }

        if (replaceWithValue) {
            matcher.appendReplacement(result, Matcher.quoteReplacement(value));
        }
        else {
            matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(0)));
        }
    }
}